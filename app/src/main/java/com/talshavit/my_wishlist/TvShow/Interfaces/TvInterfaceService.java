package com.talshavit.my_wishlist.TvShow.Interfaces;

import com.talshavit.my_wishlist.Movie.Models.RootForSpecific;
import com.talshavit.my_wishlist.TvShow.Models.RootForSearch;
import com.talshavit.my_wishlist.TvShow.Models.RootForSpecificTv;
import com.talshavit.my_wishlist.TvShow.Models.RootForVideo;
import com.talshavit.my_wishlist.TvShow.Models.TvSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TvInterfaceService {

    @GET("search/tv")
    Call<TvSearchResponse> searchTv(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("sort_by") String sortBy,
        @Query("page") int page
    );

    @GET("tv/{series_id}")
    Call<RootForSpecificTv> getTvDetails(
            @Path("series_id") int series_id,
            @Query("api_key") String apiKey

    );

    @GET("tv/{series_id}/videos")
    Call<RootForVideo> getVideoDetails(
            @Path("series_id") int series_id,
            @Query("api_key") String apiKey

    );

}
