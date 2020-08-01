package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Resource
import com.androideveloper.thenewsapp.adapter.HackerFeedAdapter
import kotlinx.android.synthetic.main.fragment_top_news.*


class TopNewsFragment : Fragment(R.layout.fragment_top_news) {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    val TAG = "BreakingNewsFragment"

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

        viewModel.topStoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { resourceResponse -> //Resource<NewsResponse
                when (resourceResponse) {
                    is Resource.Success -> {
                        hideProgressBar()
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

        viewModel.topStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {hackerStory->


//                            val list = ArrayList<HackerStory>()
//                            list.addAll(hackerFeedAdapter.differ.currentList)
//                            list.add(hackerStory)
////                            val list =hackerFeedAdapter.differ.currentList.add(hackerStory)
//
//                            Log.v("zzzzzzzzzz","size = ${hackerStory.by}")
                            hackerFeedAdapter.differ.submitList(hackerStory.toList())
//                            hackerFeedAdapter.differ.

                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.v("zzzz", " error = $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
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
        hackerFeedAdapter = HackerFeedAdapter()
        rvTopNews.apply {
            adapter = hackerFeedAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }


}