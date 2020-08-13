package com.androideveloper.hackernewsfeed.play.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

/*
*  This Fragment is responsible for displaying the Hacker News articles in a webview.
* */
class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: HackerFeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel

        webView.apply {
            webViewClient = WebViewClient()
            //TODO implement this
        }


        fab.setOnClickListener {
            //TODO implement this
            Snackbar.make(it, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
}