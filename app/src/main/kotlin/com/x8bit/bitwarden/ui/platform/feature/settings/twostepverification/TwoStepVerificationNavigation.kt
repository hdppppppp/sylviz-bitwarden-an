package com.x8bit.bitwarden.ui.platform.feature.settings.twostepverification

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

@Serializable
data object TwoStepVerificationRoute

fun NavGraphBuilder.twoStepVerificationDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithPushTransitions<TwoStepVerificationRoute> {
        TwoStepVerificationScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToTwoStepVerification(navOptions: NavOptions? = null) {
    navigate(route = TwoStepVerificationRoute, navOptions = navOptions)
}
