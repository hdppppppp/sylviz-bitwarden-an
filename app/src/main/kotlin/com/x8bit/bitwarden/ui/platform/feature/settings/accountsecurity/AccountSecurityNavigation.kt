package com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.bitwarden.ui.platform.base.util.composableWithPushTransitions
import com.x8bit.bitwarden.ui.platform.feature.settings.changeemail.changeEmailDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.changeemail.navigateToChangeEmail
import com.x8bit.bitwarden.ui.platform.feature.settings.changeusername.changeUsernameDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.changeusername.navigateToChangeUsername
import com.x8bit.bitwarden.ui.platform.feature.settings.changemasterpassword.changeMasterPasswordDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.devicemanagement.deviceManagementDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.devicemanagement.navigateToDeviceManagement
import com.x8bit.bitwarden.ui.platform.feature.settings.securitykeys.navigateToSecurityKeys
import com.x8bit.bitwarden.ui.platform.feature.settings.securitykeys.securityKeysDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.twostepverification.navigateToTwoStepVerification
import com.x8bit.bitwarden.ui.platform.feature.settings.twostepverification.twoStepVerificationDestination
import kotlinx.serialization.Serializable

/** The type-safe route for the account security screen. */
@Serializable data object AccountSecurityRoute

/** Add settings destinations to the nav graph. */
fun NavGraphBuilder.accountSecurityDestination(
        onNavigateBack: () -> Unit,
        onNavigateToDeleteAccount: () -> Unit,
        onNavigateToPendingRequests: () -> Unit,
        onNavigateToSetupUnlockScreen: () -> Unit,
        onNavigateToChangeMasterPassword: () -> Unit,
        navController: NavController,
) {
        composableWithPushTransitions<AccountSecurityRoute> {
                AccountSecurityScreen(
                        onNavigateBack = onNavigateBack,
                        onNavigateToDeleteAccount = onNavigateToDeleteAccount,
                        onNavigateToPendingRequests = onNavigateToPendingRequests,
                        onNavigateToSetupUnlockScreen = onNavigateToSetupUnlockScreen,
                        onNavigateToChangeMasterPassword = onNavigateToChangeMasterPassword,
                        onNavigateToTwoStepVerification = {
                                navController.navigateToTwoStepVerification()
                        },
                        onNavigateToDeviceManagement = {
                                navController.navigateToDeviceManagement()
                        },
                        onNavigateToSecurityKeys = { navController.navigateToSecurityKeys() },
                        onNavigateToChangeEmail = { navController.navigateToChangeEmail() },
                        onNavigateToChangeUsername = { navController.navigateToChangeUsername() },
                )
        }
        changeMasterPasswordDestination(
                onNavigateBack = onNavigateBack,
        )
        twoStepVerificationDestination(
                onNavigateBack = { navController.popBackStack() },
        )
        deviceManagementDestination(
                onNavigateBack = { navController.popBackStack() },
        )
        securityKeysDestination(
                onNavigateBack = { navController.popBackStack() },
        )
        changeEmailDestination(
                onNavigateBack = { navController.popBackStack() },
        )
        changeUsernameDestination(
                onNavigateBack = { navController.popBackStack() },
        )
}

/** Navigate to the account security screen. */
fun NavController.navigateToAccountSecurity(navOptions: NavOptions? = null) {
        this.navigate(route = AccountSecurityRoute, navOptions = navOptions)
}
