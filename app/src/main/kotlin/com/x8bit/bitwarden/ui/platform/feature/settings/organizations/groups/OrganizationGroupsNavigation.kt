package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.groups

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import kotlinx.serialization.Serializable

/**
 * 组织组列表路由
 */
@Serializable
data class OrganizationGroupsRoute(
    val organizationId: String,
    val organizationName: String,
)

data class OrganizationGroupsArgs(
    val organizationId: String,
    val organizationName: String,
)

fun SavedStateHandle.toOrganizationGroupsArgs(): OrganizationGroupsArgs {
    val route = this.toRoute<OrganizationGroupsRoute>()
    return OrganizationGroupsArgs(
        organizationId = route.organizationId,
        organizationName = route.organizationName,
    )
}

fun NavGraphBuilder.organizationGroupsDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions<OrganizationGroupsRoute> {
        OrganizationGroupsScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToOrganizationGroups(
    organizationId: String,
    organizationName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = OrganizationGroupsRoute(
            organizationId = organizationId,
            organizationName = organizationName,
        ),
        navOptions = navOptions,
    )
}
