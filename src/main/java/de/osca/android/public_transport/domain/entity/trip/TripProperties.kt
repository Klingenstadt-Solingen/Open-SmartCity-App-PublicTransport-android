package de.osca.android.public_transport.domain.entity.trip

import com.google.gson.annotations.SerializedName

data class TripProperties(
    @SerializedName("stopId")
    var stopId: String?
)