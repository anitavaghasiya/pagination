package com.vicky.paggingretrofitrecyclerview.api;

import com.vicky.paggingretrofitrecyclerview.model.ModelResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("/api/GetArticle")
    Call<ModelResponse> getAllData(@Field("Mode") String Mode,
                                   @Field("UserType") String UserType,
                                   @Field("PageNumber") int PageNumber);
}
