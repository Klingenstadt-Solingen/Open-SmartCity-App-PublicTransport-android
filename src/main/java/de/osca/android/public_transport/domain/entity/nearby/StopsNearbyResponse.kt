package de.osca.android.public_transport.domain.entity.nearby

import com.google.gson.annotations.SerializedName
import de.osca.android.public_transport.domain.entity.StopsResponse
import de.osca.android.public_transport.domain.entity.trip.TripProperties

data class StopsNearbyResponse(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("distance")
    var distance: Int = 0,
    @SerializedName("duration")
    var duration: Int = 0,
    @SerializedName("productClasses")
    var productClasses: List<Int>?,
    @SerializedName("properties")
    var properties: TripProperties?
) {
    fun toStopResponse(): StopsResponse {
        return StopsResponse(
            id = id,
            name = name,
            type = type,
            matchQuality = 100,
            productClasses = productClasses,
            properties = properties
        )
    }
}