package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitwarden.ui.platform.base.util.EventsEffect
import com.bitwarden.ui.platform.base.util.standardHorizontalMargin
import com.bitwarden.ui.platform.components.appbar.BitwardenMediumTopAppBar
import com.bitwarden.ui.platform.components.appbar.NavigationIcon
import com.bitwarden.ui.platform.components.card.bitwardenCardColors
import com.bitwarden.ui.platform.components.content.BitwardenLoadingContent
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString
import com.bitwarden.ui.util.asText

/**
 * 组织成员列表页面
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationMembersScreen(
    onNavigateBack: () -> Unit,
    viewModel: OrganizationMembersViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is OrganizationMembersEvent.NavigateBack -> onNavigateBack()
        }
    }
    
    // 对话框
    when (val dialog = state.dialogState) {
        is OrganizationMembersState.DialogState.Error -> {
            BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.an_error_has_occurred),
                message = dialog.message(),
                onDismissRequest = { viewModel.trySendAction(OrganizationMembersAction.DismissDialog) },
            )
        }
        is OrganizationMembersState.DialogState.Loading -> {
            BitwardenLoadingDialog(text = dialog.message())
        }
        is OrganizationMembersState.DialogState.ConfirmDelete -> {
            BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.remove_member),
                message = stringResource(id = BitwardenString.remove_member_confirmation).asText(),
                onDismissRequest = { viewModel.trySendAction(OrganizationMembersAction.DismissDialog) },
            )
        }
        null -> Unit
    }
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    
    BitwardenScaffold(
        topBar = {
            BitwardenMediumTopAppBar(
                title = stringResource(id = BitwardenString.members),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = BitwardenString.back),
                    onNavigationIconClick = { viewModel.trySendAction(OrganizationMembersAction.BackClick) },
                ),
                actions = {
                    IconButton(onClick = { viewModel.trySendAction(OrganizationMembersAction.InviteClick) }) {
                        Icon(
                            painter = painterResource(id = BitwardenDrawable.ic_plus),
                            contentDescription = stringResource(id = BitwardenString.invite_member),
                        )
                    }
                },
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        when (val viewState = state.viewState) {
            is OrganizationMembersState.ViewState.Loading -> {
                BitwardenLoadingContent(modifier = Modifier.fillMaxSize())
            }
            is OrganizationMembersState.ViewState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = viewState.members,
                        key = { it.id },
                    ) { member ->
                        Card(
                            modifier = Modifier
                                .standardHorizontalMargin()
                                .padding(vertical = 4.dp),
                            colors = bitwardenCardColors(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = member.name ?: member.email ?: "Unknown",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    member.email?.let {
                                        Text(
                                            text = it,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.trySendAction(
                                            OrganizationMembersAction.RemoveMemberClick(member.id),
                                        )
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = BitwardenDrawable.ic_trash),
                                        contentDescription = stringResource(id = BitwardenString.remove_member),
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
            is OrganizationMembersState.ViewState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = stringResource(id = BitwardenString.no_members))
                }
            }
            is OrganizationMembersState.ViewState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = stringResource(id = BitwardenString.an_error_has_occurred))
                }
            }
        }
    }
}
