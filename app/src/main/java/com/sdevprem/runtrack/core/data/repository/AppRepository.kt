package com.sdevprem.runtrack.core.data.repository

import com.sdevprem.runtrack.core.data.db.dao.RunDao
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getSortedAllRun(sortingOrder: RunSortOrder) = when (sortingOrder) {
        RunSortOrder.DATE -> runDao.getAllRunSortByDate()
        RunSortOrder.DURATION -> runDao.getAllRunSortByDuration()
        RunSortOrder.CALORIES_BURNED -> runDao.getAllRunSortByCaloriesBurned()
        RunSortOrder.AVG_SPEED -> runDao.getAllRunSortByAvgSpeed()
        RunSortOrder.DISTANCE -> runDao.getAllRunSortByDistance()
    }

    fun getTotalRunningDuration(): Flow<Long> = runDao.getTotalRunningDuration()

    fun getTotalCaloriesBurned(): Flow<Long> = runDao.getTotalCaloriesBurned()

    fun getTotalDistance(): Flow<Long> = runDao.getTotalDistance()

    fun getTotalAvgSpeed(): Flow<Float> = runDao.getTotalAvgSpeed()

}