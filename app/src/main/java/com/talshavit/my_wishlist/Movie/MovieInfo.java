package com.talshavit.my_wishlist.Movie;

import com.talshavit.my_wishlist.GeneralHelpers.GenerealInterfaces;

import java.io.Serializable;
import java.util.List;

public class MovieInfo implements Serializable , GenerealInterfaces{
    private int serialID;
    private String userID;
    private int movieID;
    private String movieName;
    private String releaseYear;
    private String imageUrl;
    private String movieLenght;
    private List<String> genres;
    private String overview;
    private String trailer;
    private boolean isWatched;

    public MovieInfo() {
    }

    public MovieInfo(int movieID, String movieName, String releaseYear, String imageUrl, String movieLenght, List<String> genres,
                        String overview, String trailer, boolean isWatched) {
        this.movieID = movieID;
        this.movieName = movieName;
        this.releaseYear = releaseYear;
        this.imageUrl = imageUrl;
        this.movieLenght = movieLenght;
        this.genres = genres;
        this.overview = overview;
        this.trailer = trailer;
        this.isWatched = isWatched;
    }

    public String getMovieName() {
        return movieName;
    }

    public MovieInfo setMovieName(String movieName) {
        this.movieName = movieName;
        return this;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public MovieInfo setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MovieInfo setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getMovieLenght() {
        return movieLenght;
    }

    public MovieInfo setMovieLenght(String movieLenght) {
        this.movieLenght = movieLenght;
        return this;
    }

    public List<String> getGenres() {
        return genres;
    }

    public MovieInfo setGenres(List<String> genres) {
        this.genres = genres;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MovieInfo setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getTrailer() {
        return trailer;
    }

    public MovieInfo setTrailer(String trailer) {
        this.trailer = trailer;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public MovieInfo setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public int getMovieID() {
        return movieID;
    }

    public MovieInfo setMovieID(int movieID) {
        this.movieID = movieID;
        return this;
    }

    public int getSerialID() {
        return serialID;
    }

    public MovieInfo setSerialID(int serialID) {
        this.serialID = serialID;
        return this;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
        //return this;
    }

    @Override
    public int getID() {
        return movieID;
    }

    @Override
    public String getName() {
        return movieName;
    }

    @Override
    public String getLenght() {
        return movieLenght;
    }


}
