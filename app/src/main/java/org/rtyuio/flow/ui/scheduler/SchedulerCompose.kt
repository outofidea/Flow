package org.rtyuio.flow.ui.scheduler

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.rtyuio.flow.R
import org.rtyuio.flow.shared.DialogTriggerInfo
import org.rtyuio.flow.ui.theme.FlowTheme
import org.rtyuio.flow.ui.theme.Purple40
import org.rtyuio.flow.shared.DialogTriggerType
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: SchedulerViewModel = viewModel()) {
    val editing = viewModel.editing
    val checklistItems = viewModel.checklistItems
    val showTimePickerDialog = viewModel.showTimePickerDialog
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    FlowTheme(dynamicColor = false) {
        Scaffold() { paddingValues ->

            val snackbarHost = SnackbarHost(snackbarHostState)

            val animatedProgress by animateFloatAsState(
                viewModel.progress,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )
            val calendar = Calendar.getInstance()
            val timePicker = rememberTimePickerState(
                0, 0, true
            )

            if (showTimePickerDialog) {
                timePicker.hour = calendar.get(Calendar.HOUR)
                timePicker.minute = calendar.get(Calendar.MINUTE)

                TimePickerDialog(
                    onDismiss = { viewModel.dismissTimePicker() },
                    onConfirm = {
                        viewModel.confirmTimePicker(timePicker)
                    },
                    timePickerState = timePicker
                )


            }

            Column(
                Modifier
                    .offset(
                        y = paddingValues.calculateTopPadding()
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(Modifier.height(50.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.0f),
                        trackColor = Purple40,
                        strokeWidth = 30.dp,
                        progress = {
                            animatedProgress
                        }
                    )

                    Image(
                        contentDescription = null,
                        painter = painterResource(id = R.drawable.cum),
                        modifier = Modifier.clickable {
                            viewModel.completeItem()
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(8f)
                        .background(Purple40),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = {
                            viewModel.showTimePicker()
                            viewModel.setDialogInfo(DialogTriggerInfo(DialogTriggerType.ADD))
                        },
                        modifier = Modifier.offset(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Add,
                            contentDescription = "Add a schedule item",
                            modifier = Modifier.size(256.dp),
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { viewModel.changeEditState() },
                        modifier = Modifier.offset((-16).dp)
                    ) {
                        Icon(
                            imageVector = if (!editing) Icons.Sharp.Edit else Icons.Sharp.Check,
                            contentDescription = "Edit a schedule item",
                            modifier = Modifier.size(256.dp),
                            tint = Color.White

                        )
                    }
                }


                LazyColumn(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .absolutePadding(bottom = paddingValues.calculateBottomPadding())
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                ) {
                    itemsIndexed(
                        checklistItems,
                        key = { _, item -> item.hashCode() }) { index, item ->
                        ChecklistItem(
                            editing = editing,
                            onDelete = {
                                viewModel.deleteChecklistItem(item)
                            },
                            hour = item.hour,
                            minute = item.minute,
                            isDone = item.isDone,
                            onEdit = {
                                viewModel.showTimePicker()
                                viewModel.setDialogInfo(
                                    DialogTriggerInfo(
                                        DialogTriggerType.EDIT,
                                        index
                                    )
                                )

                            })


                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun SchedulerComposePreview() {
    var editing by remember { mutableStateOf(false) }
    var checklistItems by remember { mutableStateOf((0..10).toList()) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    FlowTheme(dynamicColor = false) {
        Scaffold() { paddingValues ->

            if (showTimePickerDialog) {
                TimePickerDialogPreview()
            }

            Column(
                Modifier
                    .offset(
                        y = paddingValues.calculateTopPadding()
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(Modifier.height(50.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.0f),
                        trackColor = Purple40,
                        strokeWidth = 30.dp,
                        progress = { 0.4f }

                    )
                    Image(
                        contentDescription = null,
                        painter = painterResource(id = R.drawable.cum),
                        modifier = Modifier.clickable {
                            { }
                        }
                    )
                }






                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(8f)
                        .background(Purple40),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { showTimePickerDialog = true }, modifier = Modifier.offset(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Sharp.Add,
                            contentDescription = "Add a schedule item",
                            modifier = Modifier.size(256.dp),
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { editing = !editing }, modifier = Modifier.offset((-16).dp)
                    ) {
                        Icon(
                            imageVector = if (!editing) Icons.Sharp.Edit else Icons.Sharp.Check,
                            contentDescription = "Edit a schedule item",
                            modifier = Modifier.size(256.dp),
                            tint = Color.White

                        )
                    }
                }


                LazyColumn(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .absolutePadding(bottom = paddingValues.calculateBottomPadding())
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                ) {
                    items(checklistItems, key = { it }) { item ->
                        ChecklistItem(editing = editing, onDelete = {
                            checklistItems = checklistItems.filter { it != item }
                        }, hour = 0, minute = 0, isDone = false, onEdit = {})
                    }
                }
            }
        }
    }
}
