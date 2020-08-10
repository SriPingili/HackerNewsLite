package com.android.hackernewslite.play.util

class Constants {
    companion object {
        const val BASE_URL = "https://hacker-news.firebaseio.com"
        const val DELAY_IN_MILLI_SECONDS = 500L
        const val QUERY_PAGE_SIZE = 20


        const val HOT_STORY_TYPE = "hot_story"
        const val NEW_STORY_TYPE = "new_story"
        const val JOB_STORY_TYPE = "job_story"
        const val QUERY_SIZE_LIMIT = 3
        const val SWIPE_TO_REFRESH_DELAY = 2000L


        const val PREFS_KEY = "shared_prefs_key"
        const val SAVED_ITEMS_ID = "saved_items"
        const val SAVED_ITEM_ID = "saved_item_id"
        const val CUSTOM_TABS_PREFS = "custom_tabs_prefs"

        const val EMAIL = "harikillz@gmail.com"
        const val PASSWORD = "9885887571"

        var shouldOpenInCustomTabs = false
    }
}