package com.example.dreamplanner.database;

import android.app.Application;
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlanViewModel(application:Application,private val repository: PlanRepository) : AndroidViewModel(application) {
    private val planDao = AppDatabase.getInstance(application).planDao()
    private val dailyTaskDao = AppDatabase.getInstance(application).dailyTaskDao()
    private val sleepEntryDao = AppDatabase.getInstance(application).sleepEntryDao()
    private val goalDao = AppDatabase.getInstance(application).goalDao()
    private val articleDao = AppDatabase.getInstance(application).articleDao()
    //Pobiera singletonową instancję bazy i DAO do obsługi tabeli User.
    val plans: LiveData<List<Plan>> = planDao.getAll()
    val dailyTasks: LiveData<List<DailyTask>> = dailyTaskDao.getAll()
    val sleepEntries: LiveData<List<SleepEntry>> = sleepEntryDao.getAll()
    val goals: LiveData<List<Goal>> = goalDao.getAll()
    val article: LiveData<List<Article>> = articleDao.getAll()

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
            if (articleDao.count() == 0) {
                articleDao.insertAll(sampleArticles)
            }
        }
    }

    var isLoggedIn by mutableStateOf(false)
        private set

    fun login() {
        isLoggedIn = true
    }

    fun logout() {
        isLoggedIn = false
    }

    //Udostępnia obserwowalną listę użytkowników z bazy.
    fun togglePlanCompleted(plan: Plan, completed: Boolean) {
        viewModelScope.launch {
            repository.updatePlan(plan.copy(completed = completed))
        }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            repository.deletePlan(plan)
        }
    }

    fun addPlan(plan: Plan) {
        viewModelScope.launch {
            repository.insertPlan(plan)
        }
    }


    fun toggleDailyTaskCompleted(task: DailyTask, completed: Boolean) {
        viewModelScope.launch {
            repository.updateDailyTask(task.copy(completed = completed))
        }
    }

    fun deleteDailyTask(task: DailyTask) {
        viewModelScope.launch {
            repository.deleteDailyTask(task)
        }
    }

    fun addDailyTask(task: DailyTask) {
        viewModelScope.launch {
            repository.insertDailyTask(task)
        }
    }


    fun toggleGoalCompleted(goal: Goal, completed: Boolean) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(completed = completed))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.insertGoal(goal)
        }
    }

    fun clearAndInsertPlans(plans: List<Plan>) {
        viewModelScope.launch {
            // Usuń wszystkie istniejące plany
            planDao.getAll().value?.forEach { planDao.delete(it) }
            planDao.deleteAll()

            // Wstaw nowe plany
            planDao.insertAll(plans)
        }
    }

    suspend fun deleteAllPlans() {
        planDao.getAll().value?.forEach { planDao.delete(it) }
    }

    suspend fun insertAll(plans: List<Plan>) {
        planDao.insertAll(plans)
    }
}
