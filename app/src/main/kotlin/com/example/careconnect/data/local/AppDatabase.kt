package com.example.careconnect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.careconnect.data.local.dao.UserDao
import com.example.careconnect.data.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // Add other DAOs here
}

