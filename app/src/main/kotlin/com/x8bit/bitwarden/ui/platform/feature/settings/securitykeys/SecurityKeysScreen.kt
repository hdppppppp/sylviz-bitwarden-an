package com.x8bit.bitwarden.ui.platform.feature.settings.securitykeys

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.network.model.TwoFactorWebAuthnCredential
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenFilledButton
import com.bitwarden.ui.platform.components.button.BitwardenTextButton
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenTwoButtonDialog
import com.bitwarden.ui.platform.components.field.BitwardenPasswordField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityKeysScreen(
        onNavigateBack: () -> Unit,
        viewModel: SecurityKeysViewModel = hiltViewModel(),
) {
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var masterPassword by rememberSaveable { mutableStateOf("") }

        // Confirm delete dialog
        state.confirmDeleteId?.let { id ->
                BitwardenTwoButtonDialog(
                        title = "删除安全密钥",
                        message = "确定要删除此安全密钥吗？",
                        confirmButtonText = "删除",
                        dismissButtonText = stringResource(id = BitwardenString.cancel),
                        onConfirmClick = {
                                viewModel.trySendAction(SecurityKeysAction.ConfirmDelete(id))
                        },
                        onDismissClick = {
                                viewModel.trySendAction(SecurityKeysAction.DismissDeleteConfirm)
                        },
                        onDismissRequest = {
                                viewModel.trySendAction(SecurityKeysAction.DismissDeleteConfirm)
                        },
                )
        }

        state.dialog?.let { dialog ->
                when (dialog) {
                        is SecurityKeysDialog.Error ->
                                BitwardenBasicDialog(
                                        title =
                                                stringResource(
                                                        id = BitwardenString.an_error_has_occurred
                                                ),
                                        message = dialog.message,
                                        onDismissRequest = {
                                                viewModel.trySendAction(
                                                        SecurityKeysAction.DismissDialog
                                                )
                                        },
                                )
                        is SecurityKeysDialog.Info ->
                                BitwardenBasicDialog(
                                        title = "提示",
                                        message = dialog.message,
                                        onDismissRequest = {
                                                viewModel.trySendAction(
                                                        SecurityKeysAction.DismissDialog
                                                )
                                        },
                                )
                }
        }

        state.error?.let { error ->
                BitwardenBasicDialog(
                        title = stringResource(id = BitwardenString.an_error_has_occurred),
                        message = error,
                        onDismissRequest = {
                                viewModel.trySendAction(SecurityKeysAction.DismissDialog)
                        },
                )
        }

        if (state.isLoading) {
                BitwardenLoadingDialog(text = "加载中...")
        }

        BitwardenScaffold(
                modifier =
                        Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                        BitwardenTopAppBar(
                                title = "安全密钥（WebAuthn/FIDO2）",
                                scrollBehavior = scrollBehavior,
                                navigationIcon =
                                        rememberVectorPainter(id = BitwardenDrawable.ic_back),
                                navigationIconContentDescription =
                                        stringResource(id = BitwardenString.back),
                                onNavigationIconClick = onNavigateBack,
                        )
                },
        ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = "安全密钥（WebAuthn/FIDO2）",
                                        style = BitwardenTheme.typography.titleMedium,
                                        color = BitwardenTheme.colorScheme.text.primary,
                                        modifier =
                                                Modifier.standardHorizontalMargin()
                                                        .padding(horizontal = 16.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text =
                                                "安全密钥是一种基于硬件的两步验证方式，支持 FIDO2/WebAuthn 标准的设备（如 YubiKey、Google Titan 等）。" +
                                                        "注册后，登录时需要插入并触碰安全密钥完成验证。",
                                        style = BitwardenTheme.typography.bodyMedium,
                                        color = BitwardenTheme.colorScheme.text.secondary,
                                        modifier =
                                                Modifier.standardHorizontalMargin()
                                                        .padding(horizontal = 16.dp),
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Password input + load button (shown when keys not yet loaded)
                        if (state.masterPasswordHash == null) {
                                item {
                                        BitwardenPasswordField(
                                                label = "主密码（用于验证身份）",
                                                value = masterPassword,
                                                onValueChange = { masterPassword = it },
                                                cardStyle = CardStyle.Full,
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .standardHorizontalMargin(),
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        BitwardenFilledButton(
                                                label = "查看安全密钥",
                                                onClick = {
                                                        if (masterPassword.isNotBlank()) {
                                                                viewModel.trySendAction(
                                                                        SecurityKeysAction.LoadKeys(
                                                                                masterPassword
                                                                        )
                                                                )
                                                        }
                                                },
                                                isEnabled = masterPassword.isNotBlank(),
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .standardHorizontalMargin(),
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                }
                        } else {
                                // Keys loaded — show list or empty state
                                item {
                                        if (state.isEnabled) {
                                                Text(
                                                        text = "✓ 安全密钥两步验证已启用",
                                                        style = BitwardenTheme.typography.bodySmall,
                                                        color =
                                                                BitwardenTheme.colorScheme
                                                                        .text
                                                                        .interaction,
                                                        modifier =
                                                                Modifier.standardHorizontalMargin()
                                                                        .padding(
                                                                                horizontal = 16.dp
                                                                        ),
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        Text(
                                                text =
                                                        if (state.keys.isEmpty()) "暂无已注册的安全密钥"
                                                        else "已注册的安全密钥（${state.keys.size} 个）：",
                                                style = BitwardenTheme.typography.bodySmall,
                                                color = BitwardenTheme.colorScheme.text.secondary,
                                                modifier =
                                                        Modifier.standardHorizontalMargin()
                                                                .padding(horizontal = 16.dp),
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                }

                                if (state.keys.isEmpty()) {
                                        item {
                                                Text(
                                                        text =
                                                                "安全密钥注册需要通过支持 WebAuthn 的浏览器完成。\n请在电脑浏览器中访问网页端进行注册。",
                                                        style = BitwardenTheme.typography.bodySmall,
                                                        color =
                                                                BitwardenTheme.colorScheme
                                                                        .text
                                                                        .secondary,
                                                        modifier =
                                                                Modifier.standardHorizontalMargin()
                                                                        .padding(
                                                                                horizontal = 16.dp
                                                                        ),
                                                )
                                        }
                                } else {
                                        items(state.keys, key = { it.id }) { key ->
                                                SecurityKeyItem(
                                                        credential = key,
                                                        onDelete = {
                                                                viewModel.trySendAction(
                                                                        SecurityKeysAction
                                                                                .RequestDelete(
                                                                                        key.id
                                                                                )
                                                                )
                                                        },
                                                )
                                                HorizontalDivider(
                                                        modifier =
                                                                Modifier.standardHorizontalMargin(),
                                                        color =
                                                                BitwardenTheme.colorScheme
                                                                        .stroke
                                                                        .divider,
                                                )
                                        }
                                }

                                item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        item { Spacer(modifier = Modifier.navigationBarsPadding()) }
                }
        }
}

@Composable
private fun SecurityKeyItem(
        credential: TwoFactorWebAuthnCredential,
        onDelete: () -> Unit,
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .standardHorizontalMargin()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
        ) {
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = credential.name,
                                style = BitwardenTheme.typography.bodyLarge,
                                color = BitwardenTheme.colorScheme.text.primary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = "ID: ${credential.id}",
                                style = BitwardenTheme.typography.bodySmall,
                                color = BitwardenTheme.colorScheme.text.secondary,
                        )
                }
                BitwardenTextButton(label = "删除", onClick = onDelete)
        }
}
