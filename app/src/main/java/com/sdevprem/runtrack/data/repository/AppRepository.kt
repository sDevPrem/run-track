package com.sdevprem.runtrack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sdevprem.runtrack.data.db.mapper.toDataModel
import com.sdevprem.runtrack.data.db.mapper.toDateTime
import com.sdevprem.runtrack.data.db.mapper.toEntity
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.data.utils.RunSortOrder
import com.sdevprem.runtrack.shared.data.db.dao.RunDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run.toEntity())

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run.toEntity())

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

    suspend fun getRunStatsInDateRange(fromDate: Date?, toDate: Date?) =
        runDao.getRunStatsInDateRange(fromDate?.toDateTime(), toDate?.toDateTime())
            .map { it.toDataModel() }

    fun getRunByDescDateWithLimit(limit: Int) = runDao.getRunByDescDateWithLimit(limit)
        .map { list -> list.map { it.toDataModel() } }

    fun getTotalRunningDuration(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalRunningDuration(fromDate?.toDateTime(), toDate?.toDateTime())

    fun getTotalCaloriesBurned(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalCaloriesBurned(fromDate?.toDateTime(), toDate?.toDateTime())

    fun getTotalDistance(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
        runDao.getTotalDistance(fromDate?.toDateTime(), toDate?.toDateTime())

    fun getTotalAvgSpeed(fromDate: Date? = null, toDate: Date? = null): Flow<Float> =
        runDao.getTotalAvgSpeed(fromDate?.toDateTime(), toDate?.toDateTime())

}