package com.x8bit.bitwarden.ui.platform.feature.settings.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.core.net.toUri
import com.bitwarden.core.data.repository.util.bufferedMutableSharedFlow
import com.bitwarden.ui.platform.manager.IntentManager
import com.bitwarden.ui.util.asText
import com.x8bit.bitwarden.ui.platform.base.BitwardenComposeTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.Year
import java.time.ZoneOffset

class AboutScreenTest : BitwardenComposeTest() {
    private var haveCalledNavigateBack = false

    private val mutableStateFlow = MutableStateFlow(DEFAULT_STATE)
    private val mutableEventFlow = bufferedMutableSharedFlow<AboutEvent>()
    val viewModel: AboutViewModel = mockk {
        every { stateFlow } returns mutableStateFlow
        every { eventFlow } returns mutableEventFlow
        every { trySendAction(any()) } just runs
    }

    private val intentManager: IntentManager = mockk {
        every { launchUri(any()) } just runs
    }

    @Before
    fun setup() {
        setContent(
            intentManager = intentManager,
        ) {
            AboutScreen(
                viewModel = viewModel,
                onNavigateBack = { haveCalledNavigateBack = true },
                onNavigateToHelpCenter = {},
                onNavigateToPrivacyPolicy = {},
            )
        }
    }

    @Test
    fun `on back click should send BackClick`() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verify { viewModel.trySendAction(AboutAction.BackClick) }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `on bitwarden help center click should send HelpCenterClick`() {
        composeTestRule.onNodeWithText("Bitwarden help center").performClick()
        verify {
            viewModel.trySendAction(ofType<AboutAction.HelpCenterClick>())
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `on privacy policy click should send PrivacyPolicyClick`() {
        composeTestRule.onNodeWithText("Privacy Policy").performClick()
        verify {
            viewModel.trySendAction(ofType<AboutAction.PrivacyPolicyClick>())
        }
    }

    @Suppress("MaxLineLength")
    @Test
    fun `on bitwarden web vault click should display confirmation dialog and confirm click should emit WebVaultClick`() {
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
        composeTestRule.onNodeWithText("Bitwarden web vault").performClick()
        composeTestRule.onNode(isDialog()).assertExists()
        composeTestRule
            .onAllNodesWithText("Continue")
            .filterToOne(hasAnyAncestor(isDialog()))
            .performClick()
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
        verify {
            viewModel.trySendAction(AboutAction.WebVaultClick)
        }
    }

    @Test
    fun `on NavigateBack should call onNavigateBack`() {
        mutableEventFlow.tryEmit(AboutEvent.NavigateBack)
        assertTrue(haveCalledNavigateBack)
    }

    @Test
    fun `on NavigateToWebVault should call launchUri on IntentManager`() {
        val testUrl = "www.testUrl.com"
        mutableEventFlow.tryEmit(AboutEvent.NavigateToWebVault(testUrl))
        verify {
            intentManager.launchUri(testUrl.toUri())
        }
    }

    @Test
    fun `submit crash logs switch should be displayed according to state`() {
        mutableStateFlow.update { it.copy(shouldShowCrashLogsButton = true) }

        composeTestRule
            .onNodeWithText("Submit crash logs")
            .assertIsDisplayed()

        mutableStateFlow.update { it.copy(shouldShowCrashLogsButton = false) }

        composeTestRule
            .onNodeWithText("Submit crash logs")
            .assertIsNotDisplayed()
    }

    @Test
    fun `on submit crash logs toggle should send SubmitCrashLogsClick`() {
        val enabled = true
        composeTestRule.onNodeWithText("Submit crash logs").performClick()
        verify {
            viewModel.trySendAction(AboutAction.SubmitCrashLogsClick(enabled))
        }
    }

    fun `on submit crash logs should be toggled on or off according to the state`() {
        composeTestRule.onNodeWithText("Submit crash logs").assertIsOff()
        mutableStateFlow.update { it.copy(isSubmitCrashLogsEnabled = true) }
        composeTestRule.onNodeWithText("Submit crash logs").assertIsOn()
    }

    @Test
    fun `on version info click should send VersionClick`() {
        composeTestRule.onNodeWithText("Version: 1.0.0 (1)")
            .performScrollTo()
            .performClick()
        verify {
            viewModel.trySendAction(AboutAction.VersionClick)
        }
    }

    @Test
    fun `version should update according to the state`() = runTest {
        composeTestRule.onNodeWithText("Version: 1.0.0 (1)")
            .performScrollTo()
            .assertIsDisplayed()

        mutableStateFlow.update { it.copy(version = "Version: 1.1.0 (2)".asText()) }

        composeTestRule.onNodeWithText("Version: 1.1.0 (2)").assertIsDisplayed()
    }

    @Test
    fun `copyright info should update according to the state`() = runTest {
        val fixedClock = Clock.fixed(Instant.parse("2024-01-25T00:00:00Z"), ZoneOffset.UTC)
        val currentYear = Year.now(fixedClock).value

        mutableStateFlow.update {
            it.copy(copyrightInfo = "© Bitwarden Inc. 2015-$currentYear".asText())
        }

        composeTestRule.onNodeWithText("© Bitwarden Inc. 2015-$currentYear")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
}

private val DEFAULT_STATE = AboutState(
    version = "Version: 1.0.0 (1)".asText(),
    sdkVersion = "\uD83E\uDD80 SDK: 1.0.0-20250708.105256-238".asText(),
    serverData = "\uD83C\uDF29 Server: 2025.7.1 @ US".asText(),
    deviceData = "device_data".asText(),
    ciData = "ci_data".asText(),
    isSubmitCrashLogsEnabled = false,
    shouldShowCrashLogsButton = true,
    copyrightInfo = "".asText(),
)
