package de.osca.android.public_transport.domain.entity.trip

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.osca.android.public_transport.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripProduct(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("class")
    var theClass: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("iconId")
    var iconId: Int = 0
): Parcelable {
    fun getIconViaIconId(): Int {
        return when(theClass) {
            0 -> R.drawable.ic_interchange
            1, 13, 14, 15, 16, 17 -> R.drawable.ic_train_side_front_car
            2 -> R.drawable.ic_tram_fill_tunnel
            3, 4 -> R.drawable.ic_tram_fill
            5, 6, 7 -> R.drawable.ic_bus
            8 -> R.drawable.ic_cablecar
            99, 100 -> R.drawable.ic_figure_walk
            else -> R.drawable.ic_train_side_front_car
        }
    }
}