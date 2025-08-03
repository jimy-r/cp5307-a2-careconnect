package com.example.careconnect.di

import android.content.Context
import androidx.room.Room
import com.example.careconnect.data.local.AppDatabase
import com.example.careconnect.data.local.dao.UserDao
import com.example.careconnect.data.repository.CareRepository
import com.example.careconnect.data.repository.CareRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "careconnect_database"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideCareRepository(userDao: UserDao): CareRepository {
        return CareRepositoryImpl(userDao)
    }
}

