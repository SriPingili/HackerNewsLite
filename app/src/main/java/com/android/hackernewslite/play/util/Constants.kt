package com.android.hackernewslite.play.util

class Constants {
    companion object {
        enum class AppFlow {
            FIRST_TIME, IS_UPDATING_IN_BACKGROUND
        }

        enum class ResponseCall {
            IN_PROGRESS, COMPLETED_SUCCESSFULLY
        }

        const val BASE_URL = "https://hacker-news.firebaseio.com"

        const val HOT_STORY_TYPE = "hot_story"
        const val NEW_STORY_TYPE = "new_story"
        const val JOB_STORY_TYPE = "job_story"
        const val QUERY_SIZE_LIMIT = 3
        const val SWIPE_TO_REFRESH_DELAY = 2000L
        const val SPLASH_SCREEN_DELAY = 1000L

        const val PREFS_KEY = "shared_prefs_key"
        const val SAVED_ITEMS_ID = "saved_items"
        const val SAVED_ITEM_ID = "saved_item_id"
        const val CUSTOM_TABS_PREFS = "custom_tabs_prefs"

        //email constants
        const val FROM_EMAIL = "harikillz@gmail.com"
        const val PASSWORD = "9885887571"
        const val TO_EMAIL = "srijith.pingili@gmail.com"

        const val IS_FROM_SPLASH_SCREEN = "isFromSplashScreen"
        const val ARTICLE_ARG = "article_arg"
        const val ERROR_MESSAGE = "An error occured: %s"

        const val HOT_STORY_RESPONSE_TYPE = "hot_story_response"
        const val NEW_STORY_RESPONSE_TYPE = "new_story_response"
        const val JOB_STORY_RESPONSE_TYPE = "job_story_response"

        //Settings Constants
        const val ABOUT_CATEGORY = "about_category"
        const val APP_VERSION = "app_version"
        const val APP_LICENSE = "app_license"
        const val DISPLAY_CATEGORY = "display_category"
        const val CUSTOM_TAB = "custom_tab"
        const val HELP_CATEGORY = "help_category"
        const val REVIEW = "review"
        const val SHARE = "share"
        const val FEEDBACK = "feedback"
        const val FRAGMENT_TAG = "fragment_submit_feedback"
    }
}