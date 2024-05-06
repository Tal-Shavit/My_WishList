package com.talshavit.my_wishlist.GeneralHelpers;

import java.util.List;

public interface GenerealInterfaces {
    void setWatched(boolean watched);

    int getID();

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
