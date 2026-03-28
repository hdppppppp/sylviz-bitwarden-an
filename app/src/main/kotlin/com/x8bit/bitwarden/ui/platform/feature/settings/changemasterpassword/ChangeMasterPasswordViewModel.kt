package com.x8bit.bitwarden.ui.platform.feature.settings.changemasterpassword

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bitwarden.ui.platform.base.BaseViewModel
import com.bitwarden.ui.platform.base.util.orNullIfBlank
import com.bitwarden.ui.platform.resource.BitwardenPlurals
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.Text
import com.bitwarden.ui.util.asPluralsText
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.data.auth.datasource.sdk.model.PasswordStrength
import com.x8bit.bitwarden.data.auth.repository.AuthRepository
import com.x8bit.bitwarden.data.auth.repository.model.PasswordStrengthResult
import com.x8bit.bitwarden.data.auth.repository.model.ResetPasswordResult
import com.x8bit.bitwarden.data.auth.repository.model.ValidatePasswordResult
import com.x8bit.bitwarden.ui.auth.feature.completeregistration.PasswordStrengthState
import com.x8bit.bitwarden.ui.auth.feature.resetpassword.util.toDisplayLabels
import com.x8bit.bitwarden.ui.tools.feature.generator.util.toStrictestPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

private const val KEY_STATE = "state"
private const val MIN_PASSWORD_LENGTH = 12

/**
 * 管理修改主密码页面的状态。
 */
@HiltViewModel
@Suppress("TooManyFunctions")
class ChangeMasterPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ChangeMasterPasswordState, ChangeMasterPasswordEvent, ChangeMasterPasswordAction>(
    initialState = savedStateHandle[KEY_STATE]
        ?: run {
            val policies = authRepository.passwordPolicies
            ChangeMasterPasswordState(
                policies = policies.toDisplayLabels(),
                dialogState = null,
                currentPasswordInput = "",
                passwordInput = "",
                retypePasswordInput = "",
                passwordHintInput = "",
                passwordStrengthState = PasswordStrengthState.NONE,
                minimumPasswordLength = policies
                    .toStrictestPolicy()
                    .minLength
                    ?: MIN_PASSWORD_LENGTH,
                hasMasterPassword = authRepository.userStateFlow.value?.activeAccount?.hasMasterPassword ?: true,
            )
        },
) {
    /**
     * 跟踪获取密码强度的异步请求。用户输入变化时应取消。
     */
    private var passwordStrengthJob: Job = Job().apply { complete() }

    init {
        // 状态更新时写入 saved state handle
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: ChangeMasterPasswordAction) {
        when (action) {
            ChangeMasterPasswordAction.BackClick -> handleBackClick()
            ChangeMasterPasswordAction.SubmitClick -> handleSubmitClicked()
            ChangeMasterPasswordAction.DialogDismiss -> handleDialogDismiss()

            is ChangeMasterPasswordAction.CurrentPasswordInputChanged -> {
                handleCurrentPasswordInputChanged(action)
            }

            is ChangeMasterPasswordAction.PasswordInputChanged -> handlePasswordInputChanged(action)

            is ChangeMasterPasswordAction.RetypePasswordInputChanged -> {
                handleRetypePasswordInputChanged(action)
            }

            is ChangeMasterPasswordAction.PasswordHintInputChanged -> {
                handlePasswordHintInputChanged(action)
            }

            is ChangeMasterPasswordAction.Internal.ReceiveResetPasswordResult -> {
                handleReceiveResetPasswordResult(action)
            }

            is ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordAgainstPoliciesResult -> {
                handleReceiveValidatePasswordAgainstPoliciesResult(action)
            }

            is ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordResult -> {
                handleReceiveValidatePasswordResult(action)
            }

            is ChangeMasterPasswordAction.Internal.ReceivePasswordStrengthResult -> {
                handlePasswordStrengthResult(action)
            }
        }
    }

    private fun handleBackClick() {
        sendEvent(ChangeMasterPasswordEvent.NavigateBack)
    }

    private fun checkPasswordStrength(input: String) {
        // 更新密码强度
        passwordStrengthJob.cancel()
        if (input.isEmpty()) {
            mutableStateFlow.update {
                it.copy(passwordStrengthState = PasswordStrengthState.NONE)
            }
        } else {
            passwordStrengthJob = viewModelScope.launch {
                val result = authRepository.getPasswordStrength(
                    password = input,
                )
                trySendAction(ChangeMasterPasswordAction.Internal.ReceivePasswordStrengthResult(result))
            }
        }
    }

    private fun handlePasswordStrengthResult(
        action: ChangeMasterPasswordAction.Internal.ReceivePasswordStrengthResult,
    ) {
        when (val result = action.result) {
            is PasswordStrengthResult.Success -> {
                val updatedState = when (result.passwordStrength) {
                    PasswordStrength.LEVEL_0 -> PasswordStrengthState.WEAK_1
                    PasswordStrength.LEVEL_1 -> PasswordStrengthState.WEAK_2
                    PasswordStrength.LEVEL_2 -> PasswordStrengthState.WEAK_3
                    PasswordStrength.LEVEL_3 -> PasswordStrengthState.GOOD
                    PasswordStrength.LEVEL_4 -> PasswordStrengthState.STRONG
                }
                mutableStateFlow.update {
                    it.copy(
                        passwordStrengthState = updatedState,
                    )
                }
            }

            is PasswordStrengthResult.Error -> Unit
        }
    }

    /**
     * 用户点击提交时验证密码。
     */
    private fun handleSubmitClicked() {
        // 如果用户有主密码，验证当前密码字段
        if (state.hasMasterPassword && state.currentPasswordInput.isBlank()) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ChangeMasterPasswordState.DialogState.Error(
                        title = BitwardenString.an_error_has_occurred.asText(),
                        message = BitwardenString.validation_field_required
                            .asText(BitwardenString.current_master_password_required.asText()),
                    ),
                )
            }
            return
        }

        // 新密码字段为空时显示错误
        if (state.passwordInput.isBlank()) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ChangeMasterPasswordState.DialogState.Error(
                        title = BitwardenString.an_error_has_occurred.asText(),
                        message = BitwardenString.validation_field_required
                            .asText(BitwardenString.master_password.asText()),
                    ),
                )
            }
            return
        }

        // 检查密码策略
        if (state.policies.isNotEmpty()) {
            viewModelScope.launch {
                val result = authRepository.validatePasswordAgainstPolicies(state.passwordInput)
                sendAction(
                    ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordAgainstPoliciesResult(
                        result,
                    ),
                )
            }
        } else if (state.passwordInput.length < state.minimumPasswordLength) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ChangeMasterPasswordState.DialogState.Error(
                        title = BitwardenString.an_error_has_occurred.asText(),
                        message = BitwardenPlurals.master_password_length_val_message_x
                            .asPluralsText(
                                quantity = state.minimumPasswordLength,
                                args = arrayOf(state.minimumPasswordLength),
                            ),
                    ),
                )
            }
        } else {
            // 检查确认密码是否匹配
            if (!checkRetypedPassword()) return

            // 如果用户有主密码，先验证当前密码
            if (state.hasMasterPassword) {
                validateCurrentPassword()
            } else {
                // 无主密码用户直接修改
                changePassword()
            }
        }
    }

    /**
     * 关闭对话框。
     */
    private fun handleDialogDismiss() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
            )
        }
    }

    /**
     * 更新当前密码输入状态。
     */
    private fun handleCurrentPasswordInputChanged(
        action: ChangeMasterPasswordAction.CurrentPasswordInputChanged,
    ) {
        mutableStateFlow.update {
            it.copy(
                currentPasswordInput = action.input,
            )
        }
    }

    /**
     * 更新新密码输入状态。
     */
    private fun handlePasswordInputChanged(action: ChangeMasterPasswordAction.PasswordInputChanged) {
        mutableStateFlow.update {
            it.copy(
                passwordInput = action.input,
            )
        }
        checkPasswordStrength(input = action.input)
    }

    /**
     * 更新确认密码输入状态。
     */
    private fun handleRetypePasswordInputChanged(
        action: ChangeMasterPasswordAction.RetypePasswordInputChanged,
    ) {
        mutableStateFlow.update {
            it.copy(
                retypePasswordInput = action.input,
            )
        }
    }

    /**
     * 更新密码提示输入状态。
     */
    private fun handlePasswordHintInputChanged(
        action: ChangeMasterPasswordAction.PasswordHintInputChanged,
    ) {
        mutableStateFlow.update {
            it.copy(
                passwordHintInput = action.input,
            )
        }
    }

    /**
     * 处理修改密码结果。
     */
    private fun handleReceiveResetPasswordResult(
        action: ChangeMasterPasswordAction.Internal.ReceiveResetPasswordResult,
    ) {
        // 结束加载状态
        mutableStateFlow.update { it.copy(dialogState = null) }

        when (val result = action.result) {
            // 显示错误
            is ResetPasswordResult.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ChangeMasterPasswordState.DialogState.Error(
                            title = BitwardenString.an_error_has_occurred.asText(),
                            message = BitwardenString.generic_error_message.asText(),
                            error = result.error,
                        ),
                    )
                }
            }

            // 成功 - 显示成功提示并返回
            ResetPasswordResult.Success -> {
                sendEvent(ChangeMasterPasswordEvent.ShowToast(
                    message = BitwardenString.updated_master_password.asText(),
                ))
                sendEvent(ChangeMasterPasswordEvent.NavigateBack)
            }
        }
    }

    /**
     * 验证当前密码结果处理。
     */
    private fun handleReceiveValidatePasswordResult(
        action: ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordResult,
    ) {
        when (val result = action.result) {
            is ValidatePasswordResult.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ChangeMasterPasswordState.DialogState.Error(
                            title = BitwardenString.an_error_has_occurred.asText(),
                            message = BitwardenString.generic_error_message.asText(),
                            error = result.error,
                        ),
                    )
                }
            }

            is ValidatePasswordResult.Success -> {
                if (!result.isValid) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChangeMasterPasswordState.DialogState.Error(
                                title = BitwardenString.an_error_has_occurred.asText(),
                                message = BitwardenString.invalid_master_password.asText(),
                            ),
                        )
                    }
                } else {
                    changePassword()
                }
            }
        }
    }

    /**
     * 处理密码策略验证结果。
     */
    private fun handleReceiveValidatePasswordAgainstPoliciesResult(
        action: ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordAgainstPoliciesResult,
    ) {
        if (!action.meetsRequirements) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ChangeMasterPasswordState.DialogState.Error(
                        title = BitwardenString.master_password_policy_validation_title.asText(),
                        message = BitwardenString.master_password_policy_validation_message
                            .asText(),
                    ),
                )
            }
            return
        }

        // 检查确认密码是否匹配
        if (!checkRetypedPassword()) return

        // 如果用户有主密码，先验证当前密码
        if (state.hasMasterPassword) {
            validateCurrentPassword()
        } else {
            changePassword()
        }
    }

    /**
     * 检查确认密码是否匹配，不匹配时显示错误。返回是否匹配。
     */
    private fun checkRetypedPassword(): Boolean {
        if (state.passwordInput == state.retypePasswordInput) return true

        mutableStateFlow.update {
            it.copy(
                dialogState = ChangeMasterPasswordState.DialogState.Error(
                    title = BitwardenString.an_error_has_occurred.asText(),
                    message = BitwardenString.master_password_confirmation_val_message.asText(),
                ),
            )
        }
        return false
    }

    /**
     * 验证当前密码。
     */
    private fun validateCurrentPassword() {
        mutableStateFlow.update {
            it.copy(
                dialogState = ChangeMasterPasswordState.DialogState.Loading(
                    message = BitwardenString.loading.asText(),
                ),
            )
        }
        viewModelScope.launch {
            val result = authRepository.validatePassword(state.currentPasswordInput)
            trySendAction(ChangeMasterPasswordAction.Internal.ReceiveValidatePasswordResult(result))
        }
    }

    /**
     * 发起修改密码请求。
     */
    private fun changePassword() {
        // 显示加载对话框
        mutableStateFlow.update {
            it.copy(
                dialogState = ChangeMasterPasswordState.DialogState.Loading(
                    message = BitwardenString.updating_password.asText(),
                ),
            )
        }
        viewModelScope.launch {
            val result = authRepository.changePassword(
                currentPassword = state.currentPasswordInput.orNullIfBlank(),
                newPassword = state.passwordInput,
                passwordHint = state.passwordHintInput,
            )
            trySendAction(
                ChangeMasterPasswordAction.Internal.ReceiveResetPasswordResult(result),
            )
        }
    }
}

/**
 * 修改主密码页面的状态。
 */
@Parcelize
data class ChangeMasterPasswordState(
    val policies: List<Text>,
    val dialogState: DialogState?,
    val currentPasswordInput: String,
    val passwordInput: String,
    val retypePasswordInput: String,
    val passwordHintInput: String,
    val passwordStrengthState: PasswordStrengthState,
    val minimumPasswordLength: Int,
    val hasMasterPassword: Boolean,
) : Parcelable {
    /**
     * 页面上可能显示的对话框状态。
     */
    sealed class DialogState : Parcelable {
        /**
         * 错误对话框，包含 [message] 和可选的 [title]。
         */
        @Parcelize
        data class Error(
            val title: Text?,
            val message: Text,
            val error: Throwable? = null,
        ) : DialogState()

        /**
         * 加载对话框，显示 [message]。
         */
        @Parcelize
        data class Loading(
            val message: Text,
        ) : DialogState()
    }
}

/**
 * 修改主密码页面的事件。
 */
sealed class ChangeMasterPasswordEvent {
    /**
     * 返回上一页。
     */
    data object NavigateBack : ChangeMasterPasswordEvent()

    /**
     * 显示 Toast 提示。
     */
    data class ShowToast(val message: Text) : ChangeMasterPasswordEvent()
}

/**
 * 修改主密码页面的操作。
 */
sealed class ChangeMasterPasswordAction {
    /**
     * 用户点击返回按钮。
     */
    data object BackClick : ChangeMasterPasswordAction()

    /**
     * 用户点击提交按钮。
     */
    data object SubmitClick : ChangeMasterPasswordAction()

    /**
     * 关闭对话框。
     */
    data object DialogDismiss : ChangeMasterPasswordAction()

    /**
     * 当前密码输入变化。
     */
    data class CurrentPasswordInputChanged(val input: String) : ChangeMasterPasswordAction()

    /**
     * 新密码输入变化。
     */
    data class PasswordInputChanged(val input: String) : ChangeMasterPasswordAction()

    /**
     * 确认密码输入变化。
     */
    data class RetypePasswordInputChanged(val input: String) : ChangeMasterPasswordAction()

    /**
     * 密码提示输入变化。
     */
    data class PasswordHintInputChanged(val input: String) : ChangeMasterPasswordAction()

    /**
     * ViewModel 内部操作。
     */
    sealed class Internal : ChangeMasterPasswordAction() {
        /**
         * 收到修改密码结果。
         */
        data class ReceiveResetPasswordResult(
            val result: ResetPasswordResult,
        ) : Internal()

        /**
         * 收到验证密码结果。
         */
        data class ReceiveValidatePasswordResult(
            val result: ValidatePasswordResult,
        ) : Internal()

        /**
         * 收到密码策略验证结果。
         */
        data class ReceiveValidatePasswordAgainstPoliciesResult(
            val meetsRequirements: Boolean,
        ) : Internal()

        /**
         * 收到密码强度结果。
         */
        data class ReceivePasswordStrengthResult(
            val result: PasswordStrengthResult,
        ) : Internal()
    }
}
