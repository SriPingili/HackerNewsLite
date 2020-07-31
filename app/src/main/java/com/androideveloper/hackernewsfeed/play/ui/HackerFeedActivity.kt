package com.androideveloper.hackernewsfeed.play.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource

class HackerFeedActivity : AppCompatActivity() {
    lateinit var viewModel: HackerFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_feed)


        val hackerFeedRepository = HackerFeedRepository()
        viewModel =
            ViewModelProvider(this, HackerFeedViewModelProviderFactory(hackerFeedRepository)).get(
                HackerFeedViewModel::class.java
            )


        viewModel.topStoriesLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
//                    hideProgressBar()
                    resourceResponse.data?.let {
                        Log.v("zzzz", " size is " + it.size)
                    }
                }

                is Resource.Error -> {
//                    hideProgressBar()
                    resourceResponse.message?.let { message ->
                        Log.v("zzzz", " error = $message")
                    }
                }
                is Resource.Loading -> {
//                    showProgressBar()
                }
            }

        })

        viewModel.hackerStoryLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
//                    hideProgressBar()
                    resourceResponse.data?.let {
                        Log.v("zzzz", " size is " + it.title)
                    }
                }

                is Resource.Error -> {
//                    hideProgressBar()
                    resourceResponse.message?.let { message ->
                        Log.v("zzzz", " error = $message")
                    }
                }
                is Resource.Loading -> {
//                    showProgressBar()
                }
            }
        })
    }


}