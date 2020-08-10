package com.android.hackernewslite.play.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SharePreferenceUtil {

    companion object {
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
            return getSharedPreferences(context).getBoolean(Constants.CUSTOM_TABS_PREFS, false)
        }

        fun setCustomTabsPreferenceStatus(status: Boolean, context: Context) {
            getSharedPreferences(context).edit()
                .putBoolean(Constants.CUSTOM_TABS_PREFS, status).apply()
        }


        /**
         * returns the list of saved item id's
         *
         * @param context - the context of the calling activity
         * @return the list of saved items
         */
        fun getSavedStoryIds(context: Context): ArrayList<Int> {
            val gson = Gson()
            val json: String? =
                getSharedPreferences(context).getString(Constants.SAVED_ITEMS_ID, "")
            val type: Type = object : TypeToken<ArrayList<Int>>() {}.type
            return if (json!!.isEmpty()) {
                ArrayList()
            } else gson.fromJson(json, type)
        }

        fun saveStoryId(requestCode: Int, context: Context) {
            val requestCodes = getSavedStoryIds(context)
            if (!requestCodes.contains(requestCode)) {
                requestCodes.add(requestCode)
            }
            val gson = Gson()
            val json = gson.toJson(requestCodes)
            getSharedPreferences(context).edit().putString(Constants.SAVED_ITEMS_ID, json).apply()
        }

        fun removeStoryId(requestCode: Int, context: Context) {
            val requestCodes = getSavedStoryIds(context)
            if (requestCodes.contains(requestCode)) {
                requestCodes.remove(requestCode)
            }
            val gson = Gson()
            val json = gson.toJson(requestCodes)
            getSharedPreferences(context).edit().putString(Constants.SAVED_ITEMS_ID, json).apply()
        }

    }
}