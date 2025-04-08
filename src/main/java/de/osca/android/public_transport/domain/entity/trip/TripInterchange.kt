package de.osca.android.public_transport.domain.entity.trip

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.osca.android.essentials.domain.entity.Coordinates
import de.osca.android.essentials.utils.extensions.safeLet
import de.osca.android.public_transport.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripInterchange(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("type")
    var type: Int = 0,
    @SerializedName("coords")
    var coords: List<List<Double>> = emptyList()
) : Parcelable {

    val coordinates get() = coords.mapNotNull {
        if (it.size == 2) Coordinates(latitude = it[0], longitude = it[1]) else null
    }

    fun getIconViaType(): Int {
        return when (type) {
            100 -> R.drawable.ic_directions_bus // bus
            else -> R.drawable.ic_directions_subway
        }
    }

}