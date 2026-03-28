package com.x8bit.bitwarden.ui.platform.feature.settings.changeusername

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import kotlinx.serialization.Serializable

@Serializable
data object ChangeUsernameRoute

fun NavGraphBuilder.changeUsernameDestination(onNavigateBack: () -> Unit) {
    composableWithPushTransitions<ChangeUsernameRoute> {
        ChangeUsernameScreen(onNavigateBack = onNavigateBack)
    }
}

fun NavController.navigateToChangeUsername(navOptions: NavOptions? = null) {
    navigate(route = ChangeUsernameRoute, navOptions = navOptions)
}
