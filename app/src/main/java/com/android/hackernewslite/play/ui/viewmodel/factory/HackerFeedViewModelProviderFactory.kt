package com.android.hackernewslite.play.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.ui.viewmodel.HackerFeedViewModel

class HackerFeedViewModelProviderFactory(val hackerFeedRepository: HackerFeedRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return HackerFeedViewModel(hackerFeedRepository) as T
    }
}