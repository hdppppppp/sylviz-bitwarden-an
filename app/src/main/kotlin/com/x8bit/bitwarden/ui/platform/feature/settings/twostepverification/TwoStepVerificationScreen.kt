package com.x8bit.bitwarden.ui.platform.feature.settings.twostepverification

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.network.model.TwoFactorWebAuthnCredential
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenFilledButton
import com.bitwarden.ui.platform.components.button.BitwardenOutlinedButton
import com.bitwarden.ui.platform.components.button.BitwardenTextButton
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
fun TwoStepVerificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: TwoStepVerificationViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var masterPassword by rememberSaveable { mutableStateOf("") }
    var verificationCode by rememberSaveable { mutableStateOf("") }
    // Duo fields
    var duoHost by rememberSaveable { mutableStateOf("") }
    var duoSecretKey by rememberSaveable { mutableStateOf("") }
    var duoIntegrationKey by rememberSaveable { mutableStateOf("") }
    // YubiKey fields
    var yubiKey1 by rememberSaveable { mutableStateOf("") }
    var yubiKey2 by rememberSaveable { mutableStateOf("") }
    var yubiKey3 by rememberSaveable { mutableStateOf("") }
    var yubiKey4 by rememberSaveable { mutableStateOf("") }
    var yubiKey5 by rememberSaveable { mutableStateOf("") }

    state.dialog?.let { dialog ->
        when (dialog) {
            is TwoStepDialog.Success -> BitwardenBasicDialog(
                title = "设置成功",
                message = dialog.message,
                onDismissRequest = {
                    viewModel.trySendAction(TwoStepAction.DismissDialog)
                    onNavigateBack()
                },
            )
            is TwoStepDialog.Error -> BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.an_error_has_occurred),
                message = dialog.message,
                onDismissRequest = { viewModel.trySendAction(TwoStepAction.DismissDialog) },
            )
            is TwoStepDialog.Info -> BitwardenBasicDialog(
                title = "提示",
                message = dialog.message,
                onDismissRequest = { viewModel.trySendAction(TwoStepAction.DismissDialog) },
            )
            is TwoStepDialog.RecoveryCode -> BitwardenBasicDialog(
                title = "恢复代码",
                message = "请将以下恢复代码抄写在纸上并妥善保管：\n\n${dialog.code}\n\n当您无法访问两步验证提供程序时，可使用此代码停用两步验证。",
                onDismissRequest = { viewModel.trySendAction(TwoStepAction.DismissDialog) },
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
                title = stringResource(id = BitwardenString.two_step_login),
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

            Text(
                text = "选择两步验证方式",
                style = BitwardenTheme.typography.titleMedium,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "两步验证为您的账户提供额外的安全保护。",
                style = BitwardenTheme.typography.bodyMedium,
                color = BitwardenTheme.colorScheme.text.secondary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(16.dp))

            BitwardenPasswordField(
                label = "主密码（用于验证身份）",
                value = masterPassword,
                onValueChange = { masterPassword = it },
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Recovery code warning banner
            RecoveryCodeWarningBanner(
                onViewRecoveryCode = {
                    if (masterPassword.isNotBlank()) {
                        viewModel.trySendAction(TwoStepAction.ViewRecoveryCode(masterPassword))
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Method buttons
            val methods = listOf(
                TwoStepMethod.TOTP to "验证器 App（TOTP）",
                TwoStepMethod.EMAIL to "电子邮件",
                TwoStepMethod.DUO to "Duo",
                TwoStepMethod.YUBIKEY to "YubiKey",
                TwoStepMethod.WEBAUTHN to "通行密钥（WebAuthn/FIDO2）",
            )
            methods.forEach { (method, label) ->
                val isSelected = state.selectedMethod == method
                if (isSelected) {
                    BitwardenFilledButton(
                        label = "✓ $label",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
                    )
                } else {
                    BitwardenOutlinedButton(
                        label = label,
                        onClick = {
                            if (masterPassword.isNotBlank()) {
                                viewModel.trySendAction(TwoStepAction.SelectMethod(method, masterPassword))
                                verificationCode = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (state.selectedMethod) {
                TwoStepMethod.TOTP -> TotpSetupContent(
                    totpKey = state.totpKey,
                    verificationCode = verificationCode,
                    onVerificationCodeChange = { verificationCode = it },
                    onEnable = { viewModel.trySendAction(TwoStepAction.EnableTotp(masterPassword, verificationCode)) },
                )
                TwoStepMethod.EMAIL -> EmailSetupContent(
                    verificationCode = verificationCode,
                    onVerificationCodeChange = { verificationCode = it },
                    onSendCode = { viewModel.trySendAction(TwoStepAction.SendEmailCode(masterPassword)) },
                    onEnable = { viewModel.trySendAction(TwoStepAction.EnableEmail(masterPassword, verificationCode)) },
                )
                TwoStepMethod.DUO -> DuoSetupContent(
                    host = duoHost.ifBlank { state.duoHost.orEmpty() },
                    onHostChange = { duoHost = it },
                    integrationKey = duoIntegrationKey.ifBlank { state.duoIntegrationKey.orEmpty() },
                    onIntegrationKeyChange = { duoIntegrationKey = it },
                    secretKey = duoSecretKey,
                    onSecretKeyChange = { duoSecretKey = it },
                    isEnabled = state.duoEnabled,
                    onEnable = {
                        viewModel.trySendAction(
                            TwoStepAction.EnableDuo(masterPassword, duoHost, duoSecretKey, duoIntegrationKey),
                        )
                    },
                )
                TwoStepMethod.YUBIKEY -> YubiKeySetupContent(
                    existingKeys = state.yubiKeys,
                    key1 = yubiKey1,
                    onKey1Change = { yubiKey1 = it },
                    key2 = yubiKey2,
                    onKey2Change = { yubiKey2 = it },
                    key3 = yubiKey3,
                    onKey3Change = { yubiKey3 = it },
                    key4 = yubiKey4,
                    onKey4Change = { yubiKey4 = it },
                    key5 = yubiKey5,
                    onKey5Change = { yubiKey5 = it },
                    isEnabled = state.yubiKeyEnabled,
                    onEnable = {
                        viewModel.trySendAction(
                            TwoStepAction.EnableYubiKey(
                                masterPassword,
                                listOf(
                                    yubiKey1.takeIf { it.isNotBlank() },
                                    yubiKey2.takeIf { it.isNotBlank() },
                                    yubiKey3.takeIf { it.isNotBlank() },
                                    yubiKey4.takeIf { it.isNotBlank() },
                                    yubiKey5.takeIf { it.isNotBlank() },
                                ),
                                nfc = false,
                            ),
                        )
                    },
                )
                TwoStepMethod.WEBAUTHN -> WebAuthnSetupContent(
                    credentials = state.webAuthnCredentials,
                    isEnabled = state.webAuthnEnabled,
                    onDelete = { id -> viewModel.trySendAction(TwoStepAction.DeleteWebAuthnCredential(id, masterPassword)) },
                )
                null -> Unit
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun TotpSetupContent(
    totpKey: String?,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    onEnable: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "设置验证器 App",
            style = BitwardenTheme.typography.titleSmall,
            color = BitwardenTheme.colorScheme.text.primary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "1. 安装验证器 App（如 Google Authenticator）\n2. 选择「手动输入密钥」\n3. 输入下方密钥\n4. 输入 App 生成的 6 位验证码",
            style = BitwardenTheme.typography.bodySmall,
            color = BitwardenTheme.colorScheme.text.secondary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        if (totpKey != null) {
            Spacer(modifier = Modifier.height(8.dp))
            BitwardenTextField(
                label = "TOTP 密钥",
                value = totpKey,
                onValueChange = {},
                readOnly = true,
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenTextField(
            label = "输入验证码",
            value = verificationCode,
            onValueChange = onVerificationCodeChange,
            keyboardType = KeyboardType.Number,
            cardStyle = CardStyle.Full,
            modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        BitwardenFilledButton(
            label = "启用",
            onClick = onEnable,
            isEnabled = verificationCode.length == 6,
            modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
        )
    }
}

@Composable
private fun EmailSetupContent(
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onEnable: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "设置邮件验证",
            style = BitwardenTheme.typography.titleSmall,
            color = BitwardenTheme.colorScheme.text.primary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "点击「发送验证码」，系统会向您的注册邮箱发送一个 6 位验证码。",
            style = BitwardenTheme.typography.bodySmall,
            color = BitwardenTheme.colorScheme.text.secondary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenOutlinedButton(label = "发送验证码", onClick = onSendCode, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenTextField(
            label = "输入验证码",
            value = verificationCode,
            onValueChange = onVerificationCodeChange,
            keyboardType = KeyboardType.Number,
            cardStyle = CardStyle.Full,
            modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        BitwardenFilledButton(label = "启用", onClick = onEnable, isEnabled = verificationCode.length == 6, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
    }
}

@Composable
private fun DuoSetupContent(
    host: String,
    onHostChange: (String) -> Unit,
    integrationKey: String,
    onIntegrationKeyChange: (String) -> Unit,
    secretKey: String,
    onSecretKeyChange: (String) -> Unit,
    isEnabled: Boolean,
    onEnable: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "设置 Duo",
            style = BitwardenTheme.typography.titleSmall,
            color = BitwardenTheme.colorScheme.text.primary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "需要 Duo Security 企业账号。请在 Duo 管理面板中创建应用并获取以下信息。",
            style = BitwardenTheme.typography.bodySmall,
            color = BitwardenTheme.colorScheme.text.secondary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        if (isEnabled) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✓ Duo 已启用",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.interaction,
                modifier = Modifier.standardHorizontalMargin(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenTextField(label = "API 主机名（Host）", value = host, onValueChange = onHostChange, cardStyle = CardStyle.Full, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenTextField(label = "集成密钥（Integration Key）", value = integrationKey, onValueChange = onIntegrationKeyChange, cardStyle = CardStyle.Full, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenPasswordField(label = "密钥（Secret Key）", value = secretKey, onValueChange = onSecretKeyChange, cardStyle = CardStyle.Full, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
        Spacer(modifier = Modifier.height(12.dp))
        BitwardenFilledButton(
            label = "启用",
            onClick = onEnable,
            isEnabled = host.isNotBlank() && integrationKey.isNotBlank() && secretKey.isNotBlank(),
            modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
        )
    }
}

@Composable
private fun YubiKeySetupContent(
    existingKeys: List<String>,
    key1: String, onKey1Change: (String) -> Unit,
    key2: String, onKey2Change: (String) -> Unit,
    key3: String, onKey3Change: (String) -> Unit,
    key4: String, onKey4Change: (String) -> Unit,
    key5: String, onKey5Change: (String) -> Unit,
    isEnabled: Boolean,
    onEnable: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "设置 YubiKey",
            style = BitwardenTheme.typography.titleSmall,
            color = BitwardenTheme.colorScheme.text.primary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "将 YubiKey 插入设备，点击输入框后触碰 YubiKey 按钮，最多可添加 5 个密钥。",
            style = BitwardenTheme.typography.bodySmall,
            color = BitwardenTheme.colorScheme.text.secondary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        if (isEnabled && existingKeys.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✓ 已配置 ${existingKeys.size} 个 YubiKey",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.interaction,
                modifier = Modifier.standardHorizontalMargin(),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        listOf(
            "YubiKey 1" to (key1 to onKey1Change),
            "YubiKey 2" to (key2 to onKey2Change),
            "YubiKey 3" to (key3 to onKey3Change),
            "YubiKey 4" to (key4 to onKey4Change),
            "YubiKey 5" to (key5 to onKey5Change),
        ).forEach { (label, pair) ->
            BitwardenTextField(label = label, value = pair.first, onValueChange = pair.second, cardStyle = CardStyle.Full, modifier = Modifier.fillMaxWidth().standardHorizontalMargin())
            Spacer(modifier = Modifier.height(8.dp))
        }
        BitwardenFilledButton(
            label = "启用",
            onClick = onEnable,
            isEnabled = listOf(key1, key2, key3, key4, key5).any { it.isNotBlank() },
            modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
        )
    }
}

@Composable
private fun WebAuthnSetupContent(
    credentials: List<TwoFactorWebAuthnCredential>?,
    isEnabled: Boolean,
    onDelete: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "通行密钥（WebAuthn/FIDO2）",
            style = BitwardenTheme.typography.titleSmall,
            color = BitwardenTheme.colorScheme.text.primary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "通行密钥注册需要通过网页端完成。您可以在此查看和删除已注册的通行密钥。",
            style = BitwardenTheme.typography.bodySmall,
            color = BitwardenTheme.colorScheme.text.secondary,
            modifier = Modifier.standardHorizontalMargin(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (credentials == null) {
            Text(
                text = "加载中...",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.secondary,
                modifier = Modifier.standardHorizontalMargin(),
            )
        } else if (credentials.isEmpty()) {
            Text(
                text = "暂无已注册的通行密钥。请前往网页端注册。",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.secondary,
                modifier = Modifier.standardHorizontalMargin(),
            )
        } else {
            Text(
                text = "已注册的通行密钥：",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            Spacer(modifier = Modifier.height(4.dp))
            credentials.forEach { credential ->
                Row(
                    modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
                ) {
                    Text(
                        text = credential.name,
                        style = BitwardenTheme.typography.bodyMedium,
                        color = BitwardenTheme.colorScheme.text.primary,
                        modifier = Modifier.weight(1f),
                    )
                    BitwardenTextButton(
                        label = "删除",
                        onClick = { onDelete(credential.id) },
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun RecoveryCodeWarningBanner(
    onViewRecoveryCode: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .standardHorizontalMargin()
            .background(
                color = BitwardenTheme.colorScheme.status.weak1,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            )
            .padding(12.dp),
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.Top) {
            Text(
                text = "⚠️",
                style = BitwardenTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp, top = 2.dp),
            )
            Text(
                text = "当您无法访问两步登录提供程序时，您的一次性恢复代码可用于停用两步登录。" +
                    "Bitwarden 建议您写下恢复代码，并将其妥善保管。",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.primary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenTextButton(
            label = "查看恢复代码",
            onClick = onViewRecoveryCode,
        )
    }
}
