package com.owsky.sushihubredone.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.dao.TableDao
import com.owsky.sushihubredone.util.Connectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideOrderRepository(
        @ApplicationContext context: Context,
        orderDao: OrderDao,
        prefs: SharedPreferences,
        connectivity: Connectivity
    ): OrderRepository {
        return OrderRepository(context as Application, orderDao, prefs, connectivity)
    }

    @Provides
    fun provideTableRepository(tableDao: TableDao, prefs: SharedPreferences) = TableRepository(tableDao, prefs)
}