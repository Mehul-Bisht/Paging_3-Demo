package com.example.paging_3demo.di

import android.content.Context
import androidx.room.Room
import com.example.paging_3demo.db.unplashdb.UnsplashDao
import com.example.paging_3demo.db.unplashdb.UnsplashDatabase
import com.example.paging_3demo.network.ApiRepository
import com.example.paging_3demo.network.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(UnsplashApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi =
        retrofit.create(UnsplashApi::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): UnsplashDatabase {
        return Room.databaseBuilder(
            context,
            UnsplashDatabase::class.java,
            "unsplash_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideApiRepository(
        unsplashApi: UnsplashApi,
        unsplashDatabase: UnsplashDatabase
    ): ApiRepository {

        return ApiRepository(unsplashApi,unsplashDatabase)
    }
}