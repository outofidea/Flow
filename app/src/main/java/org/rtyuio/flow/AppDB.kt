package org.rtyuio.flow

import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import  androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate


class Converters {
    @TypeConverter
    fun dateFromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(value) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }

}


@Entity(tableName = "schedule")
 data  class ScheduleItemDb(
    @PrimaryKey val time: String,
    @ColumnInfo("isDone") val isDone: Boolean = false,
    @ColumnInfo("date") val date: LocalDate
)

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule")
    suspend fun getAllScheduleItems(): List<ScheduleItemDb>

    @Delete
    suspend fun delete(scheduleItem: ScheduleItemDb)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(vararg scheduleItems: ScheduleItemDb)
}

@TypeConverters(Converters::class)
@Database(entities = [ScheduleItemDb::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
