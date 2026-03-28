package com.x8bit.bitwarden.ui.platform.feature.settings.organizations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bitwarden.ui.platform.components.appbar.BitwardenMediumTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.card.BitwardenActionCard
import com.bitwarden.ui.platform.components.content.BitwardenLoadingContent
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString

/**
 * 组织列表页面
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOrganizationDetail: (organizationId: String, organizationName: String) -> Unit,
    viewModel: OrganizationsViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is OrganizationsEvent.NavigateBack -> onNavigateBack()
            is OrganizationsEvent.NavigateToOrganizationDetail -> {
                onNavigateToOrganizationDetail(
                    event.organizationId,
                    event.organizationName,
                )
            }
        }
    }
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    
    BitwardenScaffold(
        topBar = {
            BitwardenMediumTopAppBar(
                title = stringResource(id = BitwardenString.organizations),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = BitwardenString.back),
                    onNavigationIconClick = { viewModel.trySendAction(OrganizationsAction.BackClick) },
                ),
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        when (val viewState = state.viewState) {
            is OrganizationsState.ViewState.Loading -> {
                BitwardenLoadingContent(
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is OrganizationsState.ViewState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = viewState.organizations,
                        key = { it.id },
                    ) { organization ->
                        BitwardenActionCard(
                            cardTitle = organization.name.toString(),
                            actionText = stringResource(id = BitwardenString.view),
                            onActionClick = {
                                viewModel.trySendAction(
                                    OrganizationsAction.OrganizationClick(
                                        organizationId = organization.id,
                                        organizationName = organization.name.toString(),
                                    ),
                                )
                            },
                            modifier = Modifier
                                .standardHorizontalMargin()
                                .padding(vertical = 8.dp),
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
            is OrganizationsState.ViewState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    // 空状态提示
                }
            }
        }
    }
}
