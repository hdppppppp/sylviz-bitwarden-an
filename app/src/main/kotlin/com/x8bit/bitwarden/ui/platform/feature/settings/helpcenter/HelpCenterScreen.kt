package com.x8bit.bitwarden.ui.platform.feature.settings.helpcenter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenTopAppBar
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.platform.theme.BitwardenTheme

/**
 * Displays the help center screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onNavigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = BitwardenString.bitwarden_help_center),
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
                text = stringResource(id = BitwardenString.bitwarden_help_center),
                style = BitwardenTheme.typography.titleMedium,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "欢迎使用 Bitwarden 帮助中心\n\n" +
                    "Bitwarden 是一个安全且免费的跨平台密码管理器。\n\n" +
                    "主要功能：\n" +
                    "• 安全存储密码和敏感信息\n" +
                    "• 跨设备同步\n" +
                    "• 自动填充登录信息\n" +
                    "• 生成强密码\n" +
                    "• 安全共享密码\n\n" +
                    "如何使用：\n" +
                    "1. 创建账户并设置主密码\n" +
                    "2. 添加登录项目和其他信息\n" +
                    "3. 使用自动填充功能快速登录\n" +
                    "4. 启用两步登录增强安全性\n\n" +
                    "需要更多帮助？\n" +
                    "访问 bitwarden.com/help 获取完整文档。",
                style = BitwardenTheme.typography.bodyMedium,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
