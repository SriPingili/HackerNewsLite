package com.androideveloper.hackernewsfeed.play.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.system.measureTimeMillis

class HackerFeedViewModel(val hackerFeedRepository: HackerFeedRepository) : ViewModel() {
    val topStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val hackerStoryLiveData: MutableLiveData<Resource<HackerStory>> = MutableLiveData();

    init {
        getTopStores()

    }

    fun getTopStores() = viewModelScope.launch() {
        topStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getTopStories()
        topStoriesLiveData.postValue(handleTopStoriesResponse(response))
    }

    private fun handleTopStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                val time = measureTimeMillis {
                    for (id in hackerFeedResponse){
                        //fetchStoryBydId(id)
                        viewModelScope.launch {
                            hackerStoryLiveData.postValue(Resource.Loading())

                            val response = async {
                                hackerFeedRepository.fetchStoryById(id)
                            }
                            hackerStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
                        }
                    }
                }


                Log.v("zzzz","time taken = $time")

                return Resource.Success(hackerFeedResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun fetchStoryBydId(id: Int) = viewModelScope.launch {
        hackerStoryLiveData.postValue(Resource.Loading())

        val response = async {
            hackerFeedRepository.fetchStoryById(id)
        }
        hackerStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
    }

    private fun handleHackerStoryResponse(response: Response<HackerStory>): Resource<HackerStory> {
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                return Resource.Success(hackerStoryResponse)
            }
        }

        return Resource.Error(response.message())
    }
}