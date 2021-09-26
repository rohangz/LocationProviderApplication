package com.rohangz.location.platform.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: LocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(locations: List<LocationEntity>): List<Long>

    @Query("SELECT * FROM location WHERE id = :id")
    fun selectById(id: Long): Flow<LocationEntity>

    @Query("SELECT * FROM location")
    fun selectAll(): Flow<List<LocationEntity>>
}