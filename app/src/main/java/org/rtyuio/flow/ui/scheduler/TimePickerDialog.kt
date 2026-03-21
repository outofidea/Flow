package org.rtyuio.flow.ui.scheduler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
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
@Preview(showSystemUi = true, showBackground = false)
fun TimePickerDialogPreview(

) {
    TimePickerDialog(TimePickerState(0, 0, true))
}
