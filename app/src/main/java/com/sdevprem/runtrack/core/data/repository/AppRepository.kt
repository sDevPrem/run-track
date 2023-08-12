package com.sdevprem.runtrack.core.data.repository

import com.sdevprem.runtrack.core.data.db.dao.RunDao
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.data.utils.RunSortOrder
import kotlinx.coroutines.flow.Flow
import java.util.Date
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

    fun getRunByDescDateWithLimit(limit: Int) = runDao.getRunByDescDateWithLimit(limit)

    fun getTotalRunningDuration(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalRunningDuration(fromDate, toDate)

    fun getTotalCaloriesBurned(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalCaloriesBurned(fromDate, toDate)

    fun getTotalDistance(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalDistance(fromDate, toDate)

    fun getTotalAvgSpeed(fromDate: Date? = null, toDate: Date? = null): Flow<Float> =
        runDao.getTotalAvgSpeed(fromDate, toDate)

}