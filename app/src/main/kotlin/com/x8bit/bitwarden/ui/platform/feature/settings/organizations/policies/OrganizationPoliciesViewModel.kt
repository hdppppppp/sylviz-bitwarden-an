package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.policies

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.organization.repository.OrganizationRepository
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
        
        @Parcelize
        data object Error : ViewState()
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
    data object RetryClick : OrganizationPoliciesAction()
}

@HiltViewModel
class OrganizationPoliciesViewModel @Inject constructor(
    private val organizationRepository: OrganizationRepository,
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
            mutableStateFlow.update {
                it.copy(viewState = OrganizationPoliciesState.ViewState.Loading)
            }
            
            organizationRepository.getOrganizationPolicies(
                organizationId = stateFlow.value.organizationId,
            )
                .onSuccess { policies ->
                    mutableStateFlow.update {
                        it.copy(
                            viewState = if (policies.isEmpty()) {
                                OrganizationPoliciesState.ViewState.Empty
                            } else {
                                OrganizationPoliciesState.ViewState.Content(
                                    policies = policies.map { json ->
                                        OrganizationPoliciesState.PolicyItem(
                                            type = json.type,
                                            name = getPolicyName(json.type),
                                            enabled = json.enabled,
                                        )
                                    }.toImmutableList()
                                )
                            }
                        )
                    }
                }
                .onFailure {
                    mutableStateFlow.update {
                        it.copy(viewState = OrganizationPoliciesState.ViewState.Error)
                    }
                }
        }
    }
    
    private fun getPolicyName(type: Int): Text {
        return when (type) {
            0 -> BitwardenString.two_step_login.asText()
            1 -> BitwardenString.master_password.asText()
            2 -> BitwardenString.password_generator.asText()
            3 -> BitwardenString.single_org.asText()
            4 -> BitwardenString.require_sso.asText()
            5 -> BitwardenString.personal_ownership.asText()
            6 -> BitwardenString.send_options.asText()
            7 -> BitwardenString.reset_password.asText()
            8 -> BitwardenString.max_vault_timeout.asText()
            9 -> BitwardenString.disable_personal_vault_export.asText()
            else -> BitwardenString.policies.asText()
        }
    }
    
    override fun handleAction(action: OrganizationPoliciesAction) {
        when (action) {
            is OrganizationPoliciesAction.BackClick -> handleBackClick()
            is OrganizationPoliciesAction.DismissDialog -> handleDismissDialog()
            is OrganizationPoliciesAction.TogglePolicy -> handleTogglePolicy(action)
            is OrganizationPoliciesAction.RetryClick -> loadPolicies()
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
            // 保存当前状态以便失败时恢复
            val currentViewState = stateFlow.value.viewState
            
            mutableStateFlow.update {
                it.copy(
                    dialogState = OrganizationPoliciesState.DialogState.Loading(
                        message = BitwardenString.saving.asText(),
                    ),
                )
            }
            
            organizationRepository.updateOrganizationPolicy(
                organizationId = stateFlow.value.organizationId,
                policyType = action.type,
                enabled = action.enabled,
            )
                .onSuccess { updatedPolicy ->
                    val currentState = stateFlow.value.viewState
                    if (currentState is OrganizationPoliciesState.ViewState.Content) {
                        val updatedPolicies = currentState.policies.map { policy ->
                            if (policy.type == updatedPolicy.type) {
                                policy.copy(enabled = updatedPolicy.enabled)
                            } else {
                                policy
                            }
                        }.toImmutableList()
                        
                        mutableStateFlow.update {
                            it.copy(
                                viewState = OrganizationPoliciesState.ViewState.Content(updatedPolicies),
                                dialogState = null,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    // 失败时恢复原来的状态并显示错误
                    mutableStateFlow.update {
                        it.copy(
                            viewState = currentViewState,
                            dialogState = OrganizationPoliciesState.DialogState.Error(
                                message = error.message?.asText()
                                    ?: BitwardenString.an_error_has_occurred.asText(),
                            ),
                        )
                    }
                }
        }
    }
}
