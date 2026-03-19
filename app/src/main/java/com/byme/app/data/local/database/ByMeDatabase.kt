package com.byme.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.byme.app.data.local.dao.UserDao
import com.byme.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ByMeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}