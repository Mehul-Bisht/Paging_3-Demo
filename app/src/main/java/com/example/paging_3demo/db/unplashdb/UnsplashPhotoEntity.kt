package com.example.paging_3demo.db.unplashdb

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "unsplash")
@Parcelize
data class UnsplashPhotoEntity(
    @PrimaryKey
    val id: String,
    val description: String?,
    val regularUrl: String?
): Parcelable{

}
