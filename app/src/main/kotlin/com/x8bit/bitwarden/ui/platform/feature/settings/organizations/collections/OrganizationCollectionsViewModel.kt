package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections

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

/**
 * 组织集合列表状态
 */
@Parcelize
data class OrganizationCollectionsState(
    val organizationId: String,
    val viewState: ViewState,
    val dialogState: DialogState?,
) : Parcelable {
    
    sealed class ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState()
        
        @Parcelize
        data class Content(
            val collections: ImmutableList<CollectionItem>,
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
        data class ConfirmDelete(val collectionId: String) : DialogState()
    }
    
    @Parcelize
    data class CollectionItem(
        val id: String,
        val name: String,
    ) : Parcelable
}

sealed class OrganizationCollectionsEvent {
    data object NavigateBack : OrganizationCollectionsEvent()
}

sealed class OrganizationCollectionsAction {
    data object BackClick : OrganizationCollectionsAction()
    data object AddClick : OrganizationCollectionsAction()
    data object DismissDialog : OrganizationCollectionsAction()
    data class DeleteCollectionClick(val collectionId: String) : OrganizationCollectionsAction()
    data class ConfirmDeleteCollection(val collectionId: String) : OrganizationCollectionsAction()
    data object RetryClick : OrganizationCollectionsAction()
}

/**
 * 组织集合列表 ViewModel
 */
@HiltViewModel
class OrganizationCollectionsViewModel @Inject constructor(
    private val organizationRepository: OrganizationRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationCollectionsState, OrganizationCollectionsEvent, OrganizationCollectionsAction>(
    initialState = OrganizationCollectionsState(
        organizationId = savedStateHandle.toOrganizationCollectionsArgs().organizationId,
        viewState = OrganizationCollectionsState.ViewState.Loading,
        dialogState = null,
    ),
) {
    
    init {
        loadCollections()
    }
    
    private fun loadCollections() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(viewState = OrganizationCollectionsState.ViewState.Loading)
            }
            
            organizationRepository.getOrganizationCollections(
                organizationId = stateFlow.value.organizationId,
            )
                .onSuccess { collections ->
                    mutableStateFlow.update {
                        it.copy(
                            viewState = if (collections.isEmpty()) {
                                OrganizationCollectionsState.ViewState.Empty
                            } else {
                                OrganizationCollectionsState.ViewState.Content(
                                    collections = collections.map { json ->
                                        OrganizationCollectionsState.CollectionItem(
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
                        it.copy(viewState = OrganizationCollectionsState.ViewState.Error)
                    }
                }
        }
    }
    
    override fun handleAction(action: OrganizationCollectionsAction) {
        when (action) {
            is OrganizationCollectionsAction.BackClick -> handleBackClick()
            is OrganizationCollectionsAction.AddClick -> handleAddClick()
            is OrganizationCollectionsAction.DismissDialog -> handleDismissDialog()
            is OrganizationCollectionsAction.DeleteCollectionClick -> handleDeleteCollectionClick(action)
            is OrganizationCollectionsAction.ConfirmDeleteCollection -> handleConfirmDeleteCollection(action)
            is OrganizationCollectionsAction.RetryClick -> loadCollections()
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationCollectionsEvent.NavigateBack)
    }
    
    private fun handleAddClick() {
        // TODO: 打开新建集合对话框
    }
    
    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }
    
    private fun handleDeleteCollectionClick(action: OrganizationCollectionsAction.DeleteCollectionClick) {
        mutableStateFlow.update {
            it.copy(
                dialogState = OrganizationCollectionsState.DialogState.ConfirmDelete(action.collectionId),
            )
        }
    }
    
    private fun handleConfirmDeleteCollection(action: OrganizationCollectionsAction.ConfirmDeleteCollection) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = OrganizationCollectionsState.DialogState.Loading(
                        message = BitwardenString.deleting.asText(),
                    ),
                )
            }
            
            organizationRepository.deleteOrganizationCollection(
                organizationId = stateFlow.value.organizationId,
                collectionId = action.collectionId,
            )
                .onSuccess {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    loadCollections()
                }
                .onFailure { error ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = OrganizationCollectionsState.DialogState.Error(
                                message = error.message?.asText()
                                    ?: BitwardenString.an_error_has_occurred.asText(),
                            ),
                        )
                    }
                }
        }
    }
}
