package com.androideveloper.thenewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: HackerFeedViewModel
//    lateinit var savedNewsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        setUpRecyclerView()

//        savedNewsAdapter.setOnItemClickListener {
//            val bundle = Bundle().apply {
//                putSerializable("article_arg", it) //this needs to be same as in news_nav_graph.xml
//            }
//
//            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
//        }

//        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
//            savedNewsAdapter.differ.submitList(articles)
//
//        })


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
//                val article = savedNewsAdapter.differ.currentList[position]

//                viewModel.deleteArticle(article)

                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_SHORT).apply {
                    setAction("UNDO", View.OnClickListener {
//                        viewModel.saveArticle(article)
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
//        savedNewsAdapter = NewsAdapter()

        rvSavedNews.apply {
//            adapter = savedNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}