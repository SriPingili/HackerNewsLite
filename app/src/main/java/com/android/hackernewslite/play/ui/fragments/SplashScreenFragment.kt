package com.android.hackernewslite.play.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Resource
import kotlinx.android.synthetic.main.activity_hacker_feed.*

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    lateinit var viewModel: HackerFeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        (activity as HackerFeedActivity).hideBotomNav()

        viewModel.topStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let { hackerStory ->
                       if(hackerStory.size > 20)
                       {
//                           (activity as HackerFeedActivity).ShowBotomNav()
                           findNavController().navigate(R.id.action_splashScreenFragment_to_topNewsFragment, null)
                           //todo find way to finish the fragment
                          activity?.getFragmentManager()?.popBackStack()
                       }
                    }
                }

                is Resource.Error -> {
                    resourceResponse.message?.let { message ->
                        Log.v(TAG, " error = $message")
                    }
                }

                is Resource.Loading -> {
                    //showProgressBar()
                }
            }
        })
    }

    fun hideBotomNav(){
        activity?.bottomNavigationView?.visibility = GONE
        activity?.actionBar?.hide()



    }


    fun ShowBotomNav(){
        activity?.bottomNavigationView?.visibility = VISIBLE
        activity?.actionBar?.show()
    }
}