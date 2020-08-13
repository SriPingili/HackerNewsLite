package com.androideveloper.hackernewsfeed.play.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource

/*
 This is tthe launches activity that hosts all the
 Fragments (Article, JobNews,LatestNews,SavedNews and TopNews fragments)
* */
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


        /*
        TODO move the below observers to their respective fragments
        * */
        viewModel.topStoriesLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }

        })

        viewModel.topStoryLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }
        })


        viewModel.newStoriesLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }

        })

        viewModel.newStoryLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                        if (it.type.equals("story")) {
                        }
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.jobStoriesLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }

        })

        viewModel.jobStoryLiveData.observe(this, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }
}