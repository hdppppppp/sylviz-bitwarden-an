package com.x8bit.bitwarden.ui.platform.feature.settings.organizations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.base.util.toListItemCardStyle
import com.bitwarden.ui.platform.components.appbar.BitwardenMediumTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.icon.model.IconData
import com.bitwarden.ui.platform.components.row.BitwardenPushRow
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString

/**
 * 组织详情页面
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMembers: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToCollections: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToGroups: (organizationId: String, organizationName: String) -> Unit,
    onNavigateToPolicies: (organizationId: String, organizationName: String) -> Unit,
    viewModel: OrganizationDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is OrganizationDetailEvent.NavigateBack -> onNavigateBack()
            is OrganizationDetailEvent.NavigateToMembers -> {
                onNavigateToMembers(state.organizationId, state.organizationName.toString())
            }
            is OrganizationDetailEvent.NavigateToCollections -> {
                onNavigateToCollections(state.organizationId, state.organizationName.toString())
            }
            is OrganizationDetailEvent.NavigateToGroups -> {
                onNavigateToGroups(state.organizationId, state.organizationName.toString())
            }
            is OrganizationDetailEvent.NavigateToPolicies -> {
                onNavigateToPolicies(state.organizationId, state.organizationName.toString())
            }
        }
    }
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    
    BitwardenScaffold(
        topBar = {
            BitwardenMediumTopAppBar(
                title = state.organizationName.toString(),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = BitwardenString.back),
                    onNavigationIconClick = { viewModel.trySendAction(OrganizationDetailAction.BackClick) },
                ),
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            state.menuItems.forEachIndexed { index, menuItem ->
                BitwardenPushRow(
                    text = menuItem.title(),
                    onClick = { viewModel.trySendAction(OrganizationDetailAction.MenuItemClick(menuItem)) },
                    leadingIcon = IconData.Local(iconRes = menuItem.iconRes),
                    cardStyle = state.menuItems.toListItemCardStyle(
                        index = index,
                        dividerPadding = 48.dp,
                    ),
                    modifier = Modifier
                        .standardHorizontalMargin()
                        .fillMaxSize(),
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}
