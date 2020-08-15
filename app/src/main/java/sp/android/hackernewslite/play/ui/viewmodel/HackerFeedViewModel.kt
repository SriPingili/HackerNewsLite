package sp.android.hackernewslite.play.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import sp.android.hackernewslite.play.model.HackerStory
import sp.android.hackernewslite.play.repository.HackerFeedRepository
import sp.android.hackernewslite.play.util.Constants.Companion.HACKER_FEED_RESPONSE_SIZE
import sp.android.hackernewslite.play.util.Constants.Companion.HACKER_FEED_TOP_STORY_RESPONSE_SIZE
import sp.android.hackernewslite.play.util.Constants.Companion.HOT_STORY_TYPE
import sp.android.hackernewslite.play.util.Constants.Companion.JOB_STORY_TYPE
import sp.android.hackernewslite.play.util.Constants.Companion.NEW_STORY_TYPE
import sp.android.hackernewslite.play.util.Resource

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
                responseCounter = 0
                topStoryResponse.clear()

                val newResponse = hackerFeedResponse.take(HACKER_FEED_TOP_STORY_RESPONSE_SIZE)
                initialTopResponseSize = newResponse.size

                for (id in newResponse) {
                    fetchTopStoryById(id)
                }

                return Resource.Success(newResponse)
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
                val newResponse = hackerFeedResponse.take(HACKER_FEED_RESPONSE_SIZE)

                for (id in newResponse) {
                    fetchNewStoryById(id)
                }

                return Resource.Success(newResponse)
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
                val newResponse = hackerFeedResponse.take(HACKER_FEED_RESPONSE_SIZE)

                for (id in newResponse) {
                    fetchJobStoryById(id)
                }

                return Resource.Success(newResponse)
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

    /*
    Room Queries
    */

    fun getAllSavedStories() = hackerFeedRepository.getAllSavedNews()

    fun saveStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.upsert(hackerStory)
    }

    fun deleteStory(hackerStory: HackerStory) = viewModelScope.launch {
        hackerFeedRepository.delete(hackerStory)
    }
}