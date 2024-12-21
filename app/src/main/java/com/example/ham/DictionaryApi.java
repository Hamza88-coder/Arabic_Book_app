package com.example.ham;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DictionaryApi {

    @GET("en/{word}")
    Call<List<WordResult>> getMeaning(@Path("word") String word);
}
