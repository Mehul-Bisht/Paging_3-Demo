package com.example.paging_3demo.db.remoteKeysDb

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<UnsplashPhotoEntity>)

    @Query("SELECT * FROM unsplash")
    fun reposByName(queryString: String): PagingSource<Int, UnsplashPhotoEntity>

    @Query("DELETE FROM unsplash")
    suspend fun clearRepos()
}
