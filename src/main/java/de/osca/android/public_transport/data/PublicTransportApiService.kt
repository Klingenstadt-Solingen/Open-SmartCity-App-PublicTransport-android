package de.osca.android.public_transport.data

import de.osca.android.public_transport.domain.entity.RequestBodyForTrip
import de.osca.android.public_transport.domain.entity.RouteResponse
import de.osca.android.public_transport.domain.entity.StopsResponse
import de.osca.android.public_transport.domain.entity.nearby.StopsNearbyWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface PublicTransportApiService {

    @POST("functions/pt-stop-query")
    suspend fun getStops(
        @Query("query") query: String
    ): Response<List<StopsResponse>>

    @POST("functions/pt-stop-id-query")
    suspend fun getStopsByIds(
        @Query("ids") ids: List<String>
    ): Response<List<StopsResponse>>

    @POST("functions/pt-stop-nearby")
    suspend fun getStopsNearBy(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<StopsNearbyWrapper>

    @POST("functions/pt-trip")
    suspend fun getPtTrip(@Body bodyJson: RequestBodyForTrip): Response<List<RouteResponse>>
}