package com.android.hackernewslite.play.ui.fragments

import android.app.ActivityOptions
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.adapter.HackerFeedAdapter
import com.android.hackernewslite.play.extensions.initialize
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.SettingsActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Constants
import com.android.hackernewslite.play.util.Constants.Companion.AppFlow
import com.android.hackernewslite.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import com.android.hackernewslite.play.util.Constants.Companion.SWIPE_TO_REFRESH_DELAY
import com.android.hackernewslite.play.util.CustomTabsUtil
import com.android.hackernewslite.play.util.Resource
import com.android.hackernewslite.play.util.SharePreferenceUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_top_news.*

/*
* This Fragment is responsible for displaying the top news from the Hacker News api
* */
class TopNewsFragment : Fragment(R.layout.fragment_top_news), SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null
    lateinit var customTabsUtil: CustomTabsUtil
    val args: TopNewsFragmentArgs by navArgs()
    lateinit var appFlow: AppFlow
    val TAG = "TopNewsFragment"
    var result = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        result = args.isFromSplashScreen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        (activity as HackerFeedActivity).showBottomNavAndActionBar()
        customTabsUtil = CustomTabsUtil(context!!)
        appFlow = AppFlow.FIRST_TIME

        val savedResponse = SharePreferenceUtil.getTopHackerStoriesToSharedPres(context!!)

        if (savedResponse.size > 0) {
            appFlow = AppFlow.IS_UPDATING_IN_BACKGROUND
            hackerFeedAdapter.submitList(savedResponse)
        }

        if (result) {
            Snackbar.make(view, "Syncing...", Snackbar.LENGTH_LONG).show()
        }

        setHasOptionsMenu(true)
        swipeRefresh.initialize(this)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.getFragmentManager()?.popBackStack()
                    val intent = Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context!!.startActivity(intent);
                }

            })

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
                    R.id.action_topNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }

        hackerFeedAdapter.setOnImageClickListener {
            if (it?.isImageSaved!!) {
                Toast.makeText(context, "Story saved successfully.", Toast.LENGTH_SHORT).show()
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
                            Log.v(TAG, "An error occured: $message")
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
                        when (appFlow) {
                            AppFlow.IS_UPDATING_IN_BACKGROUND -> {
                                if (viewModel.responseCounter >= viewModel.initialTopResponseSize) {
                                    hackerFeedAdapter.submitList(hackerStory.toList())
                                }
                            }

                            AppFlow.FIRST_TIME -> {
                                hackerFeedAdapter.submitList(hackerStory.toList())
                            }
                        }

                        SharePreferenceUtil.saveTopHackerStoriesToSharedPres(hackerStory, context!!)
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_id) {
            startActivity(Intent(context, SettingsActivity::class.java))
//            activity?.overridePendingTransition(0, R.anim.slide_in_right)
            return true
        }

        return super.onOptionsItemSelected(item)
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
        appFlow = AppFlow.IS_UPDATING_IN_BACKGROUND
        if (viewModel.apiCallStatus.equals(Constants.Companion.ResponseCall.COMPLETED_SUCCESSFULLY)) {
            viewModel.getTopStores()
            Snackbar.make(view!!, "Syncing...", Snackbar.LENGTH_LONG).show()
            val handler = Handler()
            handler.postDelayed({
                swipeRefresh?.isRefreshing = false
            }, SWIPE_TO_REFRESH_DELAY)
        }
    }

    override fun onPause() {
        super.onPause()
        result = false
    }
}