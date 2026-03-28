package com.x8bit.bitwarden.ui.platform.feature.settings.changemasterpassword

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * 修改主密码页面的类型安全路由。
 */
@Serializable
data object ChangeMasterPasswordRoute

/**
 * 将修改主密码页面添加到导航图。
 */
fun NavGraphBuilder.changeMasterPasswordDestination(
    onNavigateBack: () -> Unit,
) {
    composable<ChangeMasterPasswordRoute> {
        ChangeMasterPasswordScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

/**
 * 导航到修改主密码页面。
 */
fun NavController.navigateToChangeMasterPassword(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = ChangeMasterPasswordRoute, navOptions = navOptions)
}
