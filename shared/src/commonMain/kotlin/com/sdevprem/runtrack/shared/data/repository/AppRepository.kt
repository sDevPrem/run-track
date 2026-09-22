package com.sdevprem.runtrack.shared.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sdevprem.runtrack.shared.data.db.dao.RunDao
import com.sdevprem.runtrack.shared.data.model.Run
import com.sdevprem.runtrack.shared.data.utils.RunSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

class AppRepository(
    private val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getSortedAllRun(sortingOrder: RunSortOrder) = Pager(
        config = PagingConfig(pageSize = 20),
    ) {
        when (sortingOrder) {
            RunSortOrder.DATE -> runDao.getAllRunSortByDate()
            RunSortOrder.DURATION -> runDao.getAllRunSortByDuration()
            RunSortOrder.CALORIES_BURNED -> runDao.getAllRunSortByCaloriesBurned()
            RunSortOrder.AVG_SPEED -> runDao.getAllRunSortByAvgSpeed()
            RunSortOrder.DISTANCE -> runDao.getAllRunSortByDistance()
        }
    }

    suspend fun getRunStatsInDateRange(fromDate: LocalDateTime?, toDate: LocalDateTime?) =
        runDao.getRunStatsInDateRange(fromDate, toDate)

    fun getRunByDescDateWithLimit(limit: Int) = runDao.getRunByDescDateWithLimit(limit)

    fun getTotalRunningDuration(
        fromDate: LocalDateTime? = null,
        toDate: LocalDateTime? = null
    ): Flow<Long> =
        runDao.getTotalRunningDuration(fromDate, toDate)

    fun getTotalCaloriesBurned(
        fromDate: LocalDateTime? = null,
        toDate: LocalDateTime? = null
    ): Flow<Long> =
        runDao.getTotalCaloriesBurned(fromDate, toDate)

    fun getTotalDistance(
        fromDate: LocalDateTime? = null,
        toDate: LocalDateTime? = null
    ): Flow<Long> =
        runDao.getTotalDistance(fromDate, toDate)

    fun getTotalAvgSpeed(
        fromDate: LocalDateTime? = null,
        toDate: LocalDateTime? = null
    ): Flow<Float> =
        runDao.getTotalAvgSpeed(fromDate, toDate)

}