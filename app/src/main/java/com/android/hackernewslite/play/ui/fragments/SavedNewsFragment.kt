package com.android.hackernewslite.play.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.adapter.HackerFeedAdapter
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.SettingsActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Constants
import com.android.hackernewslite.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import com.android.hackernewslite.play.util.CustomTabsUtil
import com.android.hackernewslite.play.util.SharePreferenceUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

/*
* This Fragment is responsible for displaying the news saved by the user
* */
class SavedNewsFragment : Fragment(R.layout.fragment_saved_news), SearchView.OnQueryTextListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var hackerFeedAdapter: HackerFeedAdapter
    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null
    lateinit var customTabsUtil: CustomTabsUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()
        customTabsUtil = CustomTabsUtil(context!!)

        setHasOptionsMenu(true)

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
                    ) //this needs to be same as in news_nav_graph.xml
                }

                findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
                )
            }
        }

        hackerFeedAdapter.setOnImageClickListener {
            val status = it.isImageSaved

            if (!status) {
                viewModel.deleteStory(it)
            }
        }

        viewModel.getAllSavedStories().observe(viewLifecycleOwner, Observer { hackerStories ->
            if (hackerStories.size == 0) {
                noResultsID.visibility = VISIBLE
                rvSavedNews.visibility = INVISIBLE
            } else {
                noResultsID.visibility = INVISIBLE
                rvSavedNews.visibility = VISIBLE
                hackerFeedAdapter.submitList(hackerStories)
                hackerFeedAdapter.notifyDataSetChanged()//todo this might not be needed
            }
        })


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val hackerStory = hackerFeedAdapter.differ.currentList[position]

                viewModel.deleteStory(hackerStory)

                Snackbar.make(view, getString(R.string.delete_success), Snackbar.LENGTH_SHORT)
                    .apply {
                        setAction(getString(R.string.undo)) {
                            viewModel.saveStory(hackerStory)
                        }//todo implement this, simple

                        show()
                    }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }

    /*
    helper method that sets up the recycler view
    * */
    fun setUpRecyclerView() {
        hackerFeedAdapter = HackerFeedAdapter()

        rvSavedNews.apply {
            adapter = hackerFeedAdapter
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = null
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

}