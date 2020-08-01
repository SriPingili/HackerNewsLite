package com.androideveloper.hackernewsfeed.play.model

data class HackerStory(
    val by: String?,
    val descendants: Int?,
    val id: Int,
    val score: Int?,
    val time: Int?,
    val title: String?,
    val type: String?,
    val url: String?,
    val text: String?,
    val kids: List<Int>?
)