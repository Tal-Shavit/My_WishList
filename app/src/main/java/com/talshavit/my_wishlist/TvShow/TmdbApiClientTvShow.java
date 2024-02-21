package com.talshavit.my_wishlist.TvShow;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TmdbApiClientTvShow {

    private static final String API_KEY = "e7bc0f9166ef27fb13b4271519c0b354";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    public static String title;

    public static List<TvShowInfo> getAllPopularTvShows() throws Exception {
        List<TvShowInfo> allTvShowInfos = new ArrayList<>();
        int page = 1;  // Start with the first page

        while (true) {
            List<TvShowInfo> tvShowInfos = getPopularTvShows(page);

            if (tvShowInfos.isEmpty()) {
                // No more pages, break the loop
                break;
            }

            allTvShowInfos.addAll(tvShowInfos);
            page++;

            // Add a condition to avoid making requests for a very large number of pages
            if (page > 1) {  // Adjust as needed
                break;
            }
        }

        return allTvShowInfos;
    }

    public static List<TvShowInfo> getPopularTvShows(int page) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/search/tv";
        String query = title;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY + "&query=" + URLEncoder.encode(query, "UTF-8") + "&sort_by=popularity.desc&page=1";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseTvShowInfo(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static List<TvShowInfo> parseTvShowInfo(String jsonResponse) {
        List<TvShowInfo> tvShowInfoList = new ArrayList<>();

        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

            if (jsonObject.has("results")) {
                JsonArray results = jsonObject.getAsJsonArray("results");
                for (int i = 0; i < results.size(); i++) {
                    JsonObject tvShowObject = results.get(i).getAsJsonObject();

                    if (tvShowObject.has("name")) {
                        String tvShowName = tvShowObject.get("name").getAsString();

                        // Get the image URL from the "poster_path" field
                        String imageUrl = tvShowObject.has("poster_path")
                                ? tvShowObject.get("poster_path").getAsString()
                                : null;

                        String firstAirDate = tvShowObject.get("first_air_date").getAsString();
                        String releaseYear = firstAirDate.substring(0, 4);


                        int tvShowId = tvShowObject.get("id").getAsInt();
                        int numberOfSeasons = getNumberOfSeasons(tvShowId);
                        //int numberOfSeasons = getNumberOfSeasons(tvShowId, jsonResponse);  // Pass the JSON response
                        List<String> genres = getTvShowGenres(tvShowId);
                        String overview = tvShowObject.get("overview").getAsString();

                        String trailerKey = getTvShowTrailerKey(tvShowId);


                        TvShowInfo tvShowInfo = new TvShowInfo(tvShowId, tvShowName, imageUrl, releaseYear, numberOfSeasons ,genres,overview, trailerKey, false);
                        tvShowInfoList.add(tvShowInfo);
                    }
                }
            } else {
                Log.e("TvShowName", "Results field not found in the JSON response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TvShowName", "Exception in parseTvShowInfo: " + e.getMessage());
        }

        return tvShowInfoList;
    }

    private static String getTvShowTrailerKey(int tvShowId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/tv/" + tvShowId + "/videos";
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseTvShowTrailerKey(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static String parseTvShowTrailerKey(String jsonResponse) {
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

    private static List<String> getTvShowGenres(int tvShowId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String endpoint = "/tv/" + tvShowId;
        String url = BASE_URL + endpoint + "?api_key=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return parseTvShowGenres(jsonResponse);
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    private static List<String> parseTvShowGenres(String jsonResponse) {
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
            throw new RuntimeException(e);
        }
    }

    private static int parseNumberOfSeasons(String jsonResponse) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonResponse).getAsJsonObject();

        if (jsonObject.has("number_of_seasons")) {
            return jsonObject.get("number_of_seasons").getAsInt();
        } else {
            return 0; //Default value if the number of seasons is not available
        }
    }

}