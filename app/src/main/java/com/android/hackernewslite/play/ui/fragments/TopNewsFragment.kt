package com.android.thenewsapp.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.*
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
import com.android.hackernewslite.play.util.Constants.Companion.shouldOpenInCustomTabs
import com.android.hackernewslite.play.util.CustomTabsUtil
import com.android.hackernewslite.play.util.Resource
import com.android.hackernewslite.play.util.SharePreferenceUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_top_news.*


class TopNewsFragment : Fragment(R.layout.fragment_top_news), SearchView.OnQueryTextListener,
    SwipeRefreshLayout.OnRefreshListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    lateinit var customTabsUtil: CustomTabsUtil
    lateinit var appFlow: AppFlow
    val TAG = "BreakingNewsFragment"

    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null
    val args: TopNewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        customTabsUtil = CustomTabsUtil(context!!)
        (activity as HackerFeedActivity).ShowBotomNav()
        appFlow = AppFlow.FIRST_TIME

        val result = args.isFromSplashScreen

        val savedResponse = SharePreferenceUtil.getTopHackerStoriesToSharedPres(context!!)

        if (savedResponse.size > 0) {
            Log.v("zzzzzzzz", "Size of saved items = ${savedResponse.size} ")
            appFlow = AppFlow.IS_UPDATING_IN_BACKGROUND
            hackerFeedAdapter.submitList(savedResponse)
        }

        if (result) {
            Snackbar.make(view, "Syncing...", Snackbar.LENGTH_LONG).show()
        }
//        else {
//            if (result) {
//                Snackbar.make(view, "Syncing...", Snackbar.LENGTH_LONG).show()
//            }
//        }


        setHasOptionsMenu(true)

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
//                        Log.v(
//                            "zzzzzzzz",
//                            "responseCounter size = ${viewModel.responseCounter} and viewModel.initialTopResponseSize = ${viewModel.initialTopResponseSize}" +
//                                    "and current response size = ${hackerStory.size}"
//                        )

                        when (appFlow) {
                            AppFlow.IS_UPDATING_IN_BACKGROUND -> {
                                if (viewModel.responseCounter >= viewModel.initialTopResponseSize) {
                                    Log.v(
                                        "zzzzzzzz",
                                        "responseCounter size = ${viewModel.responseCounter} and viewModel.initialTopResponseSize = ${viewModel.initialTopResponseSize}"
                                    )
//                                    Snackbar.make(view, "Syncing...", Snackbar.LENGTH_SHORT).show()
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_id) {
            startActivity(Intent(context, SettingsActivity::class.java))
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
            handler.postDelayed({ //hide the loading screen after 30 secs if no cloud-session cookie
                swipeRefresh?.isRefreshing = false
            }, SWIPE_TO_REFRESH_DELAY)
        }
    }


}