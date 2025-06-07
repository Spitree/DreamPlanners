package com.example.dreamplanner.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlanViewModel(application:Application) : AndroidViewModel(application) {
    private val planDao = AppDatabase.getInstance(application).planDao()
    private val dailyTaskDao = AppDatabase.getInstance(application).dailyTaskDao()
    private val sleepEntryDao = AppDatabase.getInstance(application).sleepEntryDao()
    private val goalDao = AppDatabase.getInstance(application).goalDao()
    //Pobiera singletonową instancję bazy i DAO do obsługi tabeli User.
    val plans: LiveData<List<Plan>> = planDao.getAll()
    val dailyTasks: LiveData<List<DailyTask>> = dailyTaskDao.getAll()
    val sleepEntries: LiveData<List<SleepEntry>> = sleepEntryDao.getAll()
    val goals: LiveData<List<Goal>> = goalDao.getAll()

    init {
        viewModelScope.launch {
            // Dodaj dane jeśli nie ma żadnych w bazie
            if (planDao.count() == 0) {
                planDao.insertAll(samplePlans)
            }
            if (dailyTaskDao.count() == 0) {
                dailyTaskDao.insertAll(sampleDailyTasks)
            }
            if (goalDao.count() == 0) {
                goalDao.insertAll(sampleGoals)
            }
            if (sleepEntryDao.count() == 0) {
                sleepEntryDao.insertAll(sampleSleepEntries)
            }
        }
    }
    //Udostępnia obserwowalną listę użytkowników z bazy.
    fun addPlan(
        plan: Plan
    ) {
        viewModelScope.launch {
            planDao.insert(plan)
        }
    }
}
