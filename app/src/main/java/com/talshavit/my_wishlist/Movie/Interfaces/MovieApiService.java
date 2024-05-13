package com.talshavit.my_wishlist.Movie.Interfaces;

import com.talshavit.my_wishlist.Movie.ModelsApi.MovieSearchResponse;
import com.talshavit.my_wishlist.Movie.ModelsApi.RootForSpecific;
import com.talshavit.my_wishlist.HelpersForApi.RootForVideo;

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

    @GET("movie/{movie_id}")
    Call<RootForSpecific> getMovieDetails(
            @Path("movie_id") int movie_id,
            @Query("api_key") String apiKey

    );

    @GET("movie/{movie_id}/videos")
    Call<RootForVideo> getVideoDetails(
            @Path("movie_id") int movie_id,
            @Query("api_key") String apiKey

    );

}
