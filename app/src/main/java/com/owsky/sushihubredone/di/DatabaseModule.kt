package com.owsky.sushihubredone.di

import android.content.Context
import com.owsky.sushihubredone.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideOrderDao(db: AppDatabase) = db.orderDao()

    @Provides
    fun provideTableDao(db: AppDatabase) = db.tableDao()
}