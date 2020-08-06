package com.android.hackernewslite.play.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "hacker_stories")
data class HackerStory(
    val by: String?,
    val descendants: Int?,
    @PrimaryKey
    val id: Int,
    val score: Int?,
    val time: Long?,
    val title: String?,
    val type: String?,
    val url: String?,
    val text: String?,
    val kids: List<Int>?,
    var isImageSaved: Boolean = false,
    var storyType: String? =null
) : Serializable