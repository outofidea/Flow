package org.rtyuio.flow.ui.scheduler

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.rtyuio.flow.ScheduleDao
import org.rtyuio.flow.ScheduleItemDb
import org.rtyuio.flow.shared.ScheduleData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.rtyuio.flow.shared.DialogTriggerType
import org.rtyuio.flow.shared.DialogTriggerInfo
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class SchedulerViewModel @Inject constructor(private val dao: ScheduleDao) : ViewModel() {

    var editing by mutableStateOf(false)
    var checklistItems = mutableStateListOf<ScheduleData>()
    var showTimePickerDialog by mutableStateOf(false)

    var dialogTriggerInfo by mutableStateOf<DialogTriggerInfo>(DialogTriggerInfo())

    var progress by mutableFloatStateOf(0.0f)

    init {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                dao.getAllScheduleItems().map { scheduleItemDb ->
                    val parts = scheduleItemDb.time.split("-")
                    ScheduleData(
                        parts.getOrElse(0) { "0" }.toInt(),
                        parts.getOrElse(1) { "0" }.toInt(),
                        scheduleItemDb.isDone
                    )
                }.sorted()
            }
            checklistItems.addAll(items)
            recalculateProgress()
        }

    }

    fun dismissTimePicker() {
        showTimePickerDialog = false
    }

    fun confirmTimePicker(time: TimePickerState) {
        when (dialogTriggerInfo.type) {
            DialogTriggerType.ADD -> {
                addChecklistItem(time.hour, time.minute)
            }

            DialogTriggerType.EDIT -> {
                editChecklistItem(dialogTriggerInfo.idx!!, ScheduleData(time.hour, time.minute))
            }

            DialogTriggerType.NONE -> {}
        }


        dismissTimePicker()
    }

    fun showTimePicker() {
        showTimePickerDialog = true
    }

    fun changeEditState(state: Boolean? = null) {
        editing = state ?: !editing
    }

    private fun recalculateProgress() {
        val itemsDone = checklistItems.sumOf { if (it.isDone) 1 else 0 }
        progress = if (itemsDone == 0) 0.0f else itemsDone.toFloat() / checklistItems.size
    }

    fun completeItem() {
        val idx = checklistItems.indexOfFirst { !it.isDone }
        if (idx != -1) {
            val oldItem = checklistItems[idx]
            val newItem = oldItem.copy(isDone = true)

            // This re-assignment to the SnapshotStateList at index 'idx' 
            // will correctly trigger a recomposition in Compose.
            checklistItems[idx] = newItem

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val timeString = "${oldItem.hour}-${oldItem.minute}"
                    dao.insertAll(ScheduleItemDb(timeString, true, LocalDate.now()))
                }
            }
            recalculateProgress()
        }


    }


    fun addChecklistItem(hour: Int, minute: Int): Boolean {
        if (checklistItems.any { it.hour == hour && it.minute == minute }) {
            return false
        } else {
            val item = ScheduleData(hour, minute)
            checklistItems.add(item)
            checklistItems.sort()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val timeString = "$hour-$minute"
                    val newItem = ScheduleItemDb(timeString, false, LocalDate.now())
                    dao.insertAll(newItem)
                }
            }
            recalculateProgress()
            return true
        }
    }

    fun setDialogInfo(info: DialogTriggerInfo) {
        dialogTriggerInfo = info
    }

    fun deleteChecklistItem(item: ScheduleData) {
        checklistItems.remove(item)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val timeString = "${item.hour}-${item.minute}"
                dao.delete(ScheduleItemDb(timeString, item.isDone, LocalDate.now()))
            }
        }
        recalculateProgress()
    }

    fun editChecklistItem(index: Int, newItem: ScheduleData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val delItem = checklistItems[index]

                dao.delete(
                    ScheduleItemDb(
                        "${delItem.hour}-${delItem.minute}",
                        false,
                        LocalDate.now()
                    )
                )

                dao.insertAll(
                    ScheduleItemDb(
                        "${newItem.hour}-${newItem.minute}",
                        false,
                        LocalDate.now()
                    )
                )
            }
            checklistItems[index] = newItem
        }
    }
}
