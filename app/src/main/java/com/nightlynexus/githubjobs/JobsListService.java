package com.nightlynexus.githubjobs;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface JobsListService {

    @GET("/positions.json")
    void getJobsList(@Query("description") String description, @Query("location") String location,
                     @Query("page") int page, Callback<List<Job>> cb);

    @GET("/positions.json")
    void getJobsList(@Query("description") String description, @Query("location") String location,
                     @Query("full_time") boolean fullTime, @Query("page") int page,
                     Callback<List<Job>> cb);
}
