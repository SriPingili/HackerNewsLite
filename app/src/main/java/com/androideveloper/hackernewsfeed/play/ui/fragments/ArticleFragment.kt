package com.androideveloper.hackernewsfeed.play.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androideveloper.hackernewsfeed.play.R
import com.androideveloper.hackernewsfeed.play.ui.HackerFeedActivity
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel
import kotlinx.android.synthetic.main.fragment_article.*

/*
*  This Fragment is responsible for displaying the Hacker News articles in a webview.
* */
class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: HackerFeedViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel

        val article = args.articleArg

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            CookieManager.getInstance().setAcceptCookie(true)
            webChromeClient = WebChromeClient()
        }

        fab.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, article.url)
            startActivity(Intent.createChooser(sharingIntent, "Share Via"))
        }
    }
}