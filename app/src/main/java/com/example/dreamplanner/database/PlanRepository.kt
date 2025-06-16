package com.example.dreamplanner.database

import androidx.lifecycle.LiveData

class PlanRepository(private val planDao: PlanDao, private val goalDao: GoalDao, private val dailyTaskDao: DailyTaskDao) {

    val allPlans: LiveData<List<Plan>> = planDao.getAll()
    val allGoals: LiveData<List<Goal>> = goalDao.getAll()
    val allDailyTasks: LiveData<List<DailyTask>> = dailyTaskDao.getAll()

    suspend fun insertPlan(plan: Plan) = planDao.insert(plan)
    suspend fun updatePlan(plan: Plan) = planDao.update(plan)
    suspend fun deletePlan(plan: Plan) = planDao.delete(plan)

    suspend fun insertGoal(goal: Goal) = goalDao.insert(goal)
    suspend fun updateGoal(goal: Goal) = goalDao.update(goal)
    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)

    suspend fun insertDailyTask(task: DailyTask) = dailyTaskDao.insert(task)
    suspend fun updateDailyTask(task: DailyTask) = dailyTaskDao.update(task)
    suspend fun deleteDailyTask(task: DailyTask) = dailyTaskDao.delete(task)
}