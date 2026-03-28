package com.x8bit.bitwarden.ui.platform.feature.settings.changeemail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

@Serializable
data object ChangeEmailRoute

fun NavGraphBuilder.changeEmailDestination(onNavigateBack: () -> Unit) {
    composableWithPushTransitions<ChangeEmailRoute> {
        ChangeEmailScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToChangeEmail(navOptions: NavOptions? = null) {
    navigate(route = ChangeEmailRoute, navOptions = navOptions)
}
