package de.osca.android.public_transport.domain.entity.trip

import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.utils.extensions.safeLet
import de.osca.android.essentials.utils.extensions.toLocalDateTime
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Parcelize
data class Trip(
    @SerializedName("duration")
    var durationInSeconds: Int = 0,
    @SerializedName("isRelatime")
    var isRealtime: Boolean = false,
    @SerializedName("origin")
    var origin: TripStop? = TripStop(),
    @SerializedName("destination")
    var destination: TripStop? = TripStop(),
    @SerializedName("transportation")
    var transportation: Transportation? = Transportation(),
    @SerializedName("hints")
    var hints: List<String> = emptyList(),
    @SerializedName("stopSequence")
    var stopSequence: List<TripStop> = emptyList(),
    @SerializedName("coords")
    var coords: List<List<Double>> = emptyList(),
    @SerializedName("interchange")
    var interchange: TripInterchange? = null
) : Parcelable {
    val durationInMinutes get() = durationInSeconds / 60

    val isByTransport: Boolean
        get() {
            return transportation?.product?.name != TRANSPORTATION_FOOTPATH && transportation?.number != null
        }

    fun estimatedDepartureTime(): LocalDateTime {
        val zdt: ZonedDateTime = ZonedDateTime.of(origin?.estimatedDeparture, ZoneId.systemDefault())
        return zdt.plusSeconds(zdt.offset.totalSeconds.toLong()).toLocalDateTime()
    }

    fun plannedDepartureTime(): LocalDateTime {
        val zdt: ZonedDateTime = ZonedDateTime.of(origin?.plannedDeparture, ZoneId.systemDefault())
        return zdt.plusSeconds(zdt.offset.totalSeconds.toLong()).toLocalDateTime()
    }

    val estimatedArrivalTime get() = estimatedDepartureTime().plusSeconds(durationInSeconds.toLong())
    val plannedArrivalTime get() = plannedDepartureTime().plusSeconds(durationInSeconds.toLong())


    val coordinates: List<Coordinates>
        get() = coords.mapNotNull {
            safeLet(it.getOrNull(index = 0), it.getOrNull(index = 1)) { lat, lon ->
                Coordinates(latitude = lat, longitude = lon)
            }
        }

    companion object {
        const val TRANSPORTATION_FOOTPATH = "footpath"
    }
}