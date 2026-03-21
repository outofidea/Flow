package org.rtyuio.flow.ui.scheduler

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em


@Composable
fun ChecklistItem(
    hour: Int,
    minute: Int,
    isDone: Boolean,
    editing: Boolean = true,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .clickable {
                if (editing) {
                    onEdit()
                }
            }
    ) {
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

            if (!editing) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Sharp.Done,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .offset((-16).dp),
                    )
                }

            } else {
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
    ChecklistItem(
        0, 0, isDone = true, editing = false, {},
        {}
    )
}