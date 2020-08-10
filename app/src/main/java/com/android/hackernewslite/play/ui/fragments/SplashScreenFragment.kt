package com.android.hackernewslite.play.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.hackernewslite.play.R
import com.android.hackernewslite.play.ui.HackerFeedActivity
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel
import com.android.hackernewslite.play.util.Resource

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    lateinit var viewModel: HackerFeedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerFeedActivity).viewModel
        (activity as HackerFeedActivity).hideBottomNavAndActionBar()

        viewModel.topStoryLiveData.observe(viewLifecycleOwner, Observer { resourceResponse ->
            when (resourceResponse) {

                is Resource.Success -> {
                    resourceResponse.data?.let { hackerStory ->
                        if (hackerStory.size > 5) {
                            findNavController().navigate(
                                R.id.action_splashScreenFragment_to_topNewsFragment,
                                null
                            )
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
                }
            }
        })
    }
}