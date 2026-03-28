package com.x8bit.bitwarden.ui.platform.feature.settings.twostepverification

import androidx.lifecycle.viewModelScope
import com.bitwarden.network.model.TwoFactorWebAuthnCredential
import com.bitwarden.network.service.TwoFactorService
import com.bitwarden.ui.platform.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TwoStepVerificationViewModel @Inject constructor(
    private val twoFactorService: TwoFactorService,
) : BaseViewModel<TwoStepState, TwoStepEvent, TwoStepAction>(
    initialState = TwoStepState(),
) {
    override fun handleAction(action: TwoStepAction) {
        when (action) {
            is TwoStepAction.SelectMethod -> handleSelectMethod(action)
            is TwoStepAction.EnableTotp -> handleEnableTotp(action)
            is TwoStepAction.SendEmailCode -> handleSendEmailCode(action)
            is TwoStepAction.EnableEmail -> handleEnableEmail(action)
            is TwoStepAction.LoadDuoConfig -> handleLoadDuoConfig(action)
            is TwoStepAction.EnableDuo -> handleEnableDuo(action)
            is TwoStepAction.LoadYubiKeyConfig -> handleLoadYubiKeyConfig(action)
            is TwoStepAction.EnableYubiKey -> handleEnableYubiKey(action)
            is TwoStepAction.LoadWebAuthnConfig -> handleLoadWebAuthnConfig(action)
            is TwoStepAction.DeleteWebAuthnCredential -> handleDeleteWebAuthnCredential(action)
            is TwoStepAction.ViewRecoveryCode -> handleViewRecoveryCode(action)
            TwoStepAction.DismissDialog -> mutableStateFlow.value =
                mutableStateFlow.value.copy(dialog = null)
        }
    }

    private fun handleSelectMethod(action: TwoStepAction.SelectMethod) {
        mutableStateFlow.value = mutableStateFlow.value.copy(
            selectedMethod = action.method,
            totpKey = null,
            duoHost = null,
            duoIntegrationKey = null,
            yubiKeyEnabled = false,
            webAuthnCredentials = null,
        )
        when (action.method) {
            TwoStepMethod.TOTP -> loadTotpKey(action.masterPasswordHash)
            TwoStepMethod.DUO -> sendAction(TwoStepAction.LoadDuoConfig(action.masterPasswordHash))
            TwoStepMethod.YUBIKEY -> sendAction(TwoStepAction.LoadYubiKeyConfig(action.masterPasswordHash))
            TwoStepMethod.WEBAUTHN -> sendAction(TwoStepAction.LoadWebAuthnConfig(action.masterPasswordHash))
            TwoStepMethod.EMAIL -> Unit
        }
    }

    private fun loadTotpKey(masterPasswordHash: String) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.getAuthenticatorKey(masterPasswordHash)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, totpKey = it.key) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("获取 TOTP 密钥失败，请检查密码是否正确。")) }
        }
    }

    private fun handleEnableTotp(action: TwoStepAction.EnableTotp) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.enableAuthenticator(action.masterPasswordHash, action.token)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Success("验证器 App 两步验证已成功启用。")) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("验证码无效，请重试。")) }
        }
    }

    private fun handleSendEmailCode(action: TwoStepAction.SendEmailCode) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.sendEmailSetupCode(action.masterPasswordHash)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Info("验证码已发送到您的邮箱。")) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("发送验证码失败，请重试。")) }
        }
    }

    private fun handleEnableEmail(action: TwoStepAction.EnableEmail) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.enableEmail(action.masterPasswordHash, action.token)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Success("邮件两步验证已成功启用。")) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("验证码无效，请重试。")) }
        }
    }

    private fun handleLoadDuoConfig(action: TwoStepAction.LoadDuoConfig) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.getDuoConfig(action.masterPasswordHash)
                .onSuccess {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        duoHost = it.host.orEmpty(),
                        duoIntegrationKey = it.integrationKey.orEmpty(),
                        duoEnabled = it.enabled,
                    )
                }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("获取 Duo 配置失败，请检查密码是否正确。")) }
        }
    }

    private fun handleEnableDuo(action: TwoStepAction.EnableDuo) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.enableDuo(action.masterPasswordHash, action.host, action.secretKey, action.integrationKey)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Success("Duo 两步验证已成功启用。")) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("启用 Duo 失败，请检查配置信息。")) }
        }
    }

    private fun handleLoadYubiKeyConfig(action: TwoStepAction.LoadYubiKeyConfig) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.getYubiKeyConfig(action.masterPasswordHash)
                .onSuccess {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        yubiKeyEnabled = it.enabled,
                        yubiKeys = listOfNotNull(it.key1, it.key2, it.key3, it.key4, it.key5),
                    )
                }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("获取 YubiKey 配置失败，请检查密码是否正确。")) }
        }
    }

    private fun handleEnableYubiKey(action: TwoStepAction.EnableYubiKey) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.enableYubiKey(action.masterPasswordHash, action.keys, action.nfc)
                .onSuccess { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Success("YubiKey 两步验证已成功启用。")) }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("启用 YubiKey 失败，请重试。")) }
        }
    }

    private fun handleLoadWebAuthnConfig(action: TwoStepAction.LoadWebAuthnConfig) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.getWebAuthnConfig(action.masterPasswordHash)
                .onSuccess {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        webAuthnCredentials = it.keys.orEmpty(),
                        webAuthnEnabled = it.enabled,
                    )
                }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("获取通行密钥配置失败，请检查密码是否正确。")) }
        }
    }

    private fun handleDeleteWebAuthnCredential(action: TwoStepAction.DeleteWebAuthnCredential) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.deleteWebAuthnCredential(action.id, action.masterPasswordHash)
                .onSuccess {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        webAuthnCredentials = it.keys.orEmpty(),
                        webAuthnEnabled = it.enabled,
                        dialog = TwoStepDialog.Info("通行密钥已删除。"),
                    )
                }
                .onFailure { mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = false, dialog = TwoStepDialog.Error("删除通行密钥失败，请重试。")) }
        }
    }

    private fun handleViewRecoveryCode(action: TwoStepAction.ViewRecoveryCode) {
        mutableStateFlow.value = mutableStateFlow.value.copy(isLoading = true)
        viewModelScope.launch {
            twoFactorService.getRecoveryCode(action.masterPasswordHash)
                .onSuccess {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        dialog = TwoStepDialog.RecoveryCode(it.code),
                    )
                }
                .onFailure {
                    mutableStateFlow.value = mutableStateFlow.value.copy(
                        isLoading = false,
                        dialog = TwoStepDialog.Error("获取恢复代码失败，请检查密码是否正确。"),
                    )
                }
        }
    }
}

data class TwoStepState(
    val selectedMethod: TwoStepMethod? = null,
    val isLoading: Boolean = false,
    val dialog: TwoStepDialog? = null,
    // TOTP
    val totpKey: String? = null,
    // Duo
    val duoHost: String? = null,
    val duoIntegrationKey: String? = null,
    val duoEnabled: Boolean = false,
    // YubiKey
    val yubiKeyEnabled: Boolean = false,
    val yubiKeys: List<String> = emptyList(),
    // WebAuthn
    val webAuthnEnabled: Boolean = false,
    val webAuthnCredentials: List<TwoFactorWebAuthnCredential>? = null,
)

sealed class TwoStepDialog {
    data class Success(val message: String) : TwoStepDialog()
    data class Error(val message: String) : TwoStepDialog()
    data class Info(val message: String) : TwoStepDialog()
    data class RecoveryCode(val code: String) : TwoStepDialog()
}

sealed class TwoStepEvent

sealed class TwoStepAction {
    data class SelectMethod(val method: TwoStepMethod, val masterPasswordHash: String) : TwoStepAction()
    // TOTP
    data class EnableTotp(val masterPasswordHash: String, val token: String) : TwoStepAction()
    // Email
    data class SendEmailCode(val masterPasswordHash: String) : TwoStepAction()
    data class EnableEmail(val masterPasswordHash: String, val token: String) : TwoStepAction()
    // Duo
    data class LoadDuoConfig(val masterPasswordHash: String) : TwoStepAction()
    data class EnableDuo(val masterPasswordHash: String, val host: String, val secretKey: String, val integrationKey: String) : TwoStepAction()
    // YubiKey
    data class LoadYubiKeyConfig(val masterPasswordHash: String) : TwoStepAction()
    data class EnableYubiKey(val masterPasswordHash: String, val keys: List<String?>, val nfc: Boolean) : TwoStepAction()
    // WebAuthn
    data class LoadWebAuthnConfig(val masterPasswordHash: String) : TwoStepAction()
    data class DeleteWebAuthnCredential(val id: Int, val masterPasswordHash: String) : TwoStepAction()

    // Recovery Code
    data class ViewRecoveryCode(val masterPasswordHash: String) : TwoStepAction()

    data object DismissDialog : TwoStepAction()
}

enum class TwoStepMethod {
    TOTP,
    EMAIL,
    DUO,
    YUBIKEY,
    WEBAUTHN,
}
