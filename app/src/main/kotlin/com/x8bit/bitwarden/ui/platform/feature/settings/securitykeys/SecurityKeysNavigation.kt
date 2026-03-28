package com.x8bit.bitwarden.ui.platform.feature.settings.securitykeys

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

@Serializable data object SecurityKeysRoute

fun NavGraphBuilder.securityKeysDestination(onNavigateBack: () -> Unit) {
    composableWithPushTransitions<SecurityKeysRoute> {
        SecurityKeysScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToSecurityKeys(navOptions: NavOptions? = null) {
    navigate(route = SecurityKeysRoute, navOptions = navOptions)
}
