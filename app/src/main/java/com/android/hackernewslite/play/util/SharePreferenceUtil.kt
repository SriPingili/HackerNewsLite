package com.android.hackernewslite.play.util

import android.content.Context
import android.content.SharedPreferences
import com.android.hackernewslite.play.model.HackerStory
import com.android.hackernewslite.play.util.Constants.Companion.HOT_STORY_RESPONSE_TYPE
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class SharePreferenceUtil {

    companion object {
        fun getGson(): Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE)
        }

        fun getSavedStatus(id: Int, context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(Constants.SAVED_ITEM_ID + id, false)
        }

        fun setSavedStatus(id: Int, status: Boolean, context: Context) {
            getSharedPreferences(context).edit()
                .putBoolean(Constants.SAVED_ITEM_ID + id, status).apply()
        }

        fun getCustomTabsPreferenceStatus(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(Constants.CUSTOM_TABS_PREFS, true)
        }

        fun setCustomTabsPreferenceStatus(status: Boolean, context: Context) {
            getSharedPreferences(context).edit()
                .putBoolean(Constants.CUSTOM_TABS_PREFS, status).apply()
        }

        fun saveTopHackerStoriesToSharedPres(stories: List<HackerStory>, context: Context) {
            val gson = getGson()
            val listType = object : TypeToken<List<HackerStory>>() {}.type

            getSharedPreferences(context).edit()
                .putString(HOT_STORY_RESPONSE_TYPE, gson.toJson(stories, listType)).apply()
        }

        fun getTopHackerStoriesToSharedPres(context: Context): ArrayList<HackerStory> {
            val gson = getGson()
            val listType = object : TypeToken<List<HackerStory>>() {}.type

            val jsonString = getSharedPreferences(context).getString(HOT_STORY_RESPONSE_TYPE, "")

            return if (jsonString!!.isEmpty()) {
                ArrayList()
            } else {
                gson.fromJson(jsonString, listType)
            }
        }

    }
}