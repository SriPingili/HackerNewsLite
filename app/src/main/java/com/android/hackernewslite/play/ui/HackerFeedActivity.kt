package com.android.hackernewslite.play.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.db.HackerStoryDatabase
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.ui.viewmodel.factory.HackerFeedViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_hacker_feed.*

/*
 The launcher activity launches activity that hosts all the
 Fragments (Article, JobNews,LatestNews,SavedNews and TopNews fragments)
* */
class HackerFeedActivity : AppCompatActivity() {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        setContentView(R.layout.activity_hacker_feed)
        actionBar = supportActionBar!!

        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())

        val hackerFeedRepository = HackerFeedRepository(HackerStoryDatabase(this))
        viewModel =
            ViewModelProvider(this,
                HackerFeedViewModelProviderFactory(
                    hackerFeedRepository
                )
            ).get(
                HackerFeedViewModel::class.java
            )
    }

    /*
    * helper method that hides bottom nav and navigation
    * */
    fun hideBottomNavAndActionBar() {
        bottomNavigationView?.visibility = View.GONE
        actionBar.hide()
    }

    /*
    * helper method that shows bottom nav and navigation
    * */
    fun showBottomNavAndActionBar() {
        bottomNavigationView?.visibility = View.VISIBLE
        actionBar.show()
    }
}