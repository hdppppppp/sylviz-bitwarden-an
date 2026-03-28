package com.x8bit.bitwarden.ui.platform.feature.settings.devicemanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.network.model.DeviceResponseJson
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.button.BitwardenTextButton
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenTwoButtonDialog
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: DeviceManagementViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Confirm delete dialog
    state.confirmDeleteId?.let { id ->
        BitwardenTwoButtonDialog(
            title = "移除设备",
            message = "确定要移除此设备吗？移除后该设备需要重新登录。",
            confirmButtonText = "移除",
            dismissButtonText = stringResource(id = BitwardenString.cancel),
            onConfirmClick = { viewModel.trySendAction(DeviceManagementAction.ConfirmDelete(id)) },
            onDismissClick = { viewModel.trySendAction(DeviceManagementAction.DismissDeleteConfirm) },
            onDismissRequest = { viewModel.trySendAction(DeviceManagementAction.DismissDeleteConfirm) },
        )
    }

    // Error dialog
    state.error?.let { error ->
        BitwardenBasicDialog(
            title = stringResource(id = BitwardenString.an_error_has_occurred),
            message = error,
            onDismissRequest = { viewModel.trySendAction(DeviceManagementAction.Refresh) },
        )
    }

    if (state.isLoading) {
        BitwardenLoadingDialog(text = "加载中...")
    }

    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = "登录设备管理",
                scrollBehavior = scrollBehavior,
                navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                navigationIconContentDescription = stringResource(id = BitwardenString.back),
                onNavigationIconClick = onNavigateBack,
            )
        },
    ) {
        if (!state.isLoading && state.error == null) {
            if (state.devices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "暂无登录设备",
                        style = BitwardenTheme.typography.bodyMedium,
                        color = BitwardenTheme.colorScheme.text.secondary,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "共 ${state.devices.size} 台设备",
                            style = BitwardenTheme.typography.bodySmall,
                            color = BitwardenTheme.colorScheme.text.secondary,
                            modifier = Modifier
                                .standardHorizontalMargin()
                                .padding(horizontal = 16.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(state.devices, key = { it.id }) { device ->
                        DeviceItem(
                            device = device,
                            onRemove = {
                                viewModel.trySendAction(DeviceManagementAction.RequestDelete(device.id))
                            },
                        )
                        HorizontalDivider(
                            modifier = Modifier.standardHorizontalMargin(),
                            color = BitwardenTheme.colorScheme.stroke.divider,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: DeviceResponseJson,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .standardHorizontalMargin()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                style = BitwardenTheme.typography.bodyLarge,
                color = BitwardenTheme.colorScheme.text.primary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = deviceTypeName(device.type),
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.secondary,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "标识符：${device.identifier.take(8)}...",
                style = BitwardenTheme.typography.bodySmall,
                color = BitwardenTheme.colorScheme.text.secondary,
            )
            if (device.isTrusted) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "✓ 受信任设备",
                    style = BitwardenTheme.typography.labelSmall,
                    color = BitwardenTheme.colorScheme.text.interaction,
                )
            }
        }
        BitwardenTextButton(
            label = "移除",
            onClick = onRemove,
        )
    }
}

private fun deviceTypeName(type: Int): String = when (type) {
    0 -> "Android"
    1 -> "iOS"
    2 -> "Chrome 扩展"
    3 -> "Firefox 扩展"
    4 -> "Opera 扩展"
    5 -> "Edge 扩展"
    6 -> "Windows 桌面"
    7 -> "macOS 桌面"
    8 -> "Linux 桌面"
    9 -> "Chrome 浏览器"
    10 -> "Firefox 浏览器"
    11 -> "Opera 浏览器"
    12 -> "Edge 浏览器"
    13 -> "Internet Explorer"
    14 -> "未知浏览器"
    15 -> "Android Amazon"
    16 -> "UWP"
    17 -> "Safari 扩展"
    18 -> "Vivaldi 扩展"
    19 -> "Vivaldi 浏览器"
    20 -> "Safari 浏览器"
    21 -> "macOS 扩展"
    22 -> "Web 应用"
    else -> "未知设备"
}
