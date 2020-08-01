package com.androideveloper.hackernewsfeed.play.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_top_news.*
import kotlinx.android.synthetic.main.fragment_saved_news.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: HackerFeedViewModel
//    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel

//        val article = args.articleArg

        webView.apply {
            webViewClient = WebViewClient()
//            loadUrl(article.url)
        }


        fab.setOnClickListener {
//            viewModel.saveArticle(article)
            Snackbar.make(it, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
}