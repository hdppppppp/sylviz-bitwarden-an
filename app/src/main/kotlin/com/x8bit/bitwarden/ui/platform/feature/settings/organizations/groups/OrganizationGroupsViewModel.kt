package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.groups

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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class OrganizationGroupsState(
    val organizationId: String,
    val viewState: ViewState,
    val dialogState: DialogState?,
) : Parcelable {
    
    sealed class ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState()
        
        @Parcelize
        data class Content(
            val groups: ImmutableList<GroupItem>,
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
        
        @Parcelize
        data class ConfirmDelete(val groupId: String) : DialogState()
    }
    
    @Parcelize
    data class GroupItem(
        val id: String,
        val name: String,
    ) : Parcelable
}

sealed class OrganizationGroupsEvent {
    data object NavigateBack : OrganizationGroupsEvent()
}

sealed class OrganizationGroupsAction {
    data object BackClick : OrganizationGroupsAction()
    data object AddClick : OrganizationGroupsAction()
    data object DismissDialog : OrganizationGroupsAction()
    data class DeleteGroupClick(val groupId: String) : OrganizationGroupsAction()
    data class ConfirmDeleteGroup(val groupId: String) : OrganizationGroupsAction()
    data object RetryClick : OrganizationGroupsAction()
}

@HiltViewModel
class OrganizationGroupsViewModel @Inject constructor(
    private val organizationRepository: OrganizationRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationGroupsState, OrganizationGroupsEvent, OrganizationGroupsAction>(
    initialState = OrganizationGroupsState(
        organizationId = savedStateHandle.toOrganizationGroupsArgs().organizationId,
        viewState = OrganizationGroupsState.ViewState.Loading,
        dialogState = null,
    ),
) {
    
    init {
        loadGroups()
    }
    
    private fun loadGroups() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = OrganizationGroupsState.ViewState.Loading)
            }
            
            organizationRepository.getOrganizationGroups(
                organizationId = stateFlow.value.organizationId,
            )
                .onSuccess { groups ->
                    mutableStateFlow.update {
                        it.copy(
                            viewState = if (groups.isEmpty()) {
                                OrganizationGroupsState.ViewState.Empty
                            } else {
                                OrganizationGroupsState.ViewState.Content(
                                    groups = groups.map { json ->
                                        OrganizationGroupsState.GroupItem(
                                            id = json.id,
                                            name = json.name,
                                        )
                                    }.toImmutableList()
                                )
                            }
                        )
                    }
                }
                .onFailure {
                    mutableStateFlow.update {
                        it.copy(viewState = OrganizationGroupsState.ViewState.Error)
                    }
                }
        }
    }
    
    override fun handleAction(action: OrganizationGroupsAction) {
        when (action) {
            is OrganizationGroupsAction.BackClick -> handleBackClick()
            is OrganizationGroupsAction.AddClick -> handleAddClick()
            is OrganizationGroupsAction.DismissDialog -> handleDismissDialog()
            is OrganizationGroupsAction.DeleteGroupClick -> handleDeleteGroupClick(action)
            is OrganizationGroupsAction.ConfirmDeleteGroup -> handleConfirmDeleteGroup(action)
            is OrganizationGroupsAction.RetryClick -> loadGroups()
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationGroupsEvent.NavigateBack)
    }
    
    private fun handleAddClick() {
        // TODO: 打开新建组对话框
    }
    
    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }
    
    private fun handleDeleteGroupClick(action: OrganizationGroupsAction.DeleteGroupClick) {
        mutableStateFlow.update {
            it.copy(
                dialogState = OrganizationGroupsState.DialogState.ConfirmDelete(action.groupId),
            )
        }
    }
    
    private fun handleConfirmDeleteGroup(action: OrganizationGroupsAction.ConfirmDeleteGroup) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = OrganizationGroupsState.DialogState.Loading(
                        message = BitwardenString.deleting.asText(),
                    ),
                )
            }
            
            organizationRepository.deleteOrganizationGroup(
                organizationId = stateFlow.value.organizationId,
                groupId = action.groupId,
            )
                .onSuccess {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    loadGroups()
                }
                .onFailure { error ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = OrganizationGroupsState.DialogState.Error(
                                message = error.message?.asText()
                                    ?: BitwardenString.an_error_has_occurred.asText(),
                            ),
                        )
                    }
                }
        }
    }
}
