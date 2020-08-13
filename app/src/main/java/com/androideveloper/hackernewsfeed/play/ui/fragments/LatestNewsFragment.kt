package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource
import com.androideveloper.thenewsapp.adapter.HackerFeedAdapter
import kotlinx.android.synthetic.main.fragment_latest_news.*

/*
* This Fragment is responsible for displaying the latest news from the Hacker News api
* */
class LatestNewsFragment : Fragment(R.layout.fragment_latest_news) {
    lateinit var viewModel: HackerFeedViewModel
    val TAG = "LatestNewsFragment"
    lateinit var hackerFeedAdapter: HackerFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

        hackerFeedAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article_arg", it)//this needs to be same as in news_nav_graph.xml
            }

            findNavController().navigate(
                R.id.action_latestNewsFragment_to_articleFragment,
                bundle
            )
        }

        hackerFeedAdapter.setOnImageClickListener {
            //todo also save id and isenabled (if true) to db, everytime app starts, check in db for true and update flags accrdingly
            if (it?.isImageSaved!!) {
                viewModel.saveStory(it)
            } else {
                viewModel.deleteStory(it)
            }
            Toast.makeText(context,"clicked ${it.isImageSaved}", Toast.LENGTH_SHORT).show()
        }

        viewModel.newStoriesLiveData.observe(
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

        viewModel.newStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {
                is Resource.Success -> {
                    resourceResponse.data?.let {
                        hackerFeedAdapter.differ.submitList(it.toList())
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.v(TAG, "An error occured: $message")
                    }
                }

                is Resource.Loading -> {
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
        hackerFeedAdapter = HackerFeedAdapter()
        rvLatestNews.apply {
            adapter = hackerFeedAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}