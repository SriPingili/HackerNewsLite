package com.android.hackernewslite.play.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/*
data class representing Hacker story from Hacker news api response and
also the room enity
* */
@Parcelize
@Entity(tableName = "hacker_stories")
data class HackerStory(
    @Expose val by: String?,
    @Expose val descendants: Int?,
    @PrimaryKey
    @Expose val id: Int,
    @Expose val score: Int?,
    @Expose val time: Long?,
    @Expose val title: String?,
    @Expose val type: String?,
    @Expose val url: String?,
    @Expose val text: String?,
    @Expose val kids: List<Int>?,
    @Expose var isImageSaved: Boolean = false,
    @Expose var storyType: String? = null
) : Serializable, Parcelable, Comparable<HackerStory> {
    constructor() : this("", 0, 0, 0, 0, "", "", "", "", null)

    override fun describeContents() = 0

    override fun compareTo(other: HackerStory): Int {
        return id.compareTo(other.id)
    }
}