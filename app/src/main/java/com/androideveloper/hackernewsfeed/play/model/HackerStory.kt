package com.androideveloper.hackernewsfeed.play.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/*
data class representing Hacker story from Hacker news api response and
also the room enity
* */
@Entity(tableName = "hacker_stories")
data class HackerStory(
    val by: String?,
    val descendants: Int?,
    @PrimaryKey
    val id: Int,
    val score: Int?,
    val time: Int?,
    val title: String?,
    val type: String?,
    val url: String?,
    val text: String?,
    val kids: List<Int>?,
    var isImageSaved: Boolean = false,
    var storyType: String? = null
) : Serializable