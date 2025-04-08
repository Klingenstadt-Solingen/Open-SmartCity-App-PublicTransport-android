package de.osca.android.public_transport.domain.entity

import com.google.gson.annotations.SerializedName

/**
 * make sure the "from" "to" coordintes are swapped for "lat" and "long"
 * the server expect a reversed lat, long coordinate
 */
data class RequestBodyForTrip(
    @SerializedName("from")
    var from: TripStopId = TripStopId(),
    @SerializedName("to")
    var to: TripStopId = TripStopId(),
    @SerializedName("trip")
    var trip: TripBody = TripBody()
)

data class TripBody(
    @SerializedName("type")
    var type: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("count")
    var count: Int? = 10,
    @SerializedName("tz")
    var tz: String? = null,
)

data class TripStopId(
    @SerializedName("stop")
    var stop: String = "",
)