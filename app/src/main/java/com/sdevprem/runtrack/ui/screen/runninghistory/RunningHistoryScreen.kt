package com.sdevprem.runtrack.ui.screen.runninghistory

import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import com.sdevprem.runtrack.ui.common.compose.component.DropDownList
import com.sdevprem.runtrack.ui.common.compose.component.RunInfoDialog
import com.sdevprem.runtrack.ui.common.compose.component.RunItem
import com.sdevprem.runtrack.ui.common.compose.compositonLocal.LocalScaffoldBottomPadding

@Composable
fun RunningHistoryScreen(
    navController: NavController,
    viewModel: RunningHistoryVM = hiltViewModel()
) {
    val runItems = viewModel.runList.collectAsLazyPagingItems()


    RunningHistoryScreenContent(
        runItems = runItems,
        onSortOrderSelected = viewModel::setSortOrder,
        onItemClick = viewModel::setDialogRun,
        onNavIconClick = { navController.navigateUp() }
    )

    viewModel.dialogRun.collectAsStateWithLifecycle().value?.let {
        RunInfoDialog(
            run = it,
            onDismiss = { viewModel.setDialogRun(null) },
            onDelete = { viewModel.deleteRun() }
        )
    }
}

@Composable
private fun RunningHistoryScreenContent(
    runItems: LazyPagingItems<Run>,
    onSortOrderSelected: (RunSortOrder) -> Unit,
    onItemClick: (Run) -> Unit,
    onNavIconClick: () -> Unit
) {
    Scaffold(
        topBar = { ScreenTopAppBar(onSortOrderSelected, onNavIconClick) }
    ) {
        Box(modifier = Modifier.padding(it)) {
            RunningList(
                runItems = runItems,
                onItemClick = onItemClick
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun ScreenTopAppBar(
    onSortOrderSelected: (RunSortOrder) -> Unit = {},
    onNavIconClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = "Your Activities")
        },
        navigationIcon = {
            IconButton(onClick = onNavIconClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_backward),
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            var isDropDownExpended by rememberSaveable { mutableStateOf(false) }
            val sortOrderList = remember { RunSortOrder.values().asList() }

            Column {
                Button(
                    onClick = { isDropDownExpended = !isDropDownExpended },
                ) {
                    Text(
                        text = "Sort by ",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "sort by"
                    )
                }
                DropDownList(
                    list = sortOrderList,
                    onItemSelected = {
                        onSortOrderSelected(it)
                        isDropDownExpended = false
                    },
                    request = { isDropDownExpended = it },
                    isOpened = isDropDownExpended,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            }
        }
    )
}

@Composable
private fun RunningList(
    runItems: LazyPagingItems<Run>,
    onItemClick: (Run) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = LocalScaffoldBottomPadding.current + 8.dp)
    ) {

        if (runItems.loadState.refresh == LoadState.Loading) item {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
        else items(runItems.itemCount) {
            runItems[it]?.let { run ->
                RunCardItem(run = run, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun RunCardItem(
    run: Run,
    onItemClick: (Run) -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .clickable { onItemClick(run) }
            .padding(vertical = 4.dp, horizontal = 16.dp)
    ) {
        RunItem(
            run = run,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RunCardItemPreview() {
    val runList = List(4) {
        Run(
            img = BitmapFactory.decodeResource(
                LocalContext.current.resources,
                R.drawable.running_boy
            )
        )
    }

    RunCardItem(run = runList[0])
}