package com.androideveloper.hackernewsfeed.play.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource
import kotlinx.android.synthetic.main.activity_hacker_feed.*

class HackerFeedActivity : AppCompatActivity() {
    lateinit var viewModel: HackerFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_feed)

        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())

        val hackerFeedRepository = HackerFeedRepository()
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