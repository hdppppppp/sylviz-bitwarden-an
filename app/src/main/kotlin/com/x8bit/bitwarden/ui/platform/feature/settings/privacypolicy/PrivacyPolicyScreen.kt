package com.x8bit.bitwarden.ui.platform.feature.settings.privacypolicy

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
 * Displays the privacy policy screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = BitwardenString.privacy_policy),
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
                text = stringResource(id = BitwardenString.privacy_policy),
                style = BitwardenTheme.typography.titleMedium,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Bitwarden 隐私政策\n\n" +
                    "最后更新：2024年\n\n" +
                    "我们重视您的隐私\n\n" +
                    "Bitwarden 致力于保护您的隐私。我们采用端到端加密技术，确保只有您能访问您的数据。\n\n" +
                    "我们收集的信息：\n" +
                    "• 账户信息（电子邮件地址）\n" +
                    "• 使用数据（匿名）\n" +
                    "• 设备信息\n\n" +
                    "我们不会收集：\n" +
                    "• 您的主密码\n" +
                    "• 您存储的密码和数据（已加密）\n\n" +
                    "数据安全：\n" +
                    "• 端到端 AES-256 位加密\n" +
                    "• PBKDF2 SHA-256 密钥派生\n" +
                    "• 零知识架构\n\n" +
                    "您的权利：\n" +
                    "• 访问您的数据\n" +
                    "• 删除您的账户\n" +
                    "• 导出您的数据\n\n" +
                    "完整隐私政策请访问：\n" +
                    "bitwarden.com/privacy",
                style = BitwardenTheme.typography.bodyMedium,
                color = BitwardenTheme.colorScheme.text.primary,
                modifier = Modifier.standardHorizontalMargin(),
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
