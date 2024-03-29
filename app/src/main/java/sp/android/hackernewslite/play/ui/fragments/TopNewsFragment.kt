package sp.android.hackernewslite.play.ui.fragments

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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_top_news.*
import sp.android.hackernewslite.play.R
import sp.android.hackernewslite.play.adapter.HackerFeedAdapter
import sp.android.hackernewslite.play.extensions.initialize
import sp.android.hackernewslite.play.ui.HackerFeedActivity
import sp.android.hackernewslite.play.ui.SettingsActivity
import sp.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import sp.android.hackernewslite.play.util.Constants
import sp.android.hackernewslite.play.util.Constants.Companion.AppFlow
import sp.android.hackernewslite.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import sp.android.hackernewslite.play.util.Constants.Companion.ResponseCall.COMPLETED_SUCCESSFULLY
import sp.android.hackernewslite.play.util.Constants.Companion.ResponseCall.IN_PROGRESS
import sp.android.hackernewslite.play.util.Constants.Companion.SWIPE_TO_REFRESH_DELAY
import sp.android.hackernewslite.play.util.CustomTabsUtil
import sp.android.hackernewslite.play.util.Resource
import sp.android.hackernewslite.play.util.SharePreferenceUtil

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
    var apiCallStatus = COMPLETED_SUCCESSFULLY
    var result = false
    val TAG = TopNewsFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HackerFeedActivity).showBottomNavAndActionBar()
        result = args.isFromSplashScreen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        customTabsUtil = CustomTabsUtil(context!!)
        appFlow = AppFlow.FIRST_TIME

        val savedResponse = SharePreferenceUtil.getTopHackerStoriesToSharedPres(context!!)

        if (savedResponse.size > 0) {
            appFlow = AppFlow.IS_UPDATING_IN_BACKGROUND
            hackerFeedAdapter.submitList(savedResponse)
        }

        else if (result) {
            Snackbar.make(view, getString(R.string.syncing), Snackbar.LENGTH_LONG).show()
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
                Toast.makeText(context, getString(R.string.cannot_open_page), Toast.LENGTH_SHORT)
                    .show()
                return@setOnItemClickListener
            }

            if (SharePreferenceUtil.getCustomTabsPreferenceStatus(context!!)) {
                customTabsUtil.setToUseBackArrow()
                customTabsUtil.openCustomTab(it.url)
            } else {
                val bundle = Bundle().apply {
                    putSerializable(
                        Constants.ARTICLE_ARG,
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
                Toast.makeText(context, getString(R.string.story_save_success), Toast.LENGTH_SHORT)
                    .show()
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
                        apiCallStatus = COMPLETED_SUCCESSFULLY
                        swipeRefresh?.isRefreshing = false
                    }

                    is Resource.Error -> {
                        apiCallStatus = COMPLETED_SUCCESSFULLY
                        swipeRefresh?.isRefreshing = false
                        resourceResponse.message?.let { message ->
                            Log.v(TAG, String.format(Constants.ERROR_MESSAGE, message))
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
                        Log.v(TAG, String.format(Constants.ERROR_MESSAGE, message))
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
        if (apiCallStatus.equals(COMPLETED_SUCCESSFULLY)) {
            viewModel.getTopStores()
            apiCallStatus = IN_PROGRESS
            Snackbar.make(view!!, getString(R.string.syncing), Snackbar.LENGTH_LONG).show()
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