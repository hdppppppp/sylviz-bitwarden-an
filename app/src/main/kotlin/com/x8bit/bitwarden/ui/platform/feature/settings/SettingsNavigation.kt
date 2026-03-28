package com.x8bit.bitwarden.ui.platform.feature.settings

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithRootPushTransitions
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import com.bitwarden.ui.platform.util.ParcelableRouteSerializer
import com.x8bit.bitwarden.ui.platform.feature.settings.about.aboutDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.about.navigateToAbout
import com.x8bit.bitwarden.ui.platform.feature.settings.helpcenter.helpCenterDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.helpcenter.navigateToHelpCenter
import com.x8bit.bitwarden.ui.platform.feature.settings.privacypolicy.privacyPolicyDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.privacypolicy.navigateToPrivacyPolicy
import com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity.accountSecurityDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity.navigateToAccountSecurity
import com.x8bit.bitwarden.ui.platform.feature.settings.changemasterpassword.navigateToChangeMasterPassword
import com.x8bit.bitwarden.ui.platform.feature.settings.appearance.appearanceDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.appearance.navigateToAppearance
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.autoFillDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.blockautofill.blockAutoFillDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.blockautofill.navigateToBlockAutoFillScreen
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.navigateToAutoFill
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.privilegedapps.list.navigateToPrivilegedAppsList
import com.x8bit.bitwarden.ui.platform.feature.settings.autofill.privilegedapps.list.privilegedAppsListDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.other.navigateToOther
import com.x8bit.bitwarden.ui.platform.feature.settings.other.otherDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.vault.navigateToVaultSettings
import com.x8bit.bitwarden.ui.platform.feature.settings.vault.vaultSettingsDestination
import com.x8bit.bitwarden.ui.vault.feature.importitems.importItemsDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.navigateToOrganizationDetail
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.navigateToOrganizations
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.organizationDetailDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.organizationsDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.members.navigateToOrganizationMembers
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.members.organizationMembersDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections.navigateToOrganizationCollections
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections.organizationCollectionsDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.groups.navigateToOrganizationGroups
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.groups.organizationGroupsDestination
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.policies.navigateToOrganizationPolicies
import com.x8bit.bitwarden.ui.platform.feature.settings.organizations.policies.organizationPoliciesDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * The type-safe route for the settings graph.
 */
@Serializable
data object SettingsGraphRoute

/**
 * The type-safe route for the settings screen.
 */
@Parcelize
@Serializable(with = SettingsRoute.Serializer::class)
sealed class SettingsRoute : Parcelable {
    /**
     * Indicates that the settings screen should be shown as a pre-authentication.
     */
    abstract val isPreAuth: Boolean

    /**
     * Custom serializer to support polymorphic routes.
     */
    class Serializer : ParcelableRouteSerializer<SettingsRoute>(SettingsRoute::class)

    /**
     * The type-safe route for the settings screen in the settings graph.
     */
    @Parcelize
    @Serializable(with = Standard.Serializer::class)
    data object Standard : SettingsRoute() {
        override val isPreAuth: Boolean get() = false

        /**
         * Custom serializer to support polymorphic routes.
         */
        class Serializer : ParcelableRouteSerializer<Standard>(Standard::class)
    }

    /**
     * The type-safe route for the pre-auth settings screen.
     */
    @Parcelize
    @Serializable(with = PreAuth.Serializer::class)
    data object PreAuth : SettingsRoute() {
        override val isPreAuth: Boolean get() = true

        /**
         * Custom serializer to support polymorphic routes.
         */
        class Serializer : ParcelableRouteSerializer<PreAuth>(PreAuth::class)
    }
}

/**
 * Class to retrieve settings arguments from the [SavedStateHandle].
 */
data class SettingsArgs(val isPreAuth: Boolean)

/**
 * Constructs a [SettingsArgs] from the [SavedStateHandle] and internal route data.
 */
fun SavedStateHandle.toSettingsArgs(): SettingsArgs {
    val route = this.toRoute<SettingsRoute>()
    return SettingsArgs(isPreAuth = route.isPreAuth)
}

/**
 * Add settings destinations to the nav graph.
 */
@Suppress("LongParameterList")
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onNavigateToDeleteAccount: () -> Unit,
    onNavigateToExportVault: () -> Unit,
    onNavigateToFolders: () -> Unit,
    onNavigateToPendingRequests: () -> Unit,
    onNavigateToSetupUnlockScreen: () -> Unit,
    onNavigateToSetupAutoFillScreen: () -> Unit,
    onNavigateToSetupBrowserAutofill: () -> Unit,
    onNavigateToImportLogins: () -> Unit,
    onNavigateToImportItems: () -> Unit,
    onNavigateToAboutPrivilegedApps: () -> Unit,
) {
    navigation<SettingsGraphRoute>(
        startDestination = SettingsRoute.Standard,
    ) {
        composableWithRootPushTransitions<SettingsRoute.Standard> {
            SettingsScreen(
                onNavigateBack = {},
                onNavigateToAbout = { navController.navigateToAbout(isPreAuth = false) },
                onNavigateToAccountSecurity = { navController.navigateToAccountSecurity() },
                onNavigateToAppearance = { navController.navigateToAppearance(isPreAuth = false) },
                onNavigateToAutoFill = { navController.navigateToAutoFill() },
                onNavigateToOther = { navController.navigateToOther(isPreAuth = false) },
                onNavigateToVault = { navController.navigateToVaultSettings() },
                onNavigateToOrganizations = { navController.navigateToOrganizations() },
            )
        }
        aboutDestination(
            isPreAuth = false,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToHelpCenter = { navController.navigateToHelpCenter() },
            onNavigateToPrivacyPolicy = { navController.navigateToPrivacyPolicy() },
        )
        helpCenterDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        privacyPolicyDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        accountSecurityDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToDeleteAccount = onNavigateToDeleteAccount,
            onNavigateToPendingRequests = onNavigateToPendingRequests,
            onNavigateToSetupUnlockScreen = onNavigateToSetupUnlockScreen,
            onNavigateToChangeMasterPassword = { navController.navigateToChangeMasterPassword() },
            navController = navController,
        )
        appearanceDestination(
            isPreAuth = false,
            onNavigateBack = { navController.popBackStack() },
        )
        autoFillDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToBlockAutoFillScreen = { navController.navigateToBlockAutoFillScreen() },
            onNavigateToSetupAutofill = onNavigateToSetupAutoFillScreen,
            onNavigateToSetupBrowserAutofill = onNavigateToSetupBrowserAutofill,
            onNavigateToAboutPrivilegedAppsScreen = onNavigateToAboutPrivilegedApps,
            onNavigateToPrivilegedAppsList = { navController.navigateToPrivilegedAppsList() },
        )
        otherDestination(
            isPreAuth = false,
            onNavigateBack = { navController.popBackStack() },
        )
        vaultSettingsDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToExportVault = onNavigateToExportVault,
            onNavigateToFolders = onNavigateToFolders,
            onNavigateToImportLogins = onNavigateToImportLogins,
            onNavigateToImportItems = onNavigateToImportItems,
        )
        importItemsDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToImportLogins = onNavigateToImportLogins,
        )
        blockAutoFillDestination(onNavigateBack = { navController.popBackStack() })
        privilegedAppsListDestination(onNavigateBack = { navController.popBackStack() })
        
        // 组织管理相关页面
        organizationsDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToOrganizationDetail = { orgId, orgName ->
                navController.navigateToOrganizationDetail(
                    organizationId = orgId,
                    organizationName = orgName,
                )
            },
        )
        organizationDetailDestination(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToMembers = { orgId, orgName ->
                navController.navigateToOrganizationMembers(orgId, orgName)
            },
            onNavigateToCollections = { orgId, orgName ->
                navController.navigateToOrganizationCollections(orgId, orgName)
            },
            onNavigateToGroups = { orgId, orgName ->
                navController.navigateToOrganizationGroups(orgId, orgName)
            },
            onNavigateToPolicies = { orgId, orgName ->
                navController.navigateToOrganizationPolicies(orgId, orgName)
            },
        )
        organizationMembersDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        organizationCollectionsDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        organizationGroupsDestination(
            onNavigateBack = { navController.popBackStack() },
        )
        organizationPoliciesDestination(
            onNavigateBack = { navController.popBackStack() },
        )
    }
}

/**
 * Add pre auth settings and associated screens to the nav graph.
 */
fun NavGraphBuilder.preAuthSettingsDestinations(
    navController: NavController,
) {
    composableWithSlideTransitions<SettingsRoute.PreAuth> {
        SettingsScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToAbout = { navController.navigateToAbout(isPreAuth = true) },
            onNavigateToAppearance = { navController.navigateToAppearance(isPreAuth = true) },
            onNavigateToOther = { navController.navigateToOther(isPreAuth = true) },
            onNavigateToAccountSecurity = { /* no-op */ },
            onNavigateToAutoFill = { /* no-op */ },
            onNavigateToVault = { /* no-op */ },
            onNavigateToOrganizations = { /* no-op */ },
        )
    }
    appearanceDestination(
        isPreAuth = true,
        onNavigateBack = { navController.popBackStack() },
    )
    otherDestination(
        isPreAuth = true,
        onNavigateBack = { navController.popBackStack() },
    )
    aboutDestination(
        isPreAuth = true,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToHelpCenter = { navController.navigateToHelpCenter() },
        onNavigateToPrivacyPolicy = { navController.navigateToPrivacyPolicy() },
    )
    helpCenterDestination(
        onNavigateBack = { navController.popBackStack() },
    )
    privacyPolicyDestination(
        onNavigateBack = { navController.popBackStack() },
    )
}

/**
 * Navigate to the settings graph.
 */
fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(route = SettingsGraphRoute, navOptions = navOptions)
}

/**
 * Navigate to the settings graph root.
 */
fun NavController.navigateToSettingsGraphRoot() {
    // Brings up back to the Settings graph
    navigateToSettingsGraph(
        navOptions = navOptions {
            popUpTo(id = graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        },
    )
    // Then ensures that we are at the root
    popBackStack(route = SettingsRoute.Standard, inclusive = false)
}

/**
 * Navigate to the pre-auth settings screen.
 */
fun NavController.navigateToPreAuthSettings(navOptions: NavOptions? = null) {
    this.navigate(route = SettingsRoute.PreAuth, navOptions = navOptions)
}
