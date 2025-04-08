package de.osca.android.public_transport.presentation.route_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.*
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.RootContainer
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.presentation.component.topbar.ScreenTopBar
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.essentials.utils.extensions.getBounds
import de.osca.android.public_transport.R
import de.osca.android.public_transport.domain.entity.RouteResponse
import de.osca.android.public_transport.presentation.component.*
import de.osca.android.public_transport.presentation.public_transport.PublicTransportViewModel
import kotlinx.coroutines.launch

/**
 *
 */
@Composable
fun TransportDetailsScreen(
    navController: NavController,
    publicTransportViewModel: PublicTransportViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = publicTransportViewModel.defaultDesignArgs,
    route: RouteResponse?
) {
    val context = LocalContext.current
    val design = publicTransportViewModel.designArgs

    LaunchedEffect(Unit) {
        publicTransportViewModel.initializeRouteDetails()
    }

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    SetSystemStatusBar(
        !(design.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite), Color.Transparent
    )

    ScreenWrapper(
        topBar = {
            ScreenTopBar(
                title = stringResource(id = R.string.public_transport_route_details_title),
                navController = navController,
                masterDesignArgs = masterDesignArgs
            )
        },
        navController = rememberNavController(),
        screenWrapperState = publicTransportViewModel.wrapperState,
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = design
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (route != null) {
                BaseCardContainer(
                    moduleDesignArgs = design,
                    masterDesignArgs = masterDesignArgs,
                    useContentPadding = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(
                                RoundedCornerShape(
                                    design.mShapeCard ?: masterDesignArgs.mShapeCard
                                )
                            )
                            .height(200.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(mapStyleOptions = if (design.mapStyle != null) MapStyleOptions.loadRawResourceStyle(context, design.mapStyle!!) else null),
                            uiSettings = MapUiSettings(
                                compassEnabled = false,
                                tiltGesturesEnabled = false,
                                mapToolbarEnabled = false,
                                indoorLevelPickerEnabled = false,
                                myLocationButtonEnabled = false,
                                zoomControlsEnabled = false
                            ),
                            onMapClick = { latLng ->
                                // ...
                            },
                            onMapLoaded = {

                            },
                        ) {
                            route.trips.forEach { trip ->
                                if (trip.coordinates.isNotEmpty()) {
                                    Polyline(
                                        points = trip.coordinates.map { it.toLatLng() },
                                        color = Color.Blue
                                    )

                                    if (trip.coordinates.firstOrNull() != null) {
                                        Marker(
                                            title = "Start",
                                            state = MarkerState(trip.coordinates.first().toLatLng()),
                                            onClick = { marker ->
                                                true
                                            }
                                        )
                                    }
                                    if (trip.coordinates.lastOrNull() != null) {
                                        Marker(
                                            title = "Ziel",
                                            state = MarkerState(trip.coordinates.last().toLatLng()),
                                            onClick = { marker ->
                                                true
                                            }
                                        )
                                    }
                                }

                                trip.interchange?.coordinates?.let { coordinates ->
                                    Polyline(
                                        points = coordinates.map { it.toLatLng() },
                                        color = Color.Red
                                    )
                                }
                            }

                            route.trips.firstOrNull()?.coordinates?.let { originCoordinates ->
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        getBounds(originCoordinates, cameraPositionState.position.target)
                                    )
                                }
                            }
                        }
                    }
                }

                RootContainer(
                    masterDesignArgs = masterDesignArgs,
                    moduleDesignArgs = design
                ) {
                    item {
                        if (route.getAllTransportations().isNotEmpty()) {
                            BaseCardContainer(
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = design
                            ) {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (trans in route.getAllTransportations()) {
                                        item {
                                            CommuteButton(
                                                commuteName = trans?.number.toString(),
                                                iconId = trans?.product?.getIconViaIconId()
                                                    ?: R.drawable.ic_directions_bus,
                                                masterDesignArgs = masterDesignArgs
                                            )

                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        BaseCardContainer(
                            masterDesignArgs = masterDesignArgs,
                            moduleDesignArgs = design
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (route.minutesUntilDeparture == 0L) {
                                            stringResource(id = R.string.public_transport_departure_now)
                                        } else {
                                            stringResource(
                                                id = R.string.public_transport_departure_in_x_minutes,
                                                route.minutesUntilDeparture
                                            )
                                        },
                                        style = masterDesignArgs.normalTextStyle,
                                        color = masterDesignArgs.mCardTextColor
                                    )

                                    Text(
                                        text = stringResource(
                                            id = R.string.public_transport_tripTime,
                                            route.getRealDrivingDuration()
                                        ),
                                        style = masterDesignArgs.bodyTextStyle
                                    )
                                }

                                Divider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = Color.Gray,
                                    thickness = 1.dp
                                )

                                RouteStopRow(
                                    route = route,
                                    isStart = true,
                                    masterDesignArgs = masterDesignArgs
                                )

                                for (trip in route.trips) {
                                    RouteCommute(
                                        trip = trip,
                                        onClick = { },
                                        masterDesignArgs = masterDesignArgs
                                    )

                                    if (trip.interchange != null) {
                                        RouteCommute(
                                            interchange = trip.interchange,
                                            onClick = { },
                                            masterDesignArgs = masterDesignArgs
                                        )
                                    }
                                }

                                RouteStopRow(
                                    route = route,
                                    isStart = false,
                                    masterDesignArgs = masterDesignArgs
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}