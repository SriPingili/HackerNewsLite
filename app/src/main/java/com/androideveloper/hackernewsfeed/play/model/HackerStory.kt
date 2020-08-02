package com.androideveloper.hackernewsfeed.play.model

/*
data class representing Hacker story from Hacker news api response
* */
import java.io.Serializable

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
    val kids: List<Int>?,
    var isImageSaved: Boolean = false
) : Serializable