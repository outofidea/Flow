package org.rtyuio.flow


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.rtyuio.flow.ui.scheduler.ScheduleScreen
import org.rtyuio.flow.ui.scheduler.SchedulerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val Context.dataStore by preferencesDataStore(name = "Flow_prefs")

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        val scheduleViewModel: SchedulerViewModel by viewModels()

        super.onCreate(savedInstanceState)

        setContent {
            ScheduleScreen(scheduleViewModel)
        }
    }

    override fun onStop() {
        //* save db state
        super.onStop()
    }
}
