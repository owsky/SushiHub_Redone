package com.owsky.sushihubredone.di

import android.content.SharedPreferences
import com.owsky.sushihubredone.data.OrderRepository
import com.owsky.sushihubredone.data.TableRepository
import com.owsky.sushihubredone.data.dao.OrderDao
import com.owsky.sushihubredone.data.dao.TableDao
import com.owsky.sushihubredone.util.Connectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @RepoWithConnect
    @Provides
    fun provideOrderRepository(localDataSource: OrderDao, prefs: SharedPreferences, connectivity: Connectivity) =
        OrderRepository(localDataSource, prefs, connectivity)

    @RepoSansConnect
    @Provides
    fun provideOrderRepositorySansConn(localDataSource: OrderDao, prefs: SharedPreferences) =
        OrderRepository(localDataSource, prefs, null)

    @Provides
    fun provideTableRepository(tableDao: TableDao, prefs: SharedPreferences) = TableRepository(tableDao, prefs)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RepoWithConnect

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RepoSansConnect