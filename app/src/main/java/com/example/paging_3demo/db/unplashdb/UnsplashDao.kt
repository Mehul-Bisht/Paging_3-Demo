package com.example.paging_3demo.db.unplashdb

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UnsplashDao {

    @Insert
    fun insertPhoto(unsplashPhotoEntity: UnsplashPhotoEntity)

    @Insert
    fun insertPhotos(photos: List<UnsplashPhotoEntity>)

    @Delete
    fun deletePhoto(unsplashPhotoEntity: UnsplashPhotoEntity)

    @Query("DELETE FROM unsplash")
    fun deleteAllPhotos()

    @Query("SELECT * FROM unsplash")
    fun getPhotos(): PagingSource<Int, UnsplashPhotoEntity>
}