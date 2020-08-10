package com.android.hackernewslite.play.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.hackernewslite.play.model.HackerStory
import com.android.hackernewslite.play.repository.HackerFeedRepository
import com.android.hackernewslite.play.util.Constants.Companion.HOT_STORY_TYPE
import com.android.hackernewslite.play.util.Constants.Companion.JOB_STORY_TYPE
import com.android.hackernewslite.play.util.Constants.Companion.NEW_STORY_TYPE
import com.android.hackernewslite.play.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.system.measureTimeMillis

class HackerFeedViewModel(val hackerFeedRepository: HackerFeedRepository) : ViewModel() {
    val topStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val newStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();
    val jobStoriesLiveData: MutableLiveData<Resource<List<Int>>> = MutableLiveData();

    val topStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();
    val newStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();
    val jobStoryLiveData: MutableLiveData<Resource<List<HackerStory>>> = MutableLiveData();

    val topStoryResponse = ArrayList<HackerStory>()
    val newStoryResponse = ArrayList<HackerStory>()
    val jobStoryResponse = ArrayList<HackerStory>()

    init {
        getTopStores()
        getNewStories()
        getJobStories()

    }

    fun getTopStores() = viewModelScope.launch() {
        topStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getTopStories()
        topStoriesLiveData.postValue(handleTopStoriesResponse(response))
    }

    private fun handleTopStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->

                for (id in hackerFeedResponse) {
                    fetchStoryBydId(id)
                }


                return Resource.Success(hackerFeedResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun fetchStoryBydId(id: Int) = viewModelScope.launch {
        topStoryLiveData.postValue(Resource.Loading())

        val response = async {
            hackerFeedRepository.fetchStoryById(id)
        }
        topStoryLiveData.postValue(handleHackerStoryResponse(response.await()))
    }

    private fun handleHackerStoryResponse(response: Response<HackerStory>): Resource<List<HackerStory>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerStoryResponse ->
                hackerStoryResponse.storyType = HOT_STORY_TYPE
                topStoryResponse.add(hackerStoryResponse)
                return Resource.Success(topStoryResponse)
            }
        }

        return Resource.Error(response.message())
    }

    fun updateTopStoryLiveData(hackerStory: HackerStory) {
        val response = topStoryResponse.find {
            it.id == hackerStory.id
        }

        if (response != null) {
            val index = topStoryResponse.indexOf(response)
            topStoryResponse.remove(response)

            topStoryResponse.add(index, hackerStory)

            topStoryLiveData.postValue(Resource.Success(topStoryResponse))
        }
    }

    fun getNewStories() = viewModelScope.launch() {
        newStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getNewStories()
        newStoriesLiveData.postValue(handleNewStoriesResponse(response))
    }

    private fun handleNewStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                val time = measureTimeMillis {
                    for (id in hackerFeedResponse) {
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

    fun updateNewStoryLiveData(hackerStory: HackerStory) {
        val response = newStoryResponse.find {
            it.id == hackerStory.id
        }

        if (response != null) {
            val index = newStoryResponse.indexOf(response)
            newStoryResponse.remove(response)

            newStoryResponse.add(index, hackerStory)

            newStoryLiveData.postValue(Resource.Success(newStoryResponse))
        }
    }

    fun getJobStories() = viewModelScope.launch() {
        jobStoriesLiveData.postValue(Resource.Loading())

        val response = hackerFeedRepository.getJobStories()
        jobStoriesLiveData.postValue(handleJobStoriesResponse(response))
    }

    private fun handleJobStoriesResponse(response: Response<List<Int>>): Resource<List<Int>> {
        if (response.isSuccessful) {
            response.body()?.let { hackerFeedResponse ->
                val time = measureTimeMillis {
                    for (id in hackerFeedResponse) {
                        fetchJobStoryById(id)
                    }
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

    fun updateJobStoryLiveData(hackerStory: HackerStory) {
        val response = jobStoryResponse.find {
            it.id == hackerStory.id
        }

        if (response != null) {
            val index = jobStoryResponse.indexOf(response)
            jobStoryResponse.remove(response)

            jobStoryResponse.add(index, hackerStory)

            jobStoryLiveData.postValue(Resource.Success(jobStoryResponse))
        }
    }

    fun getAllSavedStories() = hackerFeedRepository.getAllSavedNews()

    fun saveStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.upsert(hackerStory)
    }

    fun deleteStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.delete(hackerStory)
    }
}