package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.androideveloper.hackernewsfeed.play.util.Constants
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.HOT_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.JOB_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Constants.Companion.NEW_STORY_TYPE
import com.androideveloper.hackernewsfeed.play.util.Resource
import com.androideveloper.thenewsapp.adapter.HackerFeedAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: HackerFeedViewModel
    lateinit var savedFeedAdapter: HackerFeedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

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
            savedFeedAdapter.differ.submitList(hackerStories)
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
}