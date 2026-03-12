package org.rtyuio.flow


import org.rtyuio.flow.R
import android.content.Context
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.preferencesDataStore
import org.rtyuio.flow.ui.theme.FlowTheme
import org.rtyuio.flow.ui.theme.Purple40

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Check
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalTime
import java.util.Calendar
import android.os.Parcelable
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize


@Parcelize
data class ScheduleItem(val hour: Int = 0, val minute: Int = 0, val isDone: Boolean = false) :
    Parcelable, Comparable<ScheduleItem> {
    override fun compareTo(other: ScheduleItem): Int = when {
        this.hour != other.hour -> this.hour compareTo other.hour
        this.minute != other.minute -> this.minute compareTo other.minute
        else -> 0
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ScheduleItem) {
            this.hour == other.hour && this.minute == other.minute
        } else {
            false
        }
    }
}


class MainActivity : ComponentActivity() {

    val Context.dataStore by preferencesDataStore(name = "Flow_prefs")

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var editing by remember { mutableStateOf(false) }
            var checklistItems by rememberSaveable { mutableStateOf(emptyList<ScheduleItem>()) }
            var showTimePickerDialog by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            FlowTheme(dynamicColor = false) {
                Scaffold() { paddingValues ->

                    val snackbarHost = SnackbarHost(snackbarHostState)

                    val calendar = Calendar.getInstance()
                    val timePicker = rememberTimePickerState(
                        calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true
                    )

                    if (showTimePickerDialog) {
                        NewChecklistItemDialog(
                            onDismiss = { showTimePickerDialog = false },
                            onConfirm = {

                                val newChecklistItem = ScheduleItem(
                                    hour = timePicker.hour,
                                    minute = timePicker.minute
                                )

                                if (newChecklistItem in checklistItems) {
                                    scope.launch { snackbarHostState.showSnackbar("Schedule already exists!", withDismissAction = true) }
                                } else {
                                    checklistItems = (checklistItems + newChecklistItem).sorted()
                                }

                                showTimePickerDialog = false;
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

                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()
                                .aspectRatio(1.0f),
                            trackColor = Purple40,
                            strokeWidth = 30.dp,
                            progress = { 0.4f }

                        )


                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(8f)
                                .background(Purple40),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(
                                onClick = { showTimePickerDialog = true },
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
                                onClick = { editing = !editing },
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
                            items(checklistItems, key = { it }) { item ->
                                ChecklistItem(
                                    editing = editing,
                                    onDelete = {
                                        checklistItems = checklistItems.filter { it != item }
                                    },
                                    hour = item.hour,
                                    minute = item.minute,
                                    isDone = item.isDone
                                )


                            }
                        }
                    }
                }
            }
        }
    }


}


@Composable
fun ChecklistItem(
    hour: Int, minute: Int, isDone: Boolean, editing: Boolean = true, onDelete: () -> Unit = {}
) {

    Card(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                fontSize = 12.em,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            if (editing) {
                IconButton(onClick = { onDelete() }, modifier = Modifier.offset((-16).dp)) {
                    Icon(
                        imageVector = Icons.Sharp.Delete,
                        contentDescription = "Add a schedule item",
                        modifier = Modifier.size(256.dp),
                    )
                }

            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ChecklistItemPreview() {
    ChecklistItem(0, 0, isDone = false, editing = false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChecklistItemDialog(
    timePickerState: TimePickerState, onDismiss: () -> Unit = {}, onConfirm: () -> Unit = {}
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = true
        ), onDismissRequest = {},

        dismissButton = {
            TextButton(onDismiss) { Text("Cancel") }
        },

        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm") }
        }, text = {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
//                Image(
//                    painterResource(R.drawable.cum),
//                    contentDescription = "sex",
//                    Modifier.align(Alignment.CenterHorizontally)s
//                )
                    TimeInput(
                        timePickerState,
                    )
                }
            }

        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun NewChecklistItemDialogPreview(

) {
    NewChecklistItemDialog(TimePickerState(0, 0, true))
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun GreetingPreview() {
    var editing by remember { mutableStateOf(false) }
    var checklistItems by remember { mutableStateOf((0..10).toList()) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    FlowTheme(dynamicColor = false) {
        Scaffold() { paddingValues ->

            if (showTimePickerDialog) {
                NewChecklistItemDialogPreview()
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

                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.0f),
                    trackColor = Purple40,
                    strokeWidth = 30.dp,
                    progress = { 0.4f }

                )


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
                        }, hour = 0, minute = 0, isDone = false)


                    }
                }
            }
        }
    }
}



