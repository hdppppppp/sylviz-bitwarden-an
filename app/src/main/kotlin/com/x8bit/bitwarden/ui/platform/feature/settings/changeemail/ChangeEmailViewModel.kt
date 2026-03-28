package com.x8bit.bitwarden.ui.platform.feature.settings.changeemail

import androidx.lifecycle.viewModelScope
import com.bitwarden.network.service.AccountsService
import com.bitwarden.ui.platform.base.BaseViewModel
import com.x8bit.bitwarden.data.auth.datasource.disk.AuthDiskSource
import com.x8bit.bitwarden.data.auth.datasource.sdk.AuthSdkSource
import com.bitwarden.crypto.HashPurpose
import com.x8bit.bitwarden.data.auth.repository.util.toSdkParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val accountsService: AccountsService,
    private val authDiskSource: AuthDiskSource,
    private val authSdkSource: AuthSdkSource,
) : BaseViewModel<ChangeEmailState, ChangeEmailEvent, ChangeEmailAction>(
    initialState = ChangeEmailState(),
) {
    private val activeEmail: String
        get() = authDiskSource.userState?.activeAccount?.profile?.email.orEmpty()

    override fun handleAction(action: ChangeEmailAction) {
        when (action) {
            is ChangeEmailAction.MasterPasswordChange -> mutableStateFlow.value =
                mutableStateFlow.value.copy(masterPassword = action.value)
            is ChangeEmailAction.NewEmailChange -> mutableStateFlow.value =
                mutableStateFlow.value.copy(newEmail = action.value)
            is ChangeEmailAction.TokenChange -> mutableStateFlow.value =
                mutableStateFlow.value.copy(token = action.value)
            ChangeEmailAction.SendToken -> handleSendToken()
            ChangeEmailAction.ConfirmChange -> handleConfirmChange()
            ChangeEmailAction.DismissDialog -> mutableStateFlow.value =
                mutableStateFlow.value.copy(dialog = null)
        }
    }

    private fun handleSendToken() {
        val state = mutableStateFlow.value
        if (state.masterPassword.isBlank()) {
            mutableStateFlow.value = state.copy(dialog = ChangeEmailDialog.Error("必须输入主密码。"))
            return
        }
        if (state.newEmail.isBlank()) {
            mutableStateFlow.value = state.copy(dialog = ChangeEmailDialog.Error("必须输入新电子邮箱。"))
            return
        }
        mutableStateFlow.value = state.copy(isLoading = true)
        viewModelScope.launch {
            hashPassword(state.masterPassword)
                .onSuccess { hash ->
                    accountsService.requestEmailToken(hash, state.newEmail)
                        .onSuccess {
                            mutableStateFlow.value = mutableStateFlow.value.copy(
                                isLoading = false,
                                tokenSent = true,
                                dialog = ChangeEmailDialog.Info("验证码已发送到 ${state.newEmail}，请查收。"),
                            )
                        }
                        .onFailure {
                            mutableStateFlow.value = mutableStateFlow.value.copy(
                                isLoading = false,
                                dialog = ChangeEmailDialog.Error("发送验证码失败，请检查邮箱地址和密码是否正确。"),
                            )
                        }
                }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        dialog = ChangeEmailDialog.Error("密码验证失败，请重试。"),
                    )
                }
        }
    }

    private fun handleConfirmChange() {
        val state = mutableStateFlow.value
        if (state.token.isBlank()) {
            mutableStateFlow.value = state.copy(dialog = ChangeEmailDialog.Error("必须输入验证码。"))
            return
        }
        mutableStateFlow.value = state.copy(isLoading = true)
        viewModelScope.launch {
            hashPassword(state.masterPassword)
                .onSuccess { hash ->
                    accountsService.changeEmail(hash, state.newEmail, state.token)
                        .onSuccess {
                            mutableStateFlow.value = mutableStateFlow.value.copy(
                                isLoading = false,
                                dialog = ChangeEmailDialog.Success("电子邮箱已成功更改为 ${state.newEmail}。"),
                            )
                        }
                        .onFailure {
                            mutableStateFlow.value = mutableStateFlow.value.copy(
                                isLoading = false,
                                dialog = ChangeEmailDialog.Error("更改失败，请检查验证码是否正确。"),
                            )
                        }
                }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        dialog = ChangeEmailDialog.Error("密码验证失败，请重试。"),
                    )
                }
        }
    }

    private suspend fun hashPassword(password: String): Result<String> {
        val account = authDiskSource.userState?.activeAccount ?: return Result.failure(Exception("No active account"))
        return authSdkSource.hashPassword(
            email = account.profile.email,
            password = password,
            kdf = account.profile.toSdkParams(),
            purpose = HashPurpose.SERVER_AUTHORIZATION,
        )
    }
}

data class ChangeEmailState(
    val masterPassword: String = "",
    val newEmail: String = "",
    val token: String = "",
    val isLoading: Boolean = false,
    val tokenSent: Boolean = false,
    val dialog: ChangeEmailDialog? = null,
)

sealed class ChangeEmailDialog {
    data class Error(val message: String) : ChangeEmailDialog()
    data class Info(val message: String) : ChangeEmailDialog()
    data class Success(val message: String) : ChangeEmailDialog()
}

sealed class ChangeEmailEvent

sealed class ChangeEmailAction {
    data class MasterPasswordChange(val value: String) : ChangeEmailAction()
    data class NewEmailChange(val value: String) : ChangeEmailAction()
    data class TokenChange(val value: String) : ChangeEmailAction()
    data object SendToken : ChangeEmailAction()
    data object ConfirmChange : ChangeEmailAction()
    data object DismissDialog : ChangeEmailAction()
}
