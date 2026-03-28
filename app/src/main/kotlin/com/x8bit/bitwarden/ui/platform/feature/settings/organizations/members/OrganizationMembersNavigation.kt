package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.members

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.bitwarden.ui.platform.base.util.composableWithSlideTransitions
import kotlinx.serialization.Serializable

/**
 * 组织成员列表路由
 */
@Serializable
data class OrganizationMembersRoute(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 组织成员列表参数
 */
data class OrganizationMembersArgs(
    val organizationId: String,
    val organizationName: String,
)

/**
 * 从 SavedStateHandle 获取参数
 */
fun SavedStateHandle.toOrganizationMembersArgs(): OrganizationMembersArgs {
    val route = this.toRoute<OrganizationMembersRoute>()
    return OrganizationMembersArgs(
        organizationId = route.organizationId,
        organizationName = route.organizationName,
    )
}

/**
 * 添加组织成员列表页面到导航图
 */
fun NavGraphBuilder.organizationMembersDestination(
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions<OrganizationMembersRoute> {
        OrganizationMembersScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

/**
 * 导航到组织成员列表页面
 */
fun NavController.navigateToOrganizationMembers(
    organizationId: String,
    organizationName: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = OrganizationMembersRoute(
            organizationId = organizationId,
            organizationName = organizationName,
        ),
        navOptions = navOptions,
    )
}
