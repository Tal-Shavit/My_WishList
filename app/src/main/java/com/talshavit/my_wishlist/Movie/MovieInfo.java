package com.talshavit.my_wishlist.Movie;

import com.talshavit.my_wishlist.Media.MediaInfo;

import java.util.List;

public class MovieInfo extends MediaInfo {

    public MovieInfo() {
    }

    public MovieInfo(int ID, String name, String imageUrl, String imageUrlBackground, String releaseYear, List<String> genres, String overview, String trailer, boolean isWatched, String lenght) {
        super(ID, name, imageUrl, imageUrlBackground, releaseYear, genres, overview, trailer, isWatched, lenght);
    }
}
