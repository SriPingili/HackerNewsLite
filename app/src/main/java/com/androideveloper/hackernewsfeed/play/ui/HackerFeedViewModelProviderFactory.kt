package com.androideveloper.hackernewsfeed.play.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.ui.viewmodel.HackerFeedViewModel

class HackerFeedViewModelProviderFactory(val hackerFeedRepository: HackerFeedRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return HackerFeedViewModel(hackerFeedRepository) as T
    }
}