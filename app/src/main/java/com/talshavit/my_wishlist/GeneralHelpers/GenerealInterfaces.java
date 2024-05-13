package com.talshavit.my_wishlist.GeneralHelpers;

import com.talshavit.my_wishlist.Media.MediaInfo;

import java.util.List;

public interface GenerealInterfaces {
    void setWatched(boolean watched);

    int getMediaID();

    int getSerialID();

    MediaInfo setSerialID(int serialID);

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
