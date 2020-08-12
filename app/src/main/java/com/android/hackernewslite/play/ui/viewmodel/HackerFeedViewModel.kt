package com.android.hackernewslite.play.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.hackernewslite.play.model.HackerStory
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.util.Constants.Companion.HOT_STORY_TYPE
import com.android.hackernewslite.play.util.Constants.Companion.JOB_STORY_TYPE
import com.android.hackernewslite.play.util.Constants.Companion.NEW_STORY_TYPE
import com.android.hackernewslite.play.util.Constants.Companion.ResponseCall
import com.android.hackernewslite.play.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class HackerFeedViewModel(val hackerFeedRepository: HackerFeedRepository) : ViewModel() {
    //the live data object for the id's of all stories (Top stories, Job Stories, New stories)
    val topStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val newStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val jobStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();

    //the live data object for individual story (Top story, Job Story, New story)
    val topStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();
    val newStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();
    val jobStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();

    //saves the response from api call and appends it each time
    val topStoryResponse = ArrayList<HackerStory>()
    val newStoryResponse = ArrayList<HackerStory>()
    val jobStoryResponse = ArrayList<HackerStory>()

    var initialTopResponseSize = 0
    var responseCounter = 0
    var apiCallStatus = ResponseCall.COMPLETED_SUCCESSFULLY

    init {
        getTopStores()
        getNewStories()
        getJobStories()
    }

    /*
    Fetches the top stories by calling the hacker news api
    * */
    fun getTopStores() = viewModelScope.launch() {
        apiCallStatus = ResponseCall.IN_PROGRESS
        topStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getTopStories()

        topStoriesLiveData.postValue(handleTopStoriesResponse(response))
    }

    private fun handleTopStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        apiCallStatus = ResponseCall.COMPLETED_SUCCESSFULLY
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                responseCounter = 0
                topStoryResponse.clear()

                val newResponse = hackerFeedResponse.take(250)
                initialTopResponseSize = newResponse.size

                for (id in newResponse) {
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

    private fun handleHackerStoryResponse(response: Response<HackerStory>): Resource<List<HackerStory>> {
        responseCounter++
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                hackerStoryResponse.storyType = HOT_STORY_TYPE
                topStoryResponse.add(hackerStoryResponse)
                return Resource.Success(topStoryResponse)
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
                for (id in hackerFeedResponse.take(200)) {
                    fetchNewStoryById(id)
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
        newStoryLiveData.postValue(handleNewHackerStoryResponse(response.await()))
    }

    private fun handleNewHackerStoryResponse(response: Response<HackerStory>): Resource<List<HackerStory>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                hackerStoryResponse.storyType = NEW_STORY_TYPE
                newStoryResponse.add(hackerStoryResponse)
                return Resource.Success(newStoryResponse)
            }
        }

        return Resource.Error(response.message())
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
                for (id in hackerFeedResponse.take(200)) {
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
        jobStoryLiveData.postValue(handleJobHackerStoryResponse(response.await()))
    }

    private fun handleJobHackerStoryResponse(response: Response<HackerStory>): Resource<List<HackerStory>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                hackerStoryResponse.storyType = JOB_STORY_TYPE
                jobStoryResponse.add(hackerStoryResponse)
                return Resource.Success(jobStoryResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun getAllSavedStories() = hackerFeedRepository.getAllSavedNews()

    fun saveStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.upsert(hackerStory)
    }

    fun deleteStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.delete(hackerStory)
    }
}