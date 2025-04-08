package de.osca.android.public_transport.domain.entity.trip

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.utils.constants.ISO_8601_NO_MILLISECONDS
import de.osca.android.essentials.utils.extensions.toLocalDateTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripStop(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("coord")
    var coord: List<Double> = emptyList(),
    @SerializedName("platform")
    var platform: String = "",
    @SerializedName("departureTimePlanned")
    var departureTimePlanned: String = "",
    @SerializedName("departureTimeEstimated")
    var departureTimeEstimated: String = "",
    @SerializedName("niveau")
    var niveau: Int = 0
) : Parcelable {

    // TODO: API is returning dates in UTC+1, instead of UTC+2 (Germany), we add 1 hour to compensate
    // however, we need to check if this can be fixed on the API side
    val plannedDeparture
        get() = departureTimePlanned.toLocalDateTime()

    // TODO: API is returning dates in UTC+1, instead of UTC+2 (Germany), we add 1 hour to compensate
    // however, we need to check if this can be fixed on the API side
    val estimatedDeparture
        get() = departureTimeEstimated.toLocalDateTime()

    fun getCoordinateFromDoublePair(): Coordinates? {
        return if (coord.size == 2) {
            Coordinates(latitude = coord[0], longitude = coord[1])
        } else {
            null
        }
    }
}