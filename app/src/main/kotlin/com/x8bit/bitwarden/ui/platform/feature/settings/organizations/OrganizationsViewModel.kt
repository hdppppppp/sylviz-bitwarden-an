package com.x8bit.bitwarden.ui.platform.feature.settings.organizations

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * 组织列表状态
 */
@Parcelize
data class OrganizationsState(
    val viewState: ViewState,
) : Parcelable {
    
    /**
     * 视图状态
     */
    sealed class ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState()
        
        @Parcelize
        data class Content(
            val organizations: List<OrganizationItem>,
        ) : ViewState()
        
        @Parcelize
        data object Empty : ViewState()
    }
    
    /**
     * 组织列表项
     */
    @Parcelize
    data class OrganizationItem(
        val id: String,
        val name: Text,
    ) : Parcelable
}

/**
 * 组织列表事件
 */
sealed class OrganizationsEvent {
    data object NavigateBack : OrganizationsEvent()
    data class NavigateToOrganizationDetail(
        val organizationId: String,
        val organizationName: String,
    ) : OrganizationsEvent()
}

/**
 * 组织列表动作
 */
sealed class OrganizationsAction {
    data object BackClick : OrganizationsAction()
    data class OrganizationClick(
        val organizationId: String,
        val organizationName: String,
    ) : OrganizationsAction()
}

/**
 * 组织列表 ViewModel
 */
@HiltViewModel
class OrganizationsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<OrganizationsState, OrganizationsEvent, OrganizationsAction>(
    initialState = OrganizationsState(
        viewState = OrganizationsState.ViewState.Loading,
    ),
) {
    
    init {
        authRepository.userStateFlow
            .onEach { userState ->
                val organizations = userState?.activeAccount?.organizations?.map { org ->
                    OrganizationsState.OrganizationItem(
                        id = org.id,
                        name = org.name.asText(),
                    )
                } ?: emptyList()
                
                mutableStateFlow.update {
                    it.copy(
                        viewState = if (organizations.isEmpty()) {
                            OrganizationsState.ViewState.Empty
                        } else {
                            OrganizationsState.ViewState.Content(
                                organizations = organizations,
                            )
                        },
                    )
                }
            }
            .launchIn(viewModelScope)
    }
    
    override fun handleAction(action: OrganizationsAction) {
        when (action) {
            is OrganizationsAction.BackClick -> handleBackClick()
            is OrganizationsAction.OrganizationClick -> handleOrganizationClick(action)
        }
    }
    
    private fun handleBackClick() {
        sendEvent(OrganizationsEvent.NavigateBack)
    }
    
    private fun handleOrganizationClick(action: OrganizationsAction.OrganizationClick) {
        sendEvent(
            OrganizationsEvent.NavigateToOrganizationDetail(
                organizationId = action.organizationId,
                organizationName = action.organizationName,
            ),
        )
    }
}
