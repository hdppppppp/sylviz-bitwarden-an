package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.policies

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationPoliciesRoute(
    val organizationId: String,
    val organizationName: String,
)

data class OrganizationPoliciesArgs(
    val organizationId: String,
    val organizationName: String,
)

fun SavedStateHandle.toOrganizationPoliciesArgs(): OrganizationPoliciesArgs {
    val route = this.toRoute<OrganizationPoliciesRoute>()
    return OrganizationPoliciesArgs(
        organizationId = route.organizationId,
        organizationName = route.organizationName,
    )
}

fun NavGraphBuilder.organizationPoliciesDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions<OrganizationPoliciesRoute> {
        OrganizationPoliciesScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToOrganizationPolicies(
    organizationId: String,
    organizationName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = OrganizationPoliciesRoute(
            organizationId = organizationId,
            organizationName = organizationName,
        ),
        navOptions = navOptions,
    )
}
