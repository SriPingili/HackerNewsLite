package com.androideveloper.thenewsapp.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.adapter.HackerFeedAdapter
import com.androideveloper.hackernewsfeed.play.extensions.initialize
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.SWIPE_TO_REFRESH_DELAY
import com.androideveloper.hackernewsfeed.play.util.Resource
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.fragment_latest_news.swipeRefresh
import kotlinx.android.synthetic.main.fragment_top_news.*
import kotlinx.android.synthetic.main.fragment_top_news.paginationProgressBar


class LatestNewsFragment : Fragment(R.layout.fragment_latest_news), SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var latestHackerFeedAdapter: HackerFeedAdapter
    val TAG = "LatestNewsFragment"
    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        setHasOptionsMenu(true)
        swipeRefresh.initialize(this)

        latestHackerFeedAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article_arg", it)//this needs to be same as in news_nav_graph.xml
            }

            findNavController().navigate(
                R.id.action_latestNewsFragment_to_articleFragment,
                bundle
            )
        }

        latestHackerFeedAdapter.setOnImageClickListener {
            //todo also save id and isenabled (if true) to db, everytime app starts, check in db for true and update flags accrdingly
            if (it?.isImageSaved!!) {
                viewModel.saveStory(it)
            } else {
                viewModel.deleteStory(it)
            }
            Toast.makeText(context, "clicked ${it.isImageSaved}", Toast.LENGTH_SHORT).show()
        }

        viewModel.newStoriesLiveData.observe(
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

        viewModel.newStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                        latestHackerFeedAdapter.submitList(it.toList())
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.v(TAG, " error = $message")
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        searchMenuItem = menu.findItem(R.id.menu_search)
        setSearchMenuItemVisibility(true)
        searchView = searchMenuItem?.actionView as SearchView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        context?.let {
            searchView?.setOnQueryTextListener(this)
            searchView?.setIconifiedByDefault(true)
            searchView?.queryHint = getString(R.string.search)
            searchView?.inputType = InputType.TYPE_CLASS_TEXT
            searchView?.imeOptions = EditorInfo.IME_ACTION_SEARCH
            searchView?.maxWidth = resources.displayMetrics.widthPixels
            val manager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView?.setSearchableInfo(manager.getSearchableInfo(activity?.componentName))
            val searchFrame =
                searchView?.findViewById(androidx.appcompat.R.id.search_edit_frame) as LinearLayout
            (searchFrame.layoutParams as LinearLayout.LayoutParams).marginStart = 0
        }
    }

    /**
     * Sets the visibility of the search menu item.
     *
     * @param shouldDisplay flag indicating whether the search menu item should be displayed.
     */
    fun setSearchMenuItemVisibility(shouldDisplay: Boolean) {
        searchMenuItem?.isVisible = shouldDisplay
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        latestHackerFeedAdapter.filter(if (newText.length >= QUERY_SIZE_LIMIT) newText else null)

        return true
    }

    override fun onRefresh() {
        viewModel.getTopStores()

        val handler = Handler()
        handler.postDelayed({ //hide the loading screen after 3 secs
            swipeRefresh.isRefreshing = false
        }, SWIPE_TO_REFRESH_DELAY)
    }


}