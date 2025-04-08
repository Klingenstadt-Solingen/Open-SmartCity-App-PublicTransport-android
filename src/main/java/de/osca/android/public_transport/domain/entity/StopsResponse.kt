package de.osca.android.public_transport.domain.entity

import com.google.gson.annotations.SerializedName
import de.osca.android.public_transport.domain.entity.trip.TripProperties

data class StopsResponse(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("matchQuality")
    var matchQuality: Int = 0,
    @SerializedName("productClasses")
    var productClasses: List<Int>?,
    @SerializedName("properties")
    var properties: TripProperties?
)
