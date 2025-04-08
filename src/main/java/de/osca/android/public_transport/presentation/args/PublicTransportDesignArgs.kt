package de.osca.android.public_transport.presentation.args

import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs

interface PublicTransportDesignArgs : ModuleDesignArgs {
    val mapStyle: Int?
}