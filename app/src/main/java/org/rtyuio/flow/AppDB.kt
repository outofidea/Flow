package org.rtyuio.flow

import android.content.Context
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import  androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date


class Converters {
    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

}

@Entity(tableName = "schedule")
public data class ScheduleItemDb(
    @PrimaryKey val timestamp: Date,
    @ColumnInfo("isDone") val isDone: Boolean
)

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule")
    suspend fun getAllScheduleItems(): List<ScheduleItemDb>

    @Delete
    suspend fun delete(scheduleItem: ScheduleItemDb)

    @Insert
    suspend fun insertAll(vararg scheduleItems: ScheduleItemDb)
}

@TypeConverters(Converters::class)
@Database(entities = [ScheduleItemDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}

class AppDB {
    companion object {
        @Volatile
        private var dbInstance: AppDatabase? = null;
        fun getDbInstance(ctx: Context): AppDatabase {
            return dbInstance ?: synchronized(this) {
                return buildDb(ctx).also { dbInstance = it }
            }
            // will hang if ts gets me a null reference exception
        }

        private fun buildDb(ctx: Context): AppDatabase {
            return Room.databaseBuilder(ctx, AppDatabase::class.java, "Flow-schedule-db").build()
        }
    }

}