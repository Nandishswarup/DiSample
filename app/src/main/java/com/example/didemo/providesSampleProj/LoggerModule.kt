package com.example.didemo.providesSampleProj

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule{

    @Provides
    @Singleton
    fun provideLogger():InterfaceLogger{

        return ConditionalLogger(BuildConfig.DEBUG)
    }
}