package com.x8bit.bitwarden.ui.platform.feature.settings.securitykeys

import androidx.lifecycle.viewModelScope
import com.bitwarden.network.model.TwoFactorWebAuthnCredential
import com.bitwarden.network.service.TwoFactorService
import com.bitwarden.ui.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SecurityKeysViewModel
@Inject
constructor(
        private val twoFactorService: TwoFactorService,
) :
        BaseViewModel<SecurityKeysState, SecurityKeysEvent, SecurityKeysAction>(
                initialState = SecurityKeysState(),
        ) {
        override fun handleAction(action: SecurityKeysAction) {
                when (action) {
                        is SecurityKeysAction.LoadKeys -> loadKeys(action.masterPasswordHash)
                        is SecurityKeysAction.RequestDelete ->
                                mutableStateFlow.value =
                                        mutableStateFlow.value.copy(confirmDeleteId = action.id)
                        is SecurityKeysAction.ConfirmDelete -> handleConfirmDelete(action)
                        SecurityKeysAction.DismissDeleteConfirm ->
                                mutableStateFlow.value =
                                        mutableStateFlow.value.copy(confirmDeleteId = null)
                        SecurityKeysAction.DismissDialog ->
                                mutableStateFlow.value = mutableStateFlow.value.copy(dialog = null)
                }
        }

        private fun loadKeys(masterPasswordHash: String) {
                mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true, error = null)
                viewModelScope.launch {
                        twoFactorService
                                .getWebAuthnConfig(masterPasswordHash)
                                .onSuccess {
                                        mutableStateFlow.value =
                                                mutableStateFlow.value.copy(
                                                        isLoading = false,
                                                        keys = it.keys.orEmpty(),
                                                        isEnabled = it.enabled,
                                                        masterPasswordHash = masterPasswordHash,
                                                )
                                }
                                .onFailure {
                                        mutableStateFlow.value =
                                                mutableStateFlow.value.copy(
                                                        isLoading = false,
                                                        error = "加载安全密钥失败，请检查密码是否正确。",
                                                )
                                }
                }
        }

        private fun handleConfirmDelete(action: SecurityKeysAction.ConfirmDelete) {
                val hash = mutableStateFlow.value.masterPasswordHash ?: return
                mutableStateFlow.value =
                        mutableStateFlow.value.copy(confirmDeleteId = null, isLoading = true)
                viewModelScope.launch {
                        twoFactorService
                                .deleteWebAuthnCredential(action.id, hash)
                                .onSuccess {
                                        mutableStateFlow.value =
                                                mutableStateFlow.value.copy(
                                                        isLoading = false,
                                                        keys = it.keys.orEmpty(),
                                                        isEnabled = it.enabled,
                                                        dialog =
                                                                SecurityKeysDialog.Info("安全密钥已删除。"),
                                                )
                                }
                                .onFailure {
                                        mutableStateFlow.value =
                                                mutableStateFlow.value.copy(
                                                        isLoading = false,
                                                        dialog =
                                                                SecurityKeysDialog.Error(
                                                                        "删除安全密钥失败，请重试。"
                                                                ),
                                                )
                                }
                }
        }
}

data class SecurityKeysState(
        val isLoading: Boolean = false,
        val keys: List<TwoFactorWebAuthnCredential> = emptyList(),
        val isEnabled: Boolean = false,
        val error: String? = null,
        val confirmDeleteId: Int? = null,
        val dialog: SecurityKeysDialog? = null,
        val masterPasswordHash: String? = null,
)

sealed class SecurityKeysDialog {
        data class Error(val message: String) : SecurityKeysDialog()
        data class Info(val message: String) : SecurityKeysDialog()
}

sealed class SecurityKeysEvent

sealed class SecurityKeysAction {
        data class LoadKeys(val masterPasswordHash: String) : SecurityKeysAction()
        data class RequestDelete(val id: Int) : SecurityKeysAction()
        data class ConfirmDelete(val id: Int) : SecurityKeysAction()
        data object DismissDeleteConfirm : SecurityKeysAction()
        data object DismissDialog : SecurityKeysAction()
}
