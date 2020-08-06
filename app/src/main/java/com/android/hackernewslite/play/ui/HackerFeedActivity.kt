package com.android.hackernewslite.play.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_feed)

        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())

        val hackerFeedRepository = HackerFeedRepository(HackerStoryDatabase(this))
        viewModel =
            ViewModelProvider(this, HackerFeedViewModelProviderFactory(hackerFeedRepository)).get(
                HackerFeedViewModel::class.java
            )

//        viewModel.topStoriesLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        Log.v("zzzz", " size is " + it.size)
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//
//        })
//
//        viewModel.topStoryLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        if(!it.type.equals("story")) {
//                            Log.v("zzzz", " size is " + it.type)
//                        }
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//        })
//
//
//        viewModel.newStoriesLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        Log.v("zzzz", " size is " + it.size)
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//
//        })
//
//        viewModel.newStoryLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        if(it.type.equals("story")) {
//                            Log.v("zzzz", " size is " + it.type)
//                        }
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//        })
//
//        viewModel.jobStoriesLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        Log.v("zzzz", " size is " + it.size)
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//
//        })
//
//        viewModel.jobStoryLiveData.observe(this, Observer { resourceResponse ->
//            when (resourceResponse) {
//
//                is Resource.Success -> {
////                    hideProgressBar()
//                    resourceResponse.data?.let {
//                        if(!it.type.equals("story")) {
//                            Log.v("zzzz", " size is " + it.type)
//                        }
//                    }
//                }
//
//                is Resource.Error -> {
////                    hideProgressBar()
//                    resourceResponse.message?.let { message ->
//                        Log.v("zzzz", " error = $message")
//                    }
//                }
//                is Resource.Loading -> {
////                    showProgressBar()
//                }
//            }
//        })
//    }
    }

}