package com.owsky.sushihubredone.di

import android.content.Context
import android.content.SharedPreferences
import com.owsky.sushihubredone.prefsIdentifier
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
    fun providePrefs(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences(prefsIdentifier, Context.MODE_PRIVATE)
}