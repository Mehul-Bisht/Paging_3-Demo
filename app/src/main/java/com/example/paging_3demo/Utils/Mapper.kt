package com.example.paging_3demo.Utils

import com.example.paging_3demo.db.unplashdb.UnsplashPhotoEntity
import com.example.paging_3demo.network.api.data.UnsplashPhoto

fun UnsplashPhoto.toUnsplashPhotoEntity(): UnsplashPhotoEntity {

    return UnsplashPhotoEntity(

        id = this.id,
        description = this.description,
        regularUrl = this.urls.regular
    )
}