package com.x8bit.bitwarden.ui.platform.feature.settings.organizations.collections

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
import com.bitwarden.ui.platform.components.card.BitwardenCard
import com.bitwarden.ui.platform.components.content.BitwardenLoadingContent
import com.bitwarden.ui.platform.components.dialog.BitwardenBasicDialog
import com.bitwarden.ui.platform.components.dialog.BitwardenLoadingDialog
import com.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.bitwarden.ui.platform.resource.BitwardenDrawable
import com.bitwarden.ui.platform.resource.BitwardenString

/**
 * 组织集合列表页面
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationCollectionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: OrganizationCollectionsViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is OrganizationCollectionsEvent.NavigateBack -> onNavigateBack()
        }
    }
    
    // 对话框
    when (val dialog = state.dialogState) {
        is OrganizationCollectionsState.DialogState.Error -> {
            BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.an_error_has_occurred),
                message = dialog.message(),
                onDismissRequest = { viewModel.trySendAction(OrganizationCollectionsAction.DismissDialog) },
            )
        }
        is OrganizationCollectionsState.DialogState.Loading -> {
            BitwardenLoadingDialog(text = dialog.message())
        }
        is OrganizationCollectionsState.DialogState.ConfirmDelete -> {
            BitwardenBasicDialog(
                title = stringResource(id = BitwardenString.delete_collection),
                message = stringResource(id = BitwardenString.delete_collection_confirmation).asText(),
                onDismissRequest = { viewModel.trySendAction(OrganizationCollectionsAction.DismissDialog) },
            )
        }
        null -> Unit
    }
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    
    BitwardenScaffold(
        topBar = {
            BitwardenMediumTopAppBar(
                title = stringResource(id = BitwardenString.collections),
                scrollBehavior = scrollBehavior,
                navigationIcon = NavigationIcon(
                    navigationIcon = rememberVectorPainter(id = BitwardenDrawable.ic_back),
                    navigationIconContentDescription = stringResource(id = BitwardenString.back),
                    onNavigationIconClick = { viewModel.trySendAction(OrganizationCollectionsAction.BackClick) },
                ),
                actions = {
                    IconButton(onClick = { viewModel.trySendAction(OrganizationCollectionsAction.AddClick) }) {
                        Icon(
                            painter = painterResource(id = BitwardenDrawable.ic_plus),
                            contentDescription = stringResource(id = BitwardenString.add_collection),
                        )
                    }
                },
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        when (val viewState = state.viewState) {
            is OrganizationCollectionsState.ViewState.Loading -> {
                BitwardenLoadingContent(modifier = Modifier.fillMaxSize())
            }
            is OrganizationCollectionsState.ViewState.Content -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = viewState.collections,
                        key = { it.id },
                    ) { collection ->
                        BitwardenCard(
                            modifier = Modifier
                                .standardHorizontalMargin()
                                .padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = collection.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.trySendAction(
                                            OrganizationCollectionsAction.DeleteCollectionClick(collection.id),
                                        )
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = BitwardenDrawable.ic_trash),
                                        contentDescription = stringResource(id = BitwardenString.delete_collection),
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
            is OrganizationCollectionsState.ViewState.Empty -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = stringResource(id = BitwardenString.no_collections))
                }
            }
        }
    }
}
