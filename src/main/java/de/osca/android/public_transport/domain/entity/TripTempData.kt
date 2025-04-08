package de.osca.android.public_transport.domain.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDateTime

object TripTempData {

    val routeStopsData: MutableList<RouteResponse> = mutableStateListOf()

    val tripTempDateTime: MutableState<LocalDateTime> = mutableStateOf(LocalDateTime.now())
}