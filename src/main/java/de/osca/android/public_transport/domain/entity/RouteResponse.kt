package de.osca.android.public_transport.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.utils.extensions.diffTo
import de.osca.android.public_transport.domain.entity.trip.Transportation
import de.osca.android.public_transport.domain.entity.trip.Trip
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Parcelize
data class RouteResponse(
    @SerializedName("interchanges")
    var interchanges: Int = 0,
    @SerializedName("rating")
    var rating: Int = 0,
    @SerializedName("trips")
    var trips: List<Trip> = emptyList()
) : Parcelable {
    val totalDurationInMinutes get() = trips.sumOf { it.durationInMinutes }

    val plannedDepartureTime get() = trips.firstOrNull()?.plannedDepartureTime()
    val estimatedDepartureTime get() = trips.firstOrNull()?.estimatedDepartureTime()

    val originName get() = trips.firstOrNull()?.origin?.name ?: "--"
    val destinationName get() = trips.lastOrNull()?.destination?.name ?: "--"

    val estimatedArrivalTime get() = estimatedDepartureTime?.plusMinutes(totalDurationInMinutes.toLong())

    val minutesUntilDeparture
        get() = plannedDepartureTime?.let {
            LocalDateTime.now().diffTo(it)?.toMinutes()
        } ?: -1L

    fun getAllTransportations(): List<Transportation?> {
        return trips.filter { it.isByTransport }.map { it.transportation }
    }

    fun getRealArrivalTime(): LocalDateTime? {
        return trips.lastOrNull()?.estimatedArrivalTime ?: estimatedArrivalTime
    }

    fun getRealDrivingDuration(): Int {
        return ChronoUnit.MINUTES.between(estimatedDepartureTime, getRealArrivalTime()).toInt()
    }
}