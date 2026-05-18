package com.accurate.userdirectory.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.accurate.userdirectory.data.local.dao.ActivityLogDao
import com.accurate.userdirectory.data.local.dao.CityDao
import com.accurate.userdirectory.data.local.dao.UserDao
import com.accurate.userdirectory.data.local.entity.ActivityLogEntity
import com.accurate.userdirectory.data.local.entity.CityEntity
import com.accurate.userdirectory.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CityEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AccurateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cityDao(): CityDao
    abstract fun activityLogDao(): ActivityLogDao
}
