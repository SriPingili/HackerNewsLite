package com.android.hackernewslite.play.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.db.HackerStoryDatabase
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import kotlinx.android.synthetic.main.activity_hacker_feed.*

/*
 The launcher activity launches activity that hosts all the
 Fragments (Article, JobNews,LatestNews,SavedNews and TopNews fragments)
* */
class HackerFeedActivity : AppCompatActivity() {
    lateinit var viewModel: HackerFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_feed)

        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())

        val hackerFeedRepository = HackerFeedRepository(HackerStoryDatabase(this))
        viewModel =
            ViewModelProvider(this, HackerFeedViewModelProviderFactory(hackerFeedRepository)).get(
                HackerFeedViewModel::class.java
            )
    }
}