package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import kotlinx.serialization.Serializable

/**
 * 组织集合列表路由
 */
@Serializable
data class OrganizationCollectionsRoute(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 组织集合列表参数
 */
data class OrganizationCollectionsArgs(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 从 SavedStateHandle 获取参数
 */
fun SavedStateHandle.toOrganizationCollectionsArgs(): OrganizationCollectionsArgs {
    val route = this.toRoute<OrganizationCollectionsRoute>()
    return OrganizationCollectionsArgs(
        organizationId = route.organizationId,
        organizationName = route.organizationName,
    )
}

/**
 * 添加组织集合列表页面到导航图
 */
fun NavGraphBuilder.organizationCollectionsDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions<OrganizationCollectionsRoute> {
        OrganizationCollectionsScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

/**
 * 导航到组织集合列表页面
 */
fun NavController.navigateToOrganizationCollections(
    organizationId: String,
    organizationName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = OrganizationCollectionsRoute(
            organizationId = organizationId,
            organizationName = organizationName,
        ),
        navOptions = navOptions,
    )
}
