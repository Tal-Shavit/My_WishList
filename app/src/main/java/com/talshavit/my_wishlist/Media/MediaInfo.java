package com.talshavit.my_wishlist.Media;

import com.talshavit.my_wishlist.GeneralHelpers.GenerealInterfaces;

import java.io.Serializable;
import java.util.List;

public class MediaInfo implements Serializable, GenerealInterfaces {
    private int serialID;
    private String userID;
    private int mediaID;
    private String name;
    private String imageUrl;
    private String imageUrlBackground;
    private String releaseYear;
    private List<String> genres;
    private String overview;
    private String trailer;
    private boolean isWatched;
    private String lenght;

    public MediaInfo() {
    }

    public MediaInfo(int mediaID, String name, String imageUrl, String imageUrlBackground, String releaseYear, List<String> genres, String overview, String trailer, boolean isWatched, String lenght) {
        this.mediaID = mediaID;
        this.name = name;
        this.imageUrl = imageUrl;
        this.imageUrlBackground = imageUrlBackground;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.overview = overview;
        this.trailer = trailer;
        this.isWatched = isWatched;
        this.lenght = lenght;
    }

    @Override
    public int getSerialID() {
        return serialID;
    }

    public MediaInfo setSerialID(int serialID) {
        this.serialID = serialID;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public MediaInfo setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    @Override
    public int getMediaID() {
        return mediaID;
    }

    public MediaInfo setMediaID(int mediaID) {
        this.mediaID = mediaID;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public MediaInfo setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public MediaInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public String getImageUrlBackground() {
        return imageUrlBackground;
    }

    public MediaInfo setImageUrlBackground(String imageUrlBackground) {
        this.imageUrlBackground = imageUrlBackground;
        return this;
    }

    @Override
    public String getReleaseYear() {
        return releaseYear;
    }

    public MediaInfo setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    @Override
    public List<String> getGenres() {
        return genres;
    }

    public MediaInfo setGenres(List<String> genres) {
        this.genres = genres;
        return this;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    public MediaInfo setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    @Override
    public String getTrailer() {
        return trailer;
    }

    public MediaInfo setTrailer(String trailer) {
        this.trailer = trailer;
        return this;
    }

    @Override
    public boolean isWatched() {
        return isWatched;
    }

    @Override
    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    @Override
    public String getLenght() {
        return lenght;
    }

    public MediaInfo setLenght(String lenght) {
        this.lenght = lenght;
        return this;
    }
}
