package com.x8bit.bitwarden.ui.platform.feature.settings.changeusername

import androidx.lifecycle.viewModelScope
import com.bitwarden.network.service.AccountsService
import com.bitwarden.ui.platform.base.BaseViewModel
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.auth.repository.util.toUpdatedProfileNameJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeUsernameViewModel @Inject constructor(
    private val accountsService: AccountsService,
    private val authDiskSource: AuthDiskSource,
) : BaseViewModel<ChangeUsernameState, ChangeUsernameEvent, ChangeUsernameAction>(
    initialState = ChangeUsernameState(
        currentName = authDiskSource.userState?.activeAccount?.profile?.name.orEmpty(),
    ),
) {
    override fun handleAction(action: ChangeUsernameAction) {
        when (action) {
            is ChangeUsernameAction.NameChange -> mutableStateFlow.value =
                mutableStateFlow.value.copy(newName = action.value)
            ChangeUsernameAction.Save -> handleSave()
            ChangeUsernameAction.DismissDialog -> mutableStateFlow.value =
                mutableStateFlow.value.copy(dialog = null)
        }
    }

    private fun handleSave() {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            accountsService.updateProfile(name = mutableStateFlow.value.newName.takeIf { it.isNotBlank() })
                .onSuccess { updatedName ->
                    // 更新本地存储的用户状态
                    authDiskSource.userState?.let { currentUserState ->
                        authDiskSource.userState = currentUserState.toUpdatedProfileNameJson(newName = updatedName)
                    }
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        currentName = updatedName.orEmpty(),
                        dialog = ChangeUsernameDialog.Success("用户名已成功更改。"),
                    )
                }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        dialog = ChangeUsernameDialog.Error("更改用户名失败，请重试。"),
                    )
                }
        }
    }
}

data class ChangeUsernameState(
    val currentName: String = "",
    val newName: String = "",
    val isLoading: Boolean = false,
    val dialog: ChangeUsernameDialog? = null,
)

sealed class ChangeUsernameDialog {
    data class Error(val message: String) : ChangeUsernameDialog()
    data class Success(val message: String) : ChangeUsernameDialog()
}

sealed class ChangeUsernameEvent

sealed class ChangeUsernameAction {
    data class NameChange(val value: String) : ChangeUsernameAction()
    data object Save : ChangeUsernameAction()
    data object DismissDialog : ChangeUsernameAction()
}
