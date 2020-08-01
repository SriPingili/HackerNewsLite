package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource
import com.androideveloper.thenewsapp.adapter.HackerFeedAdapter
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.fragment_top_news.paginationProgressBar


class LatestNewsFragment : Fragment(R.layout.fragment_latest_news) {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var latestHackerFeedAdapter: HackerFeedAdapter
    val TAG = "LatestNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

//        newsAdapter.setOnItemClickListener {
//            val bundle = Bundle().apply {
//                putSerializable("article_arg", it)//this needs to be same as in news_nav_graph.xml
//            }
//
//            findNavController().navigate(
//                R.id.action_breakingNewsFragment_to_articleFragment,
//                bundle
//            )
//        }

        viewModel.newStoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { resourceResponse -> //Resource<NewsResponse
                when (resourceResponse) {
                    is Resource.Success -> {
                        hideProgressBar()
                        resourceResponse.data?.let { newsResponse ->
//                            newsAdapter.differ.submitList(newsResponse.articles.toList())
                        }
                    }

                    is Resource.Error -> {
                        hideProgressBar()
                        resourceResponse.message?.let { message ->
                            Toast.makeText(
                                activity,
                                "An error occured: $message",
                                Toast.LENGTH_LONG
                            ).show()
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
//                        if (it.type.equals("story")) {
//                            val list = ArrayList<HackerStory>()
//                            list.addAll(latestHackerFeedAdapter.differ.currentList)
//                            list.add(it)
////                            val list =hackerFeedAdapter.differ.currentList.add(hackerStory)
//
////                            Log.v("zzzzzzzzzz","size = ${it.by}")
                            latestHackerFeedAdapter.differ.submitList(it.toList())
                        //}
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.v("zzzz", " error = $message")
                    }
                }
                is Resource.Loading -> {
                }
            }
        })
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    fun setUpRecyclerView() {
        latestHackerFeedAdapter = HackerFeedAdapter()
        rvLatestNews.apply {
            adapter = latestHackerFeedAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}