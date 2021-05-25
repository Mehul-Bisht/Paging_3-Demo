package com.example.paging_3demo.network

import androidx.paging.*
import com.example.paging_3demo.db.unplashdb.UnsplashDatabase
import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity
import com.example.paging_3demo.network.api.UnsplashApi
import com.example.paging_3demo.network.api.data.UnsplashMediator
import com.example.paging_3demo.network.api.data.UnsplashPagingSource
import com.example.paging_3demo.network.api.data.UnsplashPhoto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val UnsplashDatabase: UnsplashDatabase
) {

    @ExperimentalPagingApi
    fun getPhotos(): Flow<PagingData<UnsplashPhotoEntity>> {

        val pagingSourceFactory = { UnsplashDatabase.getDao().getPhotos() }

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = UnsplashMediator(
                UnsplashDatabase,
                unsplashApi
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}