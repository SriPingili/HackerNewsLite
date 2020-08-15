package sp.android.hackernewslite.play.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_article.*
import sp.android.hackernewslite.play.R
import sp.android.hackernewslite.play.ui.HackerFeedActivity
import sp.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import sp.android.hackernewslite.play.util.AppUtil

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
            setBackgroundColor(resources.getColor(R.color.backgroundColor, null))
            loadUrl(article.url)
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            CookieManager.getInstance().setAcceptCookie(true)
            webChromeClient = WebChromeClient()
        }

        fab.setOnClickListener {
            AppUtil.startShareIntent(article.url!!, context!!)
        }
    }
}