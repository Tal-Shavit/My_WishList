package com.talshavit.my_wishlist.Movie.ModelsApi;

import com.talshavit.my_wishlist.HelpersForApi.Genre;
import com.talshavit.my_wishlist.HelpersForApi.ProductionCompany;
import com.talshavit.my_wishlist.HelpersForApi.ProductionCountry;
import com.talshavit.my_wishlist.HelpersForApi.SpokenLanguage;

import java.util.ArrayList;

public class RootForSpecific {
    public boolean adult;
    public String backdrop_path;
    public Object belongs_to_collection;
    public int budget;
    public ArrayList<Genre> genres;
    public String homepage;
    public int id;
    public String imdb_id;
    public String original_language;
    public String original_title;
    public String overview;
    public double popularity;
    public String poster_path;
    public ArrayList<ProductionCompany> production_companies;
    public ArrayList<ProductionCountry> production_countries;
    public String release_date;
    public long revenue;
    public int runtime;
    public ArrayList<SpokenLanguage> spoken_languages;
    public String status;
    public String tagline;
    public String title;
    public boolean video;
    public double vote_average;
    public int vote_count;
}
