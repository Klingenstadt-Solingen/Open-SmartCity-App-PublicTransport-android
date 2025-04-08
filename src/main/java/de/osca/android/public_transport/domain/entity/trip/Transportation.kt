package de.osca.android.public_transport.domain.entity.trip

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transportation(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("number")
    var number: String = "",
    @SerializedName("product")
    var product: TripProduct = TripProduct(),
    @SerializedName("destination")
    var destination: TransportationDestination = TransportationDestination()
): Parcelable
