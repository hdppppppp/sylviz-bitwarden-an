package com.x8bit.bitwarden.ui.platform.feature.settings.changemasterpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenTextButton
import com.bitwarden.ui.platform.components.card.BitwardenInfoCalloutCard
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.field.BitwardenPasswordField
import com.bitwarden.ui.platform.components.field.BitwardenTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme
import com.x8bit.bitwarden.ui.auth.feature.completeregistration.PasswordStrengthIndicator
import com.x8bit.bitwarden.ui.auth.feature.completeregistration.PasswordStrengthState

/**
 * 修改主密码页面的顶层 Composable。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod")
fun ChangeMasterPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangeMasterPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            ChangeMasterPasswordEvent.NavigateBack -> onNavigateBack()
            is ChangeMasterPasswordEvent.ShowToast -> {
                // Toast 由 BitwardenToastHost 处理
            }
        }
    }

    when (val dialog = state.dialogState) {
        is ChangeMasterPasswordState.DialogState.Error -> {
            BitwardenBasicDialog(
                title = dialog.title?.invoke(),
                message = dialog.message(),
                throwable = dialog.error,
                onDismissRequest = { viewModel.trySendAction(ChangeMasterPasswordAction.DialogDismiss) },
            )
        }

        is ChangeMasterPasswordState.DialogState.Loading -> {
            BitwardenLoadingDialog(text = dialog.message())
        }

        null -> Unit
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = BitwardenString.change_master_password),
                scrollBehavior = scrollBehavior,
                navigationIcon = null,
                actions = {
                    BitwardenTextButton(
                        label = stringResource(id = BitwardenString.cancel),
                        onClick = { viewModel.trySendAction(ChangeMasterPasswordAction.BackClick) },
                        modifier = Modifier.testTag("CancelButton"),
                    )
                    BitwardenTextButton(
                        label = stringResource(id = BitwardenString.submit),
                        onClick = { viewModel.trySendAction(ChangeMasterPasswordAction.SubmitClick) },
                        modifier = Modifier.testTag("SubmitButton"),
                    )
                },
            )
        },
    ) {
        ChangeMasterPasswordScreenContent(
            state = state,
            onCurrentPasswordInputChanged = {
                viewModel.trySendAction(ChangeMasterPasswordAction.CurrentPasswordInputChanged(it))
            },
            onPasswordInputChanged = {
                viewModel.trySendAction(ChangeMasterPasswordAction.PasswordInputChanged(it))
            },
            onRetypePasswordInputChanged = {
                viewModel.trySendAction(ChangeMasterPasswordAction.RetypePasswordInputChanged(it))
            },
            onPasswordHintInputChanged = {
                viewModel.trySendAction(ChangeMasterPasswordAction.PasswordHintInputChanged(it))
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun ChangeMasterPasswordScreenContent(
    state: ChangeMasterPasswordState,
    onCurrentPasswordInputChanged: (String) -> Unit,
    onPasswordInputChanged: (String) -> Unit,
    onRetypePasswordInputChanged: (String) -> Unit,
    onPasswordHintInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(height = 12.dp))

        // 提示信息
        BitwardenInfoCalloutCard(
            text = stringResource(id = BitwardenString.change_master_password_description_long),
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        // 密码策略提示（如果有）
        if (state.policies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            val passwordPolicyContent = listOf(
                stringResource(id = BitwardenString.master_password_policy_in_effect),
            )
                .plus(state.policies.map { it() })
                .joinToString("\n  •  ")
            BitwardenInfoCalloutCard(
                text = passwordPolicyContent,
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

        // 当前密码（如果用户有主密码）
        if (state.hasMasterPassword) {
            BitwardenPasswordField(
                label = stringResource(id = BitwardenString.current_master_password_required),
                value = state.currentPasswordInput,
                onValueChange = onCurrentPasswordInputChanged,
                showPassword = isPasswordVisible,
                showPasswordChange = { isPasswordVisible = it },
                passwordFieldTestTag = "CurrentMasterPasswordField",
                cardStyle = CardStyle.Top(dividerPadding = 0.dp),
                modifier = Modifier
                    .standardHorizontalMargin()
                    .fillMaxWidth(),
            )
        }

        // 新密码
        BitwardenPasswordField(
            label = stringResource(id = BitwardenString.new_master_password_required),
            value = state.passwordInput,
            onValueChange = onPasswordInputChanged,
            showPassword = isPasswordVisible,
            showPasswordChange = { isPasswordVisible = it },
            passwordFieldTestTag = "NewMasterPasswordField",
            supportingContent = {
                PasswordStrengthIndicator(
                    state = state.passwordStrengthState,
                    currentCharacterCount = state.passwordInput.length,
                    minimumCharacterCount = state.minimumPasswordLength,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            cardStyle = if (state.hasMasterPassword) {
                CardStyle.Middle(dividerPadding = 0.dp)
            } else {
                CardStyle.Top(dividerPadding = 0.dp)
            },
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        // 确认新密码
        BitwardenPasswordField(
            label = stringResource(id = BitwardenString.retype_master_password),
            value = state.retypePasswordInput,
            onValueChange = onRetypePasswordInputChanged,
            showPassword = isPasswordVisible,
            showPasswordChange = { isPasswordVisible = it },
            passwordFieldTestTag = "RetypeMasterPasswordField",
            cardStyle = CardStyle.Middle(dividerPadding = 0.dp),
            modifier = Modifier
                .standardHorizontalMargin()
                .fillMaxWidth(),
        )

        // 密码提示
        BitwardenTextField(
            label = stringResource(id = BitwardenString.master_password_hint),
            value = state.passwordHintInput,
            onValueChange = onPasswordHintInputChanged,
            supportingContent = {
                Column {
                    Text(
                        text = stringResource(
                            BitwardenString.bitwarden_cannot_reset_a_lost_or_forgotten_master_password,
                        ),
                        style = BitwardenTheme.typography.bodySmall,
                        color = BitwardenTheme.colorScheme.text.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            textFieldTestTag = "MasterPasswordHintField",
            cardStyle = CardStyle.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )

        Spacer(modifier = Modifier.height(height = 12.dp))
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
