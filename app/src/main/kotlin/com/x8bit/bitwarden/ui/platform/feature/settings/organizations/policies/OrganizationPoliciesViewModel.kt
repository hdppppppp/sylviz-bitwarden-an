package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.policies

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class OrganizationPoliciesState(
    val organizationId: String,
    val viewState: ViewState,
    val dialogState: DialogState?,
) : Parcelable {
    
    sealed class ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState()
        
        @Parcelize
        data class Content(
            val policies: ImmutableList<PolicyItem>,
        ) : ViewState()
        
        @Parcelize
        data object Empty : ViewState()
    }
    
    sealed class DialogState : Parcelable {
        @Parcelize
        data class Error(val message: Text) : DialogState()
        
        @Parcelize
        data class Loading(val message: Text) : DialogState()
    }
    
    @Parcelize
    data class PolicyItem(
        val type: Int,
        val name: Text,
        val enabled: Boolean,
    ) : Parcelable
}

sealed class OrganizationPoliciesEvent {
    data object NavigateBack : OrganizationPoliciesEvent()
}

sealed class OrganizationPoliciesAction {
    data object BackClick : OrganizationPoliciesAction()
    data object DismissDialog : OrganizationPoliciesAction()
    data class TogglePolicy(val type: Int, val enabled: Boolean) : OrganizationPoliciesAction()
}

@HiltViewModel
class OrganizationPoliciesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationPoliciesState, OrganizationPoliciesEvent, OrganizationPoliciesAction>(
    initialState = OrganizationPoliciesState(
        organizationId = savedStateHandle.toOrganizationPoliciesArgs().organizationId,
        viewState = OrganizationPoliciesState.ViewState.Loading,
        dialogState = null,
    ),
) {
    
    init {
        loadPolicies()
    }
    
    private fun loadPolicies() {
        viewModelScope.launch {
            // TODO: 调用 organizationAdminService.getOrganizationPolicies
            // 暂时使用预定义的策略列表
            val defaultPolicies = persistentListOf(
                OrganizationPoliciesState.PolicyItem(
                    type = 0,
                    name = BitwardenString.two_step_login.asText(),
                    enabled = false,
                ),
                OrganizationPoliciesState.PolicyItem(
                    type = 1,
                    name = BitwardenString.master_password.asText(),
                    enabled = false,
                ),
                OrganizationPoliciesState.PolicyItem(
                    type = 2,
                    name = BitwardenString.password_generator.asText(),
                    enabled = false,
                ),
            )
            
            mutableStateFlow.update {
                it.copy(
                    viewState = OrganizationPoliciesState.ViewState.Content(defaultPolicies),
                )
            }
        }
    }
    
    override fun handleAction(action: OrganizationPoliciesAction) {
        when (action) {
            is OrganizationPoliciesAction.BackClick -> handleBackClick()
            is OrganizationPoliciesAction.DismissDialog -> handleDismissDialog()
            is OrganizationPoliciesAction.TogglePolicy -> handleTogglePolicy(action)
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationPoliciesEvent.NavigateBack)
    }
    
    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }
    
    private fun handleTogglePolicy(action: OrganizationPoliciesAction.TogglePolicy) {
        viewModelScope.launch {
            // TODO: 调用 organizationAdminService.updateOrganizationPolicy
            val currentState = state.viewState
            if (currentState is OrganizationPoliciesState.ViewState.Content) {
                val updatedPolicies = currentState.policies.map { policy ->
                    if (policy.type == action.type) {
                        policy.copy(enabled = action.enabled)
                    } else {
                        policy
                    }
                }.toImmutableList()
                
                mutableStateFlow.update {
                    it.copy(
                        viewState = OrganizationPoliciesState.ViewState.Content(updatedPolicies),
                    )
                }
            }
        }
    }
}
