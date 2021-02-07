package com.owsky.sushihubredone.di

import android.app.Application
import android.content.SharedPreferences
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.dao.TableDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideOrderRepository(localDataSource: OrderDao, prefs: SharedPreferences, application: Application) =
        OrderRepository(localDataSource, prefs, application)

    @Provides
    fun provideTableRepository(tableDao: TableDao, prefs: SharedPreferences) = TableRepository(tableDao, prefs)
}