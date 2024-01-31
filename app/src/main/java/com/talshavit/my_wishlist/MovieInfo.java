package com.talshavit.my_wishlist;

import android.widget.VideoView;

import java.util.List;

public class MovieInfo {

    private int movieId;
    private String movieName;
    private String releaseYear;
    private String imageUrl;
    private String movieLenght;
    private List<String> genres;
    private String overview;
    private String trailer;

    public MovieInfo(){

    }


    public MovieInfo(int movieId, String movieName, String releaseYear, String imageUrl, String movieLenght, List<String> genres,
                        String overview, String trailer) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.releaseYear = releaseYear;
        this.imageUrl = imageUrl;
        this.movieLenght = movieLenght;
        this.genres = genres;
        this.overview = overview;
        this.trailer = trailer;
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

    public int getMovieId() {
        return movieId;
    }

    public MovieInfo setMovieId(int movieId) {
        this.movieId = movieId;
        return this;
    }

}
