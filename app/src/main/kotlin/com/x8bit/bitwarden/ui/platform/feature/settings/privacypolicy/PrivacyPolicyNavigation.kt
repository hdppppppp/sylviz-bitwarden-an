package com.x8bit.bitwarden.ui.platform.feature.settings.privacypolicy

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

/**
 * The type-safe route for the privacy policy screen.
 */
@Serializable
data object PrivacyPolicyRoute

/**
 * Add privacy policy destination to the nav graph.
 */
fun NavGraphBuilder.privacyPolicyDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithPushTransitions<PrivacyPolicyRoute> {
        PrivacyPolicyScreen(onNavigateBack = onNavigateBack)
    }
}

/**
 * Navigate to the privacy policy screen.
 */
fun NavController.navigateToPrivacyPolicy(
    navOptions: NavOptions? = null,
) {
    navigate(route = PrivacyPolicyRoute, navOptions = navOptions)
}
