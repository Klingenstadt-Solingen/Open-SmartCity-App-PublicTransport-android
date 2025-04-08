package de.osca.android.public_transport.presentation.public_transport

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import de.osca.android.essentials.presentation.base.BaseViewModel
import de.osca.android.essentials.utils.extensions.displayContent
import de.osca.android.essentials.utils.extensions.resetWith
import de.osca.android.networkservice.utils.RequestHandler
import de.osca.android.public_transport.data.PublicTransportApiService
import de.osca.android.public_transport.domain.entity.RequestBodyForTrip
import de.osca.android.public_transport.domain.entity.StopsResponse
import de.osca.android.public_transport.domain.entity.TripBody
import de.osca.android.public_transport.domain.entity.TripStopId
import de.osca.android.public_transport.domain.entity.TripTempData
import de.osca.android.public_transport.domain.entity.nearby.StopsNearbyResponse
import de.osca.android.public_transport.domain.entity.nearby.StopsNearbyWrapper
import de.osca.android.public_transport.presentation.args.PublicTransportDesignArgs
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class PublicTransportViewModel
    @Inject
    constructor(
        val designArgs: PublicTransportDesignArgs,
        private val apiService: PublicTransportApiService,
        private val requestHandler: RequestHandler,
    ) : BaseViewModel() {
        val myStops: MutableList<StopsResponse> = mutableStateListOf()
        val nearbyStops: MutableList<StopsNearbyResponse> = mutableStateListOf()
        val originStop = mutableStateOf<StopsResponse?>(null)
        val destinationStop = mutableStateOf<StopsResponse?>(null)
        val checkedStateOrigin = mutableStateOf<Boolean>(true)
        val screenContentLoading = mutableStateOf(true)

        /**
         * call this function to initialize route (with date).
         * it sets the screen to loading, fetches the data from parse and when
         * it finished successful then displays the content and when an error
         * occurred it displays an message screen
         */
        fun initializeRouteDate(
            from: String,
            to: String,
            dateTime: LocalDateTime,
        ) {
            fetchTripsForDate(from, to, dateTime)
        }

        fun initializeRouteDetails() {
            wrapperState.displayContent()
        }

        /**
         * fetches all stops from parse and when successfully loaded then
         * displays the content
         */
        fun fetchStops(stopName: String) =
            launchDataLoad {
                val result =
                    requestHandler.makeRequest {
                        apiService.getStops(query = stopName)
                    }?.filter { it.id.isNotEmpty() } ?: emptyList()

                myStops.resetWith(result)

                wrapperState.displayContent()
            }

        fun fetchStopsById(
            fromId: String?,
            toId: String?,
            dateTime: LocalDateTime,
        ) = launchDataLoad {
            if (fromId != null) {
                originStop.value =
                    requestHandler.makeRequest {
                        apiService.getStopsByIds(ids = listOf(fromId))
                    }?.first()
            }

            if (toId != null) {
                destinationStop.value =
                    requestHandler.makeRequest {
                        apiService.getStopsByIds(ids = listOf(toId))
                    }?.first()
            }
            wrapperState.displayContent()
            if (fromId != null && toId != null) {
                screenContentLoading.value = true
                TripTempData.routeStopsData.clear()
                initializeRouteDate(
                    fromId,
                    toId,
                    dateTime,
                )
            }
            screenContentLoading.value = false
        }

        fun fetchStopsNearBy(location: LatLng?) =
            launchDataLoad {
                val result =
                    requestHandler.makeRequest {
                        apiService.getStopsNearBy(
                            lat = location?.latitude.toString(),
                            lon = location?.longitude.toString(),
                        )
                    } ?: StopsNearbyWrapper()
                nearbyStops.resetWith(result.stops)
            }

        fun fetchTripsForDate(
            from: String,
            to: String,
            dateTime: LocalDateTime,
        ) = launchDataLoad {
            val zdt: ZonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault())
            val utcDateTime = zdt.minusSeconds(zdt.offset.totalSeconds.toLong()).toLocalDateTime()
            val newUTC = utcDateTime.toString().split(".")[0]
            val type = if (checkedStateOrigin.value) "dep" else "arr"
            val tripBody = TripBody(type, "$newUTC+00:00", count = null, tz = TimeZone.getDefault().id)
            val stopsRequestData = RequestBodyForTrip(TripStopId(from), TripStopId(to), tripBody)
            val result =
                requestHandler.makeRequest { apiService.getPtTrip(stopsRequestData) } ?: emptyList()

            TripTempData.routeStopsData.resetWith(result)
        }
    }
