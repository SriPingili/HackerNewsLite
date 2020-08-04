package com.androideveloper.thenewsapp.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.adapter.HackerFeedAdapter
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Constants
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.HOT_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.JOB_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.NEW_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.QUERY_SIZE_LIMIT
import com.androideveloper.hackernewsfeed.play.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news), SearchView.OnQueryTextListener {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var savedFeedAdapter: HackerFeedAdapter
    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

        setHasOptionsMenu(true)

        savedFeedAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article_arg", it) //this needs to be same as in news_nav_graph.xml
            }

            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
        }

        savedFeedAdapter.setOnImageClickListener {
            //todo also save id and isenabled (if true) to db, everytime app starts, check in db for true and update flags accrdingly
            if (it?.isImageSaved!!) {
                viewModel.saveStory(it)
            } else {
                when (it.storyType) {
                    HOT_STORY_TYPE -> viewModel.updateTopStoryLiveData(it)
                    NEW_STORY_TYPE -> viewModel.updateNewStoryLiveData(it)
                    JOB_STORY_TYPE -> viewModel.updateJobStoryLiveData(it)
                }
                viewModel.deleteStory(it)
            }
            Toast.makeText(context, "clicked ${it.isImageSaved}", Toast.LENGTH_SHORT).show()
        }

        viewModel.getAllSavedStories().observe(viewLifecycleOwner, Observer { hackerStories ->
            savedFeedAdapter.submitList(hackerStories)
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
                val hackerStory = savedFeedAdapter.differ.currentList[position]

                viewModel.deleteStory(hackerStory)

                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_SHORT).apply {
                    setAction("UNDO", View.OnClickListener {
                        viewModel.saveStory(hackerStory)
                    })
                    /*setAction("UNDO") {
                        viewModel.saveArticle(article)
                    }*/

                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }
    }


    fun setUpRecyclerView() {
        savedFeedAdapter = HackerFeedAdapter()

        rvSavedNews.apply {
            adapter = savedFeedAdapter
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
            val searchFrame = searchView?.findViewById(androidx.appcompat.R.id.search_edit_frame) as LinearLayout
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
        savedFeedAdapter.filter( if (newText.length >= QUERY_SIZE_LIMIT) newText else null)

        return true
    }

}