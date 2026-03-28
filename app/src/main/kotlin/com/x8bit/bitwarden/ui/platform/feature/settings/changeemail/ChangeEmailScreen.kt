package com.x8bit.bitwarden.ui.platform.feature.settings.changeemail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenFilledButton
import com.bitwarden.ui.platform.components.button.BitwardenOutlinedButton
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.field.BitwardenPasswordField
import com.bitwarden.ui.platform.components.field.BitwardenTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangeEmailViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    state.dialog?.let { dialog ->
        when (dialog) {
            is ChangeEmailDialog.Error -> BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.an_error_has_occurred),
                message = dialog.message,
                onDismissRequest = { viewModel.trySendAction(ChangeEmailAction.DismissDialog) },
            )
            is ChangeEmailDialog.Info -> BitwardenBasicDialog(
                title = "提示",
                message = dialog.message,
                onDismissRequest = { viewModel.trySendAction(ChangeEmailAction.DismissDialog) },
            )
            is ChangeEmailDialog.Success -> BitwardenBasicDialog(
                title = "更改成功",
                message = dialog.message,
                onDismissRequest = {
                    viewModel.trySendAction(ChangeEmailAction.DismissDialog)
                    onNavigateBack()
                },
            )
        }
    }

    if (state.isLoading) {
        BitwardenLoadingDialog(text = "处理中...")
    }

    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = "更改电子邮箱",
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                navigationIconContentDescription = stringResource(id = BitwardenString.back),
                onNavigationIconClick = onNavigateBack,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Warning banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .standardHorizontalMargin()
                    .background(
                        color = BitwardenTheme.colorScheme.status.weak1,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
            ) {
                Row {
                    Text(
                        text = "⚠️",
                        style = BitwardenTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp, top = 2.dp),
                    )
                    Text(
                        text = "继续操作将更改您的账户电子邮箱地址。但不会更改用于双重身份验证的电子邮箱地址，" +
                            "您可以在两步登录设置中更改它。",
                        style = BitwardenTheme.typography.bodySmall,
                        color = BitwardenTheme.colorScheme.text.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master password
            BitwardenPasswordField(
                label = "主密码（必填）",
                value = state.masterPassword,
                onValueChange = { viewModel.trySendAction(ChangeEmailAction.MasterPasswordChange(it)) },
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // New email
            BitwardenTextField(
                label = "新电子邮箱（必填）",
                value = state.newEmail,
                onValueChange = { viewModel.trySendAction(ChangeEmailAction.NewEmailChange(it)) },
                keyboardType = KeyboardType.Email,
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Send token button
            BitwardenOutlinedButton(
                label = "发送验证码",
                onClick = { viewModel.trySendAction(ChangeEmailAction.SendToken) },
                isEnabled = state.masterPassword.isNotBlank() && state.newEmail.isNotBlank(),
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )

            // Token input (shown after sending)
            if (state.tokenSent) {
                Spacer(modifier = Modifier.height(8.dp))
                BitwardenTextField(
                    label = "验证码",
                    value = state.token,
                    onValueChange = { viewModel.trySendAction(ChangeEmailAction.TokenChange(it)) },
                    keyboardType = KeyboardType.Number,
                    cardStyle = CardStyle.Full,
                    modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Danger confirm button
                BitwardenFilledButton(
                    label = "继续（危险操作）",
                    onClick = { viewModel.trySendAction(ChangeEmailAction.ConfirmChange) },
                    isEnabled = state.token.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
