package com.talshavit.my_wishlist.TvShow;

import com.talshavit.my_wishlist.Media.MediaInfo;

import java.util.List;

public class TvShowInfo extends MediaInfo {

    public TvShowInfo() {
    }

    public TvShowInfo(int ID, String name, String imageUrl, String imageUrlBackground, String releaseYear, List<String> genres, String overview, String trailer, boolean isWatched, String lenght) {
        super(ID, name, imageUrl, imageUrlBackground, releaseYear, genres, overview, trailer, isWatched, lenght);
    }
}
