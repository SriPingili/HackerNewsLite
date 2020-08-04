package com.android.hackernewslite.play.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*


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
//            viewModel.saveArticle(article)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
//            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
            sharingIntent.putExtra(Intent.EXTRA_TEXT, article.url)
            startActivity(Intent.createChooser(sharingIntent, "Share Via"))
            Snackbar.make(it, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
}