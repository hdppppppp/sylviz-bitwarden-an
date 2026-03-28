package com.x8bit.bitwarden.ui.platform.feature.settings.changeusername

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenFilledButton
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.field.BitwardenTextField
import com.bitwarden.ui.platform.components.model.CardStyle
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeUsernameScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChangeUsernameViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    state.dialog?.let { dialog ->
        when (dialog) {
            is ChangeUsernameDialog.Error -> BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.an_error_has_occurred),
                message = dialog.message,
                onDismissRequest = { viewModel.trySendAction(ChangeUsernameAction.DismissDialog) },
            )
            is ChangeUsernameDialog.Success -> BitwardenBasicDialog(
                title = "更改成功",
                message = dialog.message,
                onDismissRequest = {
                    viewModel.trySendAction(ChangeUsernameAction.DismissDialog)
                    onNavigateBack()
                },
            )
        }
    }

    if (state.isLoading) {
        BitwardenLoadingDialog(text = "保存中...")
    }

    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = "更改用户名",
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
                text = "用户名是您的显示名称，不影响登录。",
                style = BitwardenTheme.typography.bodyMedium,
                color = BitwardenTheme.colorScheme.text.secondary,
                modifier = Modifier.standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.currentName.isNotBlank()) {
                Text(
                    text = "当前用户名：${state.currentName}",
                    style = BitwardenTheme.typography.bodySmall,
                    color = BitwardenTheme.colorScheme.text.secondary,
                    modifier = Modifier.standardHorizontalMargin(),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            BitwardenTextField(
                label = "新用户名",
                value = state.newName,
                onValueChange = { viewModel.trySendAction(ChangeUsernameAction.NameChange(it)) },
                cardStyle = CardStyle.Full,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            BitwardenFilledButton(
                label = "保存",
                onClick = { viewModel.trySendAction(ChangeUsernameAction.Save) },
                isEnabled = state.newName.isNotBlank() && !state.isLoading,
                modifier = Modifier.fillMaxWidth().standardHorizontalMargin(),
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
