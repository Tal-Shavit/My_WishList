package com.talshavit.my_wishlist.GeneralHelpers;

import android.util.Log;

import com.airbnb.lottie.L;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TmdbApiClientGeneral<T> {
    private static final String API_KEY = "e7bc0f9166ef27fb13b4271519c0b354";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    public static String title;
    public String itemType;


    public List<T> getAllPopularMovies(String itemType) throws Exception {
        this.itemType = itemType;
        List<T> allItems = new ArrayList<>();
        int page = 1;  // Start with the first page

        while (true) {
            List<T> items = getPopularItem(page, itemType);

            if (allItems.isEmpty()) {
                //No more pages, break the loop
                break;
            }

            allItems.addAll(items);
            page++;

            //Add a condition to avoid making requests for a very large number of pages
            if (page > 1) {  //Adjust as needed
                break;
            }
        }
        return allItems;
    }

    public List<T> getPopularItem(int page, String itemType) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/search/" + itemType.toLowerCase();
        String query = title;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8") + "&sort_by=popularity.desc&page=1";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseItemInfo(jsonResponse, itemType);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private List<T> parseItemInfo(String jsonResponse, String itemType) {
        List<T> itemList = new ArrayList<>();

        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

            if (jsonObject.has("results")) {
                JsonArray results = jsonObject.getAsJsonArray("results");
                for (int i = 0; i < results.size(); i++) {
                    JsonObject itemObject = results.get(i).getAsJsonObject();

                    int itemID = itemObject.get("id").getAsInt();
                    String itemName;
                    String releaseYear;
                    String itemLenght;

                    //Get the image URL from the "poster_path" field
                    String imageUrl = itemObject.has("poster_path")
                            ? itemObject.get("poster_path").getAsString()
                            : null;

                    List<String> genres = getItemGenres(itemID, itemType);
                    String overview= itemObject.get("overview").getAsString();
                    //String trailer = getTrailerKey(itemID);
                    String trailer = getYouTubeVideoKey(itemID);


                    if(itemType.equals("movie")) {
                        if (itemObject.has("title") && itemObject.has("release_date")) {
                            itemName = itemObject.get("title").getAsString();
                            String releaseDate = itemObject.get("release_date").getAsString();
                            releaseYear = releaseDate.substring(0, 4);

                            int movieLenghtInMinutes = getMovieRuntime(itemID);
                            String hours = calcHours(movieLenghtInMinutes);
                            String minutes = calcMin(movieLenghtInMinutes);
                            itemLenght = hours + "h " + minutes + "m";

                            MovieInfo movieInfo = new MovieInfo(itemID,itemName,releaseYear,imageUrl,itemLenght,genres, overview,trailer, false);
                            itemList.add((T) movieInfo);
                        }
                    } else {
                        if (itemObject.has("name")) {
                            itemName = itemObject.get("name").getAsString();
                            String firstAirDate = itemObject.get("first_air_date").getAsString();
                            releaseYear = firstAirDate.substring(0, 4);

                            int numberOfSeasons = getNumberOfSeasons(itemID);
                            String seasons;
                            if(numberOfSeasons > 1)
                                seasons = numberOfSeasons+" seasons";
                            else{
                                seasons = numberOfSeasons+" season";
                            }
                            TvShowInfo tvShowInfo = new TvShowInfo(itemID, itemName, imageUrl, releaseYear, seasons ,genres,overview, trailer, false);
                            itemList.add((T) tvShowInfo);
                        }

                    }
                }
            } else{
                Log.e("ItemName", "Results field not found in the JSON response");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ItemName", "Exception in parseMovieNames: " + e.getMessage());
        }

        return itemList;
    }

    private static String calcMin(int movieLenght) {
        return movieLenght%60+"";
    }

    private static String calcHours(int movieLenght) {
        return movieLenght/60+"";
    }

    private static int getMovieRuntime(int movieId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/movie/" + movieId;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseMovieRuntime(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static int parseMovieRuntime(String jsonResponse) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        if (jsonObject.has("runtime")) {
            return jsonObject.get("runtime").getAsInt();
        } else {
            return 0; // Default value if runtime is not available
        }
    }

    private List<String> getItemGenres(int itemID, String itemType) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/"+itemType.toLowerCase()+"/" + itemID;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseItemGenres(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private List<String> parseItemGenres(String jsonResponse) {
        List<String> genresList = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        if (jsonObject.has("genres")) {
            JsonArray genresArray = jsonObject.getAsJsonArray("genres");
            for (int i = 0; i < genresArray.size(); i++) {
                JsonObject genreObject = genresArray.get(i).getAsJsonObject();
                if (genreObject.has("name")) {
                    genresList.add(genreObject.get("name").getAsString());
                }
            }
        }
        return genresList;
    }

    public String getTrailerKey(int itemID) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/"+itemType.toLowerCase()+"/" + itemID + "/videos";
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseTrailerKey(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static String parseTrailerKey(String jsonResponse) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        JsonArray results = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < results.size(); i++) {
            JsonObject videoObject = results.get(i).getAsJsonObject();

            // Check if the video type is a trailer
            if (videoObject.has("type") && "Trailer".equals(videoObject.get("type").getAsString())) {
                // Return the key of the first trailer found
                if (videoObject.has("key")) {
                    return videoObject.get("key").getAsString();
                }
            }
        }

        return null;
    }

    private static int getNumberOfSeasons(int tvShowId) {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/tv/" + tvShowId;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseNumberOfSeasons(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // Default value if an error occurs
        }
    }

    private static int parseNumberOfSeasons(String jsonResponse) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        if (jsonObject.has("number_of_seasons")) {
            return jsonObject.get("number_of_seasons").getAsInt();
        } else if (jsonObject.has("seasons")) {
            //If "number_of_seasons" is not directly available, try to get it from the "seasons" array
            JsonArray seasonsArray = jsonObject.getAsJsonArray("seasons");
            return seasonsArray.size();
        } else {
            return 0; // Default value if the number of seasons is not available
        }
    }


    public String getYouTubeVideoKey(int itemID) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/" + itemType.toLowerCase() + "/" + itemID + "/videos";
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseYouTubeVideoKey(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private String parseYouTubeVideoKey(String jsonResponse) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        JsonArray results = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < results.size(); i++) {
            JsonObject videoObject = results.get(i).getAsJsonObject();

            // Check if the video is from YouTube
            if (videoObject.has("site") && "YouTube".equals(videoObject.get("site").getAsString())) {
                // Return the key of the first YouTube video found
                if (videoObject.has("key")) {
                    return videoObject.get("key").getAsString();
                }
            }
        }

        return null;
    }



}
