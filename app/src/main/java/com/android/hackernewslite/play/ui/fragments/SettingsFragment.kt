package com.android.hackernewslite.play.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
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
import com.android.hackernewslite.play.ui.LicenseActivity
import com.android.hackernewslite.play.ui.PrivacyPolicyActivity
import com.android.hackernewslite.play.util.AppUtil
import com.android.hackernewslite.play.util.Constants
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
        aboutCategory.key = Constants.ABOUT_CATEGORY
        aboutCategory.title = getString(R.string.about)
        aboutCategory.isIconSpaceReserved = false

        //Version
        val versionPreference = Preference(context)
        versionPreference.key = Constants.APP_VERSION
        versionPreference.title = getString(R.string.version)
        versionPreference.summary = BuildConfig.VERSION_NAME
        versionPreference.isIconSpaceReserved = false

        //License Agreement
        val licensePreference = Preference(context)
        licensePreference.key = Constants.APP_LICENSE
        licensePreference.title = getString(R.string.license_agreement)
        licensePreference.isIconSpaceReserved = false
        licensePreference.setOnPreferenceClickListener {
            startActivity(Intent(context, LicenseActivity::class.java))
            true
        }

        //Privacy Policy
        val privacyPolicyPreference = Preference(context)
        privacyPolicyPreference.key = Constants.APP_PRIVACY_POLICY
        privacyPolicyPreference.title = getString(R.string.privacy_policy_agreement)
        privacyPolicyPreference.isIconSpaceReserved = false
        privacyPolicyPreference.setOnPreferenceClickListener {
            startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            true
        }

        screen.addPreference(aboutCategory)
        aboutCategory.addPreference(versionPreference)
        aboutCategory.addPreference(licensePreference)
        aboutCategory.addPreference(privacyPolicyPreference)

        //display
        val displayCategory = PreferenceCategory(context)
        displayCategory.key = Constants.DISPLAY_CATEGORY
        displayCategory.title = getString(R.string.display)
        displayCategory.isIconSpaceReserved = false
        screen.addPreference(displayCategory)


        //use custom tabs
        val customTabPreference = SwitchPreference(context)
        customTabPreference.key = Constants.CUSTOM_TAB
        customTabPreference.title = getString(R.string.use_custom_tabs)
        customTabPreference.summary = getString(R.string.open_in_chrome)
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
        helpCategory.key = Constants.HELP_CATEGORY
        helpCategory.title = getString(R.string.help)
        screen.addPreference(helpCategory)
        helpCategory.isIconSpaceReserved = false

        //Rate  the app
        val reviewPreference = Preference(context)
        reviewPreference.key = Constants.REVIEW
        reviewPreference.title = getString(R.string.rate_the_app)
        reviewPreference.isIconSpaceReserved = false
        reviewPreference.setOnPreferenceClickListener {
            try {
                AppUtil.startPlayStoreIntent(
                    String.format(getString(R.string.market_url), BuildConfig.APPLICATION_ID),
                    context
                )
            } catch (anfe: ActivityNotFoundException) {
                AppUtil.startPlayStoreIntent(
                    String.format(getString(R.string.play_store_url), BuildConfig.APPLICATION_ID),
                    context
                )
            }

            true
        }

        //Share the app
        val sharePreference = Preference(context)
        sharePreference.key = Constants.SHARE
        sharePreference.title = getString(R.string.share_app)
        sharePreference.isIconSpaceReserved = false
        sharePreference.setOnPreferenceClickListener {
            AppUtil.startShareIntent(
                getString(R.string.app_share_url) + BuildConfig.APPLICATION_ID,
                context
            )
            true
        }

        //Submit Feedback
        val feedbackPreference = Preference(context) //todo strings.xml
        feedbackPreference.key = Constants.FEEDBACK
        feedbackPreference.title = getString(R.string.send_feedback)
        feedbackPreference.summary = getString(R.string.feedback_summary)
        feedbackPreference.isIconSpaceReserved = false
        feedbackPreference.setOnPreferenceClickListener {
            val fm: FragmentManager? = activity?.supportFragmentManager
            if (fm != null) {
                FeedbackFragment().show(fm, Constants.FRAGMENT_TAG)
            }

            true
        }

        helpCategory.addPreference(feedbackPreference)
        helpCategory.addPreference(reviewPreference)
        helpCategory.addPreference(sharePreference)

        preferenceScreen = screen
    }
}