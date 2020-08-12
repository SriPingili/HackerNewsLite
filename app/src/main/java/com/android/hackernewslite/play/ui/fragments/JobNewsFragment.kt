package com.android.hackernewslite.play.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
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
import com.android.hackernewslite.play.ui.SettingsActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Constants
import com.android.hackernewslite.play.util.Constants.Companion.ResponseCall.COMPLETED_SUCCESSFULLY
import com.android.hackernewslite.play.util.Constants.Companion.ResponseCall.IN_PROGRESS
import com.android.hackernewslite.play.util.Constants.Companion.SWIPE_TO_REFRESH_DELAY
import com.android.hackernewslite.play.util.CustomTabsUtil
import com.android.hackernewslite.play.util.Resource
import com.android.hackernewslite.play.util.SharePreferenceUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_job_news.*

/*
* This Fragment is responsible for displaying the latest Job posts from the Hacker News api
* */
class JobNewsFragment : Fragment(R.layout.fragment_job_news), SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null
    lateinit var customTabsUtil: CustomTabsUtil
    val TAG = "JobNewsFragment"
    var apiCallStatus = COMPLETED_SUCCESSFULLY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        customTabsUtil = CustomTabsUtil(context!!)
        setHasOptionsMenu(true)
        swipeRefresh.initialize(this)

        hackerFeedAdapter.setOnItemClickListener {
            if (it.url.isNullOrBlank()) {
                Toast.makeText(context, "Cannot open this page", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            if (SharePreferenceUtil.getCustomTabsPreferenceStatus(context!!)) {
                customTabsUtil.setToUseBackArrow()
                customTabsUtil.openCustomTab(it.url)
            } else {
                val bundle = Bundle().apply {
                    putSerializable(
                        "article_arg",
                        it
                    )//this needs to be same as in news_nav_graph.xml
                }

                findNavController().navigate(
                    R.id.action_jobNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }

        hackerFeedAdapter.setOnImageClickListener {
            if (it?.isImageSaved!!) {
                viewModel.saveStory(it)
            } else {
                viewModel.deleteStory(it)
            }
        }

        viewModel.jobStoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { resourceResponse -> //Resource<NewsResponse
                when (resourceResponse) {
                    is Resource.Success -> {
                        apiCallStatus = COMPLETED_SUCCESSFULLY
                        swipeRefresh?.isRefreshing = false
                    }

                    is Resource.Error -> {
                        apiCallStatus = COMPLETED_SUCCESSFULLY
                        swipeRefresh?.isRefreshing = false
                        resourceResponse.message?.let { message ->
                            Log.v(TAG, "An error occured: $message")
                        }
                    }

                    is Resource.Loading -> {
                    }
                }

            })

        viewModel.jobStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let {
                        hackerFeedAdapter.submitList(it.toList())
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
    helper method that sets up the recycler view
    * */
    fun setUpRecyclerView() {
        hackerFeedAdapter = HackerFeedAdapter()
        rvJobNews.apply {
            adapter = hackerFeedAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_id) {
            startActivity(Intent(context, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
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
        hackerFeedAdapter.filter(if (newText.length >= Constants.QUERY_SIZE_LIMIT) newText else null)

        return true
    }

    override fun onRefresh() {
        if (apiCallStatus.equals(COMPLETED_SUCCESSFULLY)) {
            viewModel.getJobStories()
            apiCallStatus = IN_PROGRESS
            view?.let { Snackbar.make(it, "Syncing...", Snackbar.LENGTH_SHORT).show() }

            val handler = Handler()
            handler.postDelayed({ //hide the loading screen after 3 secs
                swipeRefresh?.isRefreshing = false
            }, SWIPE_TO_REFRESH_DELAY)
        }
    }
}