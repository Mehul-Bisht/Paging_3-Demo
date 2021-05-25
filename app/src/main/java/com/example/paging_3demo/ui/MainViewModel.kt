package com.example.paging_3demo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity
import com.example.paging_3demo.network.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    @ExperimentalPagingApi
    fun getData(): Flow<PagingData<UnsplashPhotoEntity>> {

        return repository.getPhotos().cachedIn(viewModelScope)
    }
}