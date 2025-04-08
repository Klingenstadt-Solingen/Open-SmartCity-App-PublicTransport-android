package de.osca.android.public_transport.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import de.osca.android.essentials.domain.entity.navigation.NavigationItem
import de.osca.android.public_transport.R

sealed class PublicTransportNavItems {
    object PublicTransport : NavigationItem(
        title = R.string.public_transport_title,
        route = PUBLIC_TRANSPORT_ROUTE,
        icon = R.drawable.ic_circle,
        arguments =
        listOf(
            navArgument(ARG_TRANSPORT_FROM_ID) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(ARG_TRANSPORT_TO_ID) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(ARG_TRANSPORT_DATETIME) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(ARG_TRANSPORT_ARRDEP) {
                type = NavType.StringType
                nullable = true
            },
        ),
        deepLinks = listOf(navDeepLink {
            uriPattern = "solingen://transport/route?${ARG_TRANSPORT_FROM_ID}={${ARG_TRANSPORT_FROM_ID}}&${ARG_TRANSPORT_TO_ID}={${ARG_TRANSPORT_TO_ID}}&${ARG_TRANSPORT_DATETIME}={${ARG_TRANSPORT_DATETIME}}&${ARG_TRANSPORT_ARRDEP}={${ARG_TRANSPORT_ARRDEP}}"
        }),
    )

    object RouteDetails : NavigationItem(
        title = R.string.public_transport_route_details_title,
        route = ROUTE_DETAILS_ROUTE,
        icon = R.drawable.ic_circle,
    ) {
        const val BUNDLE_KEY_TRIP = "bundle_route_details_trip"
    }

    companion object {
        const val ARG_TRANSPORT_FROM_ID = "from"
        const val ARG_TRANSPORT_TO_ID = "to"
        const val ARG_TRANSPORT_DATETIME = "datetime"
        const val ARG_TRANSPORT_ARRDEP = "arrdep"

        const val PUBLIC_TRANSPORT_ROUTE = "public_transport"
        const val ROUTE_DETAILS_ROUTE = "route_details"
    }
}
