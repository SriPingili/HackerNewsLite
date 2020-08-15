package sp.android.hackernewslite.play.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sp.android.hackernewslite.play.model.HackerStory

interface HackerFeedAPI {

    @GET("v0/newstories.json")
    suspend fun getNewStories(
        @Query("print")
        pretty: String = "pretty"
    ): Response<List<Int>>

    @GET("v0/topstories.json")
    suspend fun getTopStories(
        @Query("print")
        pretty: String = "pretty"
    ): Response<List<Int>>

    @GET("/v0/jobstories.json")
    suspend fun getJobStories(
        @Query("print")
        pretty: String = "pretty"
    ): Response<List<Int>>

    @GET("v0/item/{id}.json")
    suspend fun getStoryById(
        @Path("id") id: Int,
        @Query("print")
        pretty: String = "pretty"
    ): Response<HackerStory>
}