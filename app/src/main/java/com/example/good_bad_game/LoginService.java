package com.example.good_bad_game;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

public interface LoginService {

    @GET("users/")
    Call<List<Login>> getPosts();

    @POST("users/")
    Call<Post> createPost(@Body Post post);
}
