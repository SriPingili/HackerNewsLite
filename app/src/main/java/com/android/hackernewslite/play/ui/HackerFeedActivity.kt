package com.android.hackernewslite.play.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.db.HackerStoryDatabase
import com.android.hackernewslite.play.extensions.relativeDateFormat
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import kotlinx.android.synthetic.main.activity_hacker_feed.*
import java.text.SimpleDateFormat
import java.util.*

class HackerFeedActivity : AppCompatActivity() {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_feed)
        actionBar = supportActionBar!!

        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())

        val hackerFeedRepository = HackerFeedRepository(HackerStoryDatabase(this))
        viewModel =
            ViewModelProvider(this, HackerFeedViewModelProviderFactory(hackerFeedRepository)).get(
                HackerFeedViewModel::class.java
            )
    }

    fun hideBotomNav(){
        bottomNavigationView?.visibility = View.GONE
        actionBar.hide()



    }


    fun ShowBotomNav(){
        bottomNavigationView?.visibility = View.VISIBLE
        actionBar.show()
    }

}