package com.android.hackernewslite.play.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.android.hackernewslite.play.BuildConfig
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.util.SharePreferenceUtil

/*
* Fragment responsible for displaying settinga
* */
class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setBackgroundColor(resources.getColor(R.color.backgroundColor, null))
        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        //About
        val aboutCategory = PreferenceCategory(context)
        aboutCategory.key = "about_category"
        aboutCategory.title = "About"
        aboutCategory.isIconSpaceReserved = false

        //Version
        val versionPreference = Preference(context)
        versionPreference.key = "app_version"
        versionPreference.title = "Version"
        versionPreference.summary = BuildConfig.VERSION_NAME
        versionPreference.isIconSpaceReserved = false

        //License Agreement
        val licensePreference = Preference(context)
        licensePreference.key = "app_license"
        licensePreference.title = "License Agreement"
        licensePreference.isIconSpaceReserved = false

        screen.addPreference(aboutCategory)
        aboutCategory.addPreference(versionPreference)
        aboutCategory.addPreference(licensePreference)

        //display
        val displayCategory = PreferenceCategory(context)
        displayCategory.key = "display_category"
        displayCategory.title = "Display"
        displayCategory.isIconSpaceReserved = false
        screen.addPreference(displayCategory)


        //use custom tabs
        val customTabPreference = SwitchPreference(context)
        customTabPreference.key = "custom_tab"
        customTabPreference.title = "Use Chrome Tabs"
        customTabPreference.summary = "Open links in Chrome tabs instead of default browser"
        customTabPreference.isIconSpaceReserved = false
        customTabPreference.setDefaultValue(true)
        customTabPreference.onPreferenceChangeListener =
            object : Preference.OnPreferenceChangeListener {
                override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
                    SharePreferenceUtil.setCustomTabsPreferenceStatus(newValue as Boolean, context)
                    return true
                }
            }

        displayCategory.addPreference(customTabPreference)

        //Help
        val helpCategory = PreferenceCategory(context)
        helpCategory.key = "help"
        helpCategory.title = "Help"
        screen.addPreference(helpCategory)
        helpCategory.isIconSpaceReserved = false

        //Rate  the app
        val reviewPreference = Preference(context)
        reviewPreference.key = "review"
        reviewPreference.title = "Rate the app"
        reviewPreference.isIconSpaceReserved = false
        reviewPreference.setOnPreferenceClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                    )
                )
            }

            true
        }

        //Share the app
        val sharePreference = Preference(context)
        sharePreference.key = "share"
        sharePreference.title = "Share the app"
        sharePreference.isIconSpaceReserved = false
        sharePreference.setOnPreferenceClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID //todo strings.xml
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
            true
        }

        //Submit Feedback
        val feedbackPreference = Preference(context) //todo strings.xml
        feedbackPreference.key = "feedback"
        feedbackPreference.title = "Send feedback"
        feedbackPreference.summary = "Report technical issues or suggest new features"
        feedbackPreference.isIconSpaceReserved = false
        feedbackPreference.setOnPreferenceClickListener {
            val fm: FragmentManager? = activity?.supportFragmentManager
            if (fm != null) {
                FeedbackFragment().show(fm, "fragment_submit_feedback")
            }

            true
        }

        helpCategory.addPreference(feedbackPreference)
        helpCategory.addPreference(reviewPreference)
        helpCategory.addPreference(sharePreference)

        preferenceScreen = screen
    }
}