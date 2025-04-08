package de.osca.android.public_transport.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.osca.android.essentials.data.client.OSCAHttpClient
import de.osca.android.public_transport.data.PublicTransportApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PublicTransportModule {
    @Singleton
    @Provides
    fun publicTransportApiService(oscaHttpClient: OSCAHttpClient): PublicTransportApiService =
        oscaHttpClient.create(PublicTransportApiService::class.java)
}
