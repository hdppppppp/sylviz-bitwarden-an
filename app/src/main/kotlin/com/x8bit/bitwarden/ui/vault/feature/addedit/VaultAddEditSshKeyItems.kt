package com.x8bit.bitwarden.ui.vault.feature.addedit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.field.BitwardenPasswordField
import com.bitwarden.ui.platform.components.field.BitwardenTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme
import com.x8bit.bitwarden.ui.vault.feature.addedit.handlers.VaultAddEditSshKeyTypeHandlers

/**
 * The UI for adding and editing a SSH key cipher.
 */
fun LazyListScope.vaultAddEditSshKeyItems(
    sshKeyState: VaultAddEditState.ViewState.Content.ItemType.SshKey,
    sshKeyTypeHandlers: VaultAddEditSshKeyTypeHandlers,
    isEditable: Boolean,
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenPasswordField(
            label = stringResource(id = BitwardenString.private_key),
            value = sshKeyState.privateKey,
            readOnly = !isEditable,
            onValueChange = { sshKeyTypeHandlers.onPrivateKeyTextChange(it) },
            showPassword = sshKeyState.showPrivateKey,
            showPasswordChange = { sshKeyTypeHandlers.onPrivateKeyVisibilityChange(it) },
            showPasswordTestTag = "ViewPrivateKeyButton",
            passwordFieldTestTag = "PrivateKeyEntry",
            cardStyle = CardStyle.Top(),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
    }

    item {
        BitwardenTextField(
            label = stringResource(id = BitwardenString.public_key),
            value = sshKeyState.publicKey,
            readOnly = !isEditable,
            onValueChange = { sshKeyTypeHandlers.onPublicKeyTextChange(it) },
            textFieldTestTag = "PublicKeyEntry",
            cardStyle = CardStyle.Middle(),
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
    }

    item {
        BitwardenTextField(
            label = stringResource(id = BitwardenString.fingerprint),
            value = sshKeyState.fingerprint,
            readOnly = !isEditable,
            onValueChange = { sshKeyTypeHandlers.onFingerprintTextChange(it) },
            textFieldTestTag = "FingerprintEntry",
            cardStyle = CardStyle.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .standardHorizontalMargin(),
        )
    }
}

@Preview
@Composable
private fun VaultAddEditSshKeyItems_preview() {
    BitwardenTheme {
        LazyColumn {
            vaultAddEditSshKeyItems(
                sshKeyState = VaultAddEditState.ViewState.Content.ItemType.SshKey(
                    publicKey = "public key",
                    privateKey = "private key",
                    fingerprint = "fingerprint",
                    showPublicKey = false,
                    showPrivateKey = false,
                    showFingerprint = false,
                ),
                sshKeyTypeHandlers = VaultAddEditSshKeyTypeHandlers(
                    onPublicKeyTextChange = { },
                    onPrivateKeyTextChange = { },
                    onFingerprintTextChange = { },
                    onPrivateKeyVisibilityChange = { },
                ),
                isEditable = true,
            )
        }
    }
}
