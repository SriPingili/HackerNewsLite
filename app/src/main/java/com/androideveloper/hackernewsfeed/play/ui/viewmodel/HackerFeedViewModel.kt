package com.androideveloper.hackernewsfeed.play.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androideveloper.hackernewsfeed.play.model.HackerStory
import com.androideveloper.hackernewsfeed.play.repository.HackerFeedRepository
import com.androideveloper.hackernewsfeed.play.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.system.measureTimeMillis

class HackerFeedViewModel(val hackerFeedRepository: HackerFeedRepository) : ViewModel() {
    val topStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val newStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val jobStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();

    val topStoryLiveData: MutableLiveData<Resource<HackerStory>> = MutableLiveData();
    val newStoryLiveData: MutableLiveData<Resource<HackerStory>> = MutableLiveData();
    val jobStoryLiveData: MutableLiveData<Resource<HackerStory>> = MutableLiveData();

    init {
        getTopStores()
        getNewStories()
        getJobStories()
    }

    /*
    Fetches the top stories by calling the hacker news api
    * */
    fun getTopStores() = viewModelScope.launch() {
        topStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getTopStories()

        topStoriesLiveData.postValue(handleTopStoriesResponse(response))
    }

    private fun handleTopStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                for (id in hackerFeedResponse.take(200)) {
                    fetchTopStoryById(id)
                }
                return Resource.Success(hackerFeedResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun fetchTopStoryById(id: Int) = viewModelScope.launch {
        topStoryLiveData.postValue(Resource.Loading())

        val response = async {
            hackerFeedRepository.fetchStoryById(id)
        }
        topStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
    }

    private fun handleHackerStoryResponse(response: Response<HackerStory>): Resource<HackerStory> {
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                return Resource.Success(hackerStoryResponse)
            }
        }

        return Resource.Error(response.message())
    }

    /*
    Fetches the new/latest stories by calling the hacker news api
    * */
    fun getNewStories() = viewModelScope.launch() {
        newStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getNewStories()

        newStoriesLiveData.postValue(handleNewStoriesResponse(response))
    }

    private fun handleNewStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                val time = measureTimeMillis {
                    for (id in hackerFeedResponse.take(200)) {
                        fetchNewStoryById(id)
                    }
                }

                return Resource.Success(hackerFeedResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun fetchNewStoryById(id: Int) = viewModelScope.launch {
        newStoryLiveData.postValue(Resource.Loading())

        val response = async {
            hackerFeedRepository.fetchStoryById(id)
        }
        newStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
    }

    /*
    Fetches the latest job stories by calling the hacker news api
    * */
    fun getJobStories() = viewModelScope.launch() {
        jobStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getJobStories()

        jobStoriesLiveData.postValue(handleJobStoriesResponse(response))
    }

    private fun handleJobStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                for (id in hackerFeedResponse) {
                    fetchJobStoryById(id)
                }

                return Resource.Success(hackerFeedResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun fetchJobStoryById(id: Int) = viewModelScope.launch {
        jobStoryLiveData.postValue(Resource.Loading())

        val response = async {
            hackerFeedRepository.fetchStoryById(id)
        }
        jobStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
    }
}