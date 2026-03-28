package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections

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
}

/**
 * 组织集合列表 ViewModel
 */
@HiltViewModel
class OrganizationCollectionsViewModel @Inject constructor(
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
            // TODO: 调用 organizationManagementService.getOrganizationCollections
            mutableStateFlow.update {
                it.copy(
                    viewState = OrganizationCollectionsState.ViewState.Empty,
                )
            }
        }
    }
    
    override fun handleAction(action: OrganizationCollectionsAction) {
        when (action) {
            is OrganizationCollectionsAction.BackClick -> handleBackClick()
            is OrganizationCollectionsAction.AddClick -> handleAddClick()
            is OrganizationCollectionsAction.DismissDialog -> handleDismissDialog()
            is OrganizationCollectionsAction.DeleteCollectionClick -> handleDeleteCollectionClick(action)
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
}
