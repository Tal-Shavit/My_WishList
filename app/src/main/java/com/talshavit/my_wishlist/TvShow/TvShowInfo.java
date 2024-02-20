package com.talshavit.my_wishlist.TvShow;

import java.io.Serializable;
import java.util.List;

public class TvShowInfo implements Serializable  {

    private int serialID;
    private String userID;
    private int tvShowID;
    String tvShowName;
    String imageUrl;
    private String releaseYear;
    private int numOfSeasons;
    private List<String> genres;
    private String overview;
    private String trailer;

    private boolean isWatched;

    public TvShowInfo() {
    }

    public TvShowInfo(int tvShowID, String tvShowName, String imageUrl, String releaseYear, int numOfSeasons, List<String> genres, String overview, String trailer, boolean isWatched) {
        this.tvShowID = tvShowID;
        this.tvShowName = tvShowName;
        this.imageUrl = imageUrl;
        this.releaseYear = releaseYear;
        this.numOfSeasons = numOfSeasons;
        this.genres = genres;
        this.overview = overview;
        this.trailer = trailer;
        this.isWatched = isWatched;
    }

    public int getSerialID() {
        return serialID;
    }

    public TvShowInfo setSerialID(int serialID) {
        this.serialID = serialID;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public TvShowInfo setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public int getTvShowID() {
        return tvShowID;
    }

    public TvShowInfo setTvShowID(int tvShowID) {
        this.tvShowID = tvShowID;
        return this;
    }

    public String getTvShowName() {
        return tvShowName;
    }

    public TvShowInfo setTvShowName(String tvShowName) {
        this.tvShowName = tvShowName;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public TvShowInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public TvShowInfo setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public int getNumOfSeasons() {
        return numOfSeasons;
    }

    public TvShowInfo setNumOfSeasons(int numOfSeasons) {
        this.numOfSeasons = numOfSeasons;
        return this;
    }

    public List<String> getGenres() {
        return genres;
    }

    public TvShowInfo setGenres(List<String> genres) {
        this.genres = genres;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public TvShowInfo setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getTrailer() {
        return trailer;
    }

    public TvShowInfo setTrailer(String trailer) {
        this.trailer = trailer;
        return this;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public TvShowInfo setWatched(boolean watched) {
        isWatched = watched;
        return this;
    }
}
