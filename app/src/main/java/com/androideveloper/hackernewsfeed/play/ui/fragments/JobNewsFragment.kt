package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource
import kotlinx.android.synthetic.main.fragment_job_news.*

/*
* This Fragment is responsible for displaying the latest Job posts from the Hacker News api
* */
class JobNewsFragment : Fragment(R.layout.fragment_job_news) {
    lateinit var viewModel: HackerFeedViewModel
    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

        viewModel.jobStoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { resourceResponse -> //Resource<NewsResponse
                when (resourceResponse) {
                    is Resource.Success -> {
                        hideProgressBar()
                        resourceResponse.data?.let { newsResponse ->
                            //TODO implement this
                        }
                    }

                    is Resource.Error -> {
                        hideProgressBar()
                        resourceResponse.message?.let { message ->
                            Log.v(TAG, "An error occured: $message")
                        }
                    }

                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }

            })
    }

    /*
    helper method to hide the progress bar
    */
    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    /*
    helper method to show the progress bar
    */
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    /*
    helper method that sets up the recycler view
    * */
    fun setUpRecyclerView() {
        rvJobNews.apply {
            layoutManager = LinearLayoutManager(activity)

        }
    }
}