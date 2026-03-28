package com.x8bit.bitwarden.ui.platform.feature.settings.organizations

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * 组织详情状态
 */
@Parcelize
data class OrganizationDetailState(
    val organizationId: String,
    val organizationName: Text,
    val menuItems: ImmutableList<MenuItem>,
) : Parcelable {
    
    /**
     * 菜单项
     */
    @Parcelize
    data class MenuItem(
        val id: String,
        val title: Text,
        @DrawableRes val iconRes: Int,
    ) : Parcelable
}

/**
 * 组织详情事件
 */
sealed class OrganizationDetailEvent {
    data object NavigateBack : OrganizationDetailEvent()
    data object NavigateToMembers : OrganizationDetailEvent()
    data object NavigateToCollections : OrganizationDetailEvent()
    data object NavigateToGroups : OrganizationDetailEvent()
    data object NavigateToPolicies : OrganizationDetailEvent()
}

/**
 * 组织详情动作
 */
sealed class OrganizationDetailAction {
    data object BackClick : OrganizationDetailAction()
    data class MenuItemClick(val item: OrganizationDetailState.MenuItem) : OrganizationDetailAction()
}

/**
 * 组织详情 ViewModel
 */
@HiltViewModel
class OrganizationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationDetailState, OrganizationDetailEvent, OrganizationDetailAction>(
    initialState = run {
        val args = savedStateHandle.toOrganizationDetailArgs()
        OrganizationDetailState(
            organizationId = args.organizationId,
            organizationName = args.organizationName.asText(),
            menuItems = persistentListOf(
                OrganizationDetailState.MenuItem(
                    id = "members",
                    title = BitwardenString.members.asText(),
                    iconRes = BitwardenDrawable.ic_users,
                ),
                OrganizationDetailState.MenuItem(
                    id = "collections",
                    title = BitwardenString.collections.asText(),
                    iconRes = BitwardenDrawable.ic_collections,
                ),
                OrganizationDetailState.MenuItem(
                    id = "groups",
                    title = BitwardenString.groups.asText(),
                    iconRes = BitwardenDrawable.ic_users,
                ),
                OrganizationDetailState.MenuItem(
                    id = "policies",
                    title = BitwardenString.policies.asText(),
                    iconRes = BitwardenDrawable.ic_cog,
                ),
            ),
        )
    },
) {
    
    override fun handleAction(action: OrganizationDetailAction) {
        when (action) {
            is OrganizationDetailAction.BackClick -> handleBackClick()
            is OrganizationDetailAction.MenuItemClick -> handleMenuItemClick(action)
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationDetailEvent.NavigateBack)
    }
    
    private fun handleMenuItemClick(action: OrganizationDetailAction.MenuItemClick) {
        when (action.item.id) {
            "members" -> sendEvent(OrganizationDetailEvent.NavigateToMembers)
            "collections" -> sendEvent(OrganizationDetailEvent.NavigateToCollections)
            "groups" -> sendEvent(OrganizationDetailEvent.NavigateToGroups)
            "policies" -> sendEvent(OrganizationDetailEvent.NavigateToPolicies)
        }
    }
}
