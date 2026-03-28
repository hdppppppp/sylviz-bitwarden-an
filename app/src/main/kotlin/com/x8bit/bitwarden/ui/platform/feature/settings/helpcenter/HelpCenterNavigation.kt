package com.x8bit.bitwarden.ui.platform.feature.settings.helpcenter

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

/**
 * The type-safe route for the help center screen.
 */
@Serializable
data object HelpCenterRoute

/**
 * Add help center destination to the nav graph.
 */
fun NavGraphBuilder.helpCenterDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithPushTransitions<HelpCenterRoute> {
        HelpCenterScreen(onNavigateBack = onNavigateBack)
    }
}

/**
 * Navigate to the help center screen.
 */
fun NavController.navigateToHelpCenter(
    navOptions: NavOptions? = null,
) {
    navigate(route = HelpCenterRoute, navOptions = navOptions)
}
