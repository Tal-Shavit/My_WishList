package com.talshavit.my_wishlist.GeneralHelpers;

import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.List;

public interface GenerealInterfaces {
    void setWatched(boolean watched);

    int getID();

    int getSerialID();

    String getName();

    boolean isWatched();

    String getImageUrl();

    String getImageUrlBackground();

    String getLenght();

    String getReleaseYear();

    String getOverview();

    List<String> getGenres();

    String getTrailer();
}
