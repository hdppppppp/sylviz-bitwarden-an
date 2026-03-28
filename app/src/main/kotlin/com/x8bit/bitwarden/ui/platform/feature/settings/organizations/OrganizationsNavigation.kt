package com.x8bit.bitwarden.ui.platform.feature.settings.organizations

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import kotlinx.serialization.Serializable

/**
 * 组织列表路由
 */
@Serializable
data object OrganizationsRoute

/**
 * 组织详情路由
 */
@Serializable
data class OrganizationDetailRoute(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 组织详情参数
 */
data class OrganizationDetailArgs(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 从 SavedStateHandle 获取组织详情参数
 */
fun SavedStateHandle.toOrganizationDetailArgs(): OrganizationDetailArgs {
    val route = this.toRoute<OrganizationDetailRoute>()
    return OrganizationDetailArgs(
        organizationId = route.organizationId,
        organizationName = route.organizationName,
    )
}

/**
 * 添加组织列表页面到导航图
 */
fun NavGraphBuilder.organizationsDestination(
    onNavigateBack: () -> Unit,
    onNavigateToOrganizationDetail: (organizationId: String, organizationName: String) -> Unit,
) {
    composableWithSlideTransitions<OrganizationsRoute> {
        OrganizationsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToOrganizationDetail = onNavigateToOrganizationDetail,
        )
    }
}

/**
 * 添加组织详情页面到导航图
 */
fun NavGraphBuilder.organizationDetailDestination(
    onNavigateBack: () -> Unit,
    onNavigateToMembers: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToCollections: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToGroups: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToPolicies: (organizationId: String, organizationName: String) -> Unit,
) {
    composableWithSlideTransitions<OrganizationDetailRoute> {
        OrganizationDetailScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToMembers = onNavigateToMembers,
            onNavigateToCollections = onNavigateToCollections,
            onNavigateToGroups = onNavigateToGroups,
            onNavigateToPolicies = onNavigateToPolicies,
        )
    }
}

/**
 * 导航到组织列表页面
 */
fun NavController.navigateToOrganizations(navOptions: NavOptions? = null) {
    this.navigate(route = OrganizationsRoute, navOptions = navOptions)
}

/**
 * 导航到组织详情页面
 */
fun NavController.navigateToOrganizationDetail(
    organizationId: String,
    organizationName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = OrganizationDetailRoute(
            organizationId = organizationId,
            organizationName = organizationName,
        ),
        navOptions = navOptions,
    )
}
