package com.example.paging_3demo.db.unplashdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paging_3demo.db.remoteKeysDb.RemoteKeys
import com.example.paging_3demo.db.remoteKeysDb.RemoteKeysDao
import com.example.paging_3demo.network.api.data.UnsplashPhoto

@Database(
    entities = [UnsplashPhotoEntity::class, RemoteKeys::class],
    exportSchema = false,
    version = 1
)
abstract class UnsplashDatabase(): RoomDatabase() {

    abstract fun getDao(): UnsplashDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao
}
