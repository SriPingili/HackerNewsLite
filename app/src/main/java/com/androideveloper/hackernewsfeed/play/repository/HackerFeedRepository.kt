package com.androideveloper.hackernewsfeed.play.repository

import com.androideveloper.hackernewsfeed.play.api.RetrofitInstance

class HackerFeedRepository() {

    suspend fun getTopStories() =
        RetrofitInstance.api.getTopStories()

    suspend fun fetchStoryById(id:Int) = RetrofitInstance.api.getStoryById(id)
}