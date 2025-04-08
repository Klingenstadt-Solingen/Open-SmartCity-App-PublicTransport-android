package de.osca.android.public_transport.domain.entity.nearby
import com.google.gson.annotations.SerializedName

data class StopsNearbyWrapper(
    @SerializedName("location")
    var location: String = "",
    @SerializedName("stops")
    var stops: List<StopsNearbyResponse> = emptyList(),
)