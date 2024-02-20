package com.talshavit.my_wishlist.Movie;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talshavit.my_wishlist.Movie.MovieInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TmdbApiClient {

    private static final String API_KEY = "e7bc0f9166ef27fb13b4271519c0b354";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    public static String title;

    public static List<MovieInfo> getAllPopularMovies() throws Exception {
        List<MovieInfo> allMovieInfos = new ArrayList<>();
        int page = 1;  // Start with the first page

        while (true) {
            List<MovieInfo> movieInfos = getPopularMovies(page);

            if (movieInfos.isEmpty()) {
                // No more pages, break the loop
                break;
            }

            allMovieInfos.addAll(movieInfos);
            page++;

             //Add a condition to avoid making requests for a very large number of pages
            if (page > 1) {  // Adjust as needed
                break;
            }
        }

        return allMovieInfos;
    }

    public static List<MovieInfo> getPopularMovies(int page) throws Exception {
//        if (page < 1) {  // Adjust the maximum page number as needed
//            throw new IllegalArgumentException("Invalid page number");
//        }
        // || page > 200

        OkHttpClient client = new OkHttpClient();

        String endpoint = "/search/movie";
        String query = title;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8") + "&sort_by=popularity.desc&page=1";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseMovieInfo(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static List<MovieInfo> parseMovieInfo(String jsonResponse) {
        List<MovieInfo> movieInfoList = new ArrayList<>();

        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

            if (jsonObject.has("results")) {
            JsonArray results = jsonObject.getAsJsonArray("results");
            for (int i = 0; i < results.size(); i++) {

                JsonObject movieObject = results.get(i).getAsJsonObject();

                if (movieObject.has("title") && movieObject.has("release_date")) {
                    String movieName = movieObject.get("title").getAsString();
                    String releaseDate = movieObject.get("release_date").getAsString();
                    String releaseYear = releaseDate.substring(0,4);

                    // Get the image URL from the "poster_path" field
                    String imageUrl = movieObject.has("poster_path")
                            ? movieObject.get("poster_path").getAsString()
                            : null;

                    int movieId = movieObject.get("id").getAsInt();

                    int movieLenghtInMinutes = getMovieRuntime(movieId);
                    String hours = calcHours(movieLenghtInMinutes);
                    String minutes= calcMin(movieLenghtInMinutes);
                    String movieLenght = hours+"h "+minutes+"m";
                    List<String> genres = getMovieGenres(movieId);

                    String overview = movieObject.get("overview").getAsString();

                    String trailer = getMovieTrailerKey(movieId);

                    MovieInfo movieInfo = new MovieInfo(movieId,movieName,releaseYear,imageUrl,movieLenght,genres, overview,trailer, false);
                    movieInfoList.add(movieInfo);
                }
            }
            }
            else{
                Log.e("MovieName", "Results field not found in the JSON response");

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MovieName", "Exception in parseMovieNames: " + e.getMessage());
        }

        return movieInfoList;
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

    private static List<String> getMovieGenres(int movieId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/movie/" + movieId;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseMovieGenres(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static List<String> parseMovieGenres(String jsonResponse) {
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

    public static String getMovieTrailerKey(int movieId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/movie/" + movieId + "/videos";
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseMovieTrailerKey(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static String parseMovieTrailerKey(String jsonResponse) {
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


}
