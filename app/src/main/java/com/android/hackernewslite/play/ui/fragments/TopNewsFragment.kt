package com.android.thenewsapp.ui.fragments

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
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.adapter.HackerFeedAdapter
import com.android.hackernewslite.play.extensions.initialize
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import com.android.hackernewslite.play.util.Constants.Companion.SWIPE_TO_REFRESH_DELAY
import com.android.hackernewslite.play.util.Resource
import com.android.hackernewslite.play.util.SharePreferenceUtil
import kotlinx.android.synthetic.main.fragment_top_news.*


class TopNewsFragment : Fragment(R.layout.fragment_top_news), SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    val TAG = "BreakingNewsFragment"

    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        (activity as HackerFeedActivity).ShowBotomNav()

        setHasOptionsMenu(true)

        swipeRefresh.initialize(this)

        hackerFeedAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article_arg", it)//this needs to be same as in news_nav_graph.xml
            }

            findNavController().navigate(
                R.id.action_topNewsFragment_to_articleFragment,
                bundle
            )
        }

        hackerFeedAdapter.setOnImageClickListener {
            if (it?.isImageSaved!!) {
                viewModel.saveStory(it)
            } else {
                viewModel.deleteStory(it)
            }
        }

        viewModel.topStoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { resourceResponse -> //Resource<NewsResponse
                when (resourceResponse) {
                    is Resource.Success -> {
                    }

                    is Resource.Error -> {
                        resourceResponse.message?.let { message ->
                            Toast.makeText(
                                activity,
                                "An error occured: $message",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    is Resource.Loading -> {
                    }
                }

            })

        viewModel.topStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let { hackerStory ->
                        hackerFeedAdapter.submitList(hackerStory.toList())
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

    fun setUpRecyclerView() {
        hackerFeedAdapter = HackerFeedAdapter()
        rvTopNews.apply {
            adapter = hackerFeedAdapter
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
        hackerFeedAdapter.filter(if (newText.length >= QUERY_SIZE_LIMIT) newText else null)

        return true
    }

    override fun onRefresh() {
        viewModel.getTopStores()

        val handler = Handler()
        handler.postDelayed({ //hide the loading screen after 30 secs if no cloud-session cookie
            swipeRefresh.isRefreshing = false
        }, SWIPE_TO_REFRESH_DELAY)
    }





}