// Defines the file's location within the dependency injection architecture.
package com.example.careconnect.di

// Imports necessary libraries for context, Room, and the app's classes.
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
import javax.inject.Inject
import javax.inject.Singleton

// Marks this object as a Hilt module for providing dependencies.
@Module
// Installs this module's bindings into the SingletonComponent, making them app-wide.
@InstallIn(SingletonComponent::class)
// Defines a singleton object to hold all our app-wide dependency providers.
object AppModule {

    // Marks this function as a provider of a dependency.
    @Provides
    // Ensures only a single instance of the database is created for the entire app.
    @Singleton
    // Defines a function that tells Hilt how to create and provide the AppDatabase instance.
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        // Returns a newly built instance of the Room database.
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "careconnect_database"
        ).build()
    }

    // Marks this function as a provider for the UserDao dependency.
    @Provides
    // Defines a function that tells Hilt how to get the UserDao from the AppDatabase.
    fun provideUserDao(database: AppDatabase): UserDao {
        // Returns the UserDao instance that is part of our AppDatabase.
        return database.userDao()
    }

    // Marks this function as a provider for the CareRepository interface.
    @Provides
    // Ensures only a single instance of the repository is created for the entire app.
    @Singleton
    // Defines how to provide an implementation when a CareRepository is requested.
    fun provideCareRepository(userDao: UserDao): CareRepository {
        // Returns a new instance of CareRepositoryImpl, satisfying the interface dependency.
        return CareRepositoryImpl(userDao)
    }
}

