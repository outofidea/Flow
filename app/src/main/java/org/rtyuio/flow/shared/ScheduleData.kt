package org.rtyuio.flow.shared

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import kotlin.compareTo

@Parcelize
data class ScheduleData(val hour: Int, val minute: Int, val isDone: Boolean = false) :
    Parcelable, Comparable<ScheduleData> {
    override fun compareTo(other: ScheduleData): Int =
        when (this.hour compareTo other.hour) {
            0 -> this.minute compareTo other.minute
            else -> this.hour compareTo other.hour
        }
}
