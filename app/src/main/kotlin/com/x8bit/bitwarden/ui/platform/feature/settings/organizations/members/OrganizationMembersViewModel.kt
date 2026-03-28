package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.members

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.network.model.OrganizationUserDetailsJson
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * 组织成员列表状态
 */
@Parcelize
data class OrganizationMembersState(
    val organizationId: String,
    val viewState: ViewState,
    val dialogState: DialogState?,
) : Parcelable {
    
    sealed class ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState()
        
        @Parcelize
        data class Content(
            val members: ImmutableList<MemberItem>,
        ) : ViewState()
        
        @Parcelize
        data object Empty : ViewState()
    }
    
    sealed class DialogState : Parcelable {
        @Parcelize
        data class Error(val message: Text) : DialogState()
        
        @Parcelize
        data class Loading(val message: Text) : DialogState()
        
        @Parcelize
        data class ConfirmDelete(val memberId: String) : DialogState()
    }
    
    @Parcelize
    data class MemberItem(
        val id: String,
        val email: String?,
        val name: String?,
        val status: Int?,
    ) : Parcelable
}

sealed class OrganizationMembersEvent {
    data object NavigateBack : OrganizationMembersEvent()
}

sealed class OrganizationMembersAction {
    data object BackClick : OrganizationMembersAction()
    data object InviteClick : OrganizationMembersAction()
    data object DismissDialog : OrganizationMembersAction()
    data class RemoveMemberClick(val memberId: String) : OrganizationMembersAction()
    data class MembersLoaded(val members: List<OrganizationUserDetailsJson>) : OrganizationMembersAction()
}

/**
 * 组织成员列表 ViewModel
 */
@HiltViewModel
class OrganizationMembersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationMembersState, OrganizationMembersEvent, OrganizationMembersAction>(
    initialState = OrganizationMembersState(
        organizationId = savedStateHandle.toOrganizationMembersArgs().organizationId,
        viewState = OrganizationMembersState.ViewState.Loading,
        dialogState = null,
    ),
) {
    
    init {
        loadMembers()
    }
    
    private fun loadMembers() {
        viewModelScope.launch {
            // TODO: 调用 organizationManagementService.getOrganizationMembers
            // 暂时使用空列表
            mutableStateFlow.update {
                it.copy(
                    viewState = OrganizationMembersState.ViewState.Empty,
                )
            }
        }
    }
    
    override fun handleAction(action: OrganizationMembersAction) {
        when (action) {
            is OrganizationMembersAction.BackClick -> handleBackClick()
            is OrganizationMembersAction.InviteClick -> handleInviteClick()
            is OrganizationMembersAction.DismissDialog -> handleDismissDialog()
            is OrganizationMembersAction.RemoveMemberClick -> handleRemoveMemberClick(action)
            is OrganizationMembersAction.MembersLoaded -> handleMembersLoaded(action)
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationMembersEvent.NavigateBack)
    }
    
    private fun handleInviteClick() {
        // TODO: 打开邀请成员对话框
    }
    
    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }
    
    private fun handleRemoveMemberClick(action: OrganizationMembersAction.RemoveMemberClick) {
        mutableStateFlow.update {
            it.copy(
                dialogState = OrganizationMembersState.DialogState.ConfirmDelete(action.memberId),
            )
        }
    }
    
    private fun handleMembersLoaded(action: OrganizationMembersAction.MembersLoaded) {
        val members = action.members.map { json ->
            OrganizationMembersState.MemberItem(
                id = json.id,
                email = json.email,
                name = json.name,
                status = json.status,
            )
        }.toImmutableList()
        
        mutableStateFlow.update {
            it.copy(
                viewState = if (members.isEmpty()) {
                    OrganizationMembersState.ViewState.Empty
                } else {
                    OrganizationMembersState.ViewState.Content(members)
                },
            )
        }
    }
}
