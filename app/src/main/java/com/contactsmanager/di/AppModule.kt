package com.contactsmanager.di

import android.content.Context
import androidx.room.Room
import com.contactsmanager.data.MainRepository
import com.contactsmanager.data.local.AppDatabase
import com.contactsmanager.data.local.ContactsDao
import com.contactsmanager.utils.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun providesContext(@ApplicationContext applicationContext: Context): Context {
        return applicationContext
    }

    @Singleton
    @Provides
    fun provideDatabase(appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providesContactsDao(db: AppDatabase): ContactsDao = db.contactsDao()

    @Singleton
    @Provides
    fun providesContactsRepo(local: ContactsDao) = MainRepository(local)
}