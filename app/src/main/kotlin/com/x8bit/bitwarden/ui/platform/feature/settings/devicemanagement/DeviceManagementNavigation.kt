package com.x8bit.bitwarden.ui.platform.feature.settings.devicemanagement

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

@Serializable
data object DeviceManagementRoute

fun NavGraphBuilder.deviceManagementDestination(onNavigateBack: () -> Unit) {
    composableWithPushTransitions<DeviceManagementRoute> {
        DeviceManagementScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToDeviceManagement(navOptions: NavOptions? = null) {
    navigate(route = DeviceManagementRoute, navOptions = navOptions)
}
