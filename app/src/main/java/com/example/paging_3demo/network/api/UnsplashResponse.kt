package com.example.paging_3demo.network.api

import com.example.paging_3demo.network.api.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)