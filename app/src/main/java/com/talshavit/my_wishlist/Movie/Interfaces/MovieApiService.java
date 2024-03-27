package com.talshavit.my_wishlist.Movie.Interfaces;

import com.talshavit.my_wishlist.Movie.Models.MovieSearchResponse;
import com.talshavit.my_wishlist.Movie.Models.RootForSpecific;
import com.talshavit.my_wishlist.Movie.Models.RootForVideo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("search/movie")
    Call<MovieSearchResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("sort_by") String sortBy,
            @Query("page") int page
    );

    @GET("movie/{id}")
    Call<RootForSpecific> getMovieDetails(
            @Path("id") int id,
            @Query("api_key") String apiKey

    );

    @GET("movie/{id}/videos")
    Call<RootForVideo> getVideoDetails(
            @Path("id") int id,
            @Query("api_key") String apiKey

    );

}
