package sp.android.hackernewslite.play.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sp.android.hackernewslite.play.R
import sp.android.hackernewslite.play.ui.HackerFeedActivity
import sp.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import sp.android.hackernewslite.play.util.Constants

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {
    lateinit var viewModel: HackerFeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        (activity as HackerFeedActivity).hideBottomNavAndActionBar()

        val handler = Handler()
        handler.postDelayed({
            val bundle = Bundle().apply {
                putBoolean(
                    Constants.IS_FROM_SPLASH_SCREEN,
                    true
                )//this needs to be same as in news_nav_graph.xml
            }

            findNavController().navigate(
                R.id.action_splashScreenFragment_to_topNewsFragment,
                bundle
            )
        }, Constants.SPLASH_SCREEN_DELAY)
    }
}