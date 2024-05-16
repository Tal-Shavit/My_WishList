package com.talshavit.my_wishlist.Media;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.GeneralHelpers.GenerealInterfaces;
import com.talshavit.my_wishlist.GeneralHelpers.TrailerCallback;
import com.talshavit.my_wishlist.HelpersForApi.ResultForVideo;
import com.talshavit.my_wishlist.HelpersForApi.RootForVideo;
import com.talshavit.my_wishlist.Movie.Interfaces.MovieApiService;
import com.talshavit.my_wishlist.Movie.ModelsApi.MovieSearchResponse;
import com.talshavit.my_wishlist.Movie.ModelsApi.RootForSearchMovie;
import com.talshavit.my_wishlist.Movie.ModelsApi.RootForSpecific;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.Interfaces.TvInterfaceService;
import com.talshavit.my_wishlist.TvShow.ModelsApi.RootForSearch;
import com.talshavit.my_wishlist.TvShow.ModelsApi.RootForSpecificTv;
import com.talshavit.my_wishlist.TvShow.ModelsApi.TvSearchResponse;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMediaFragment<T extends GenerealInterfaces> extends Fragment implements TrailerCallback {
    private DatabaseReference databaseReference;
    private TextView titleTvOrMovie;
    private EditText titleEditText;
    private ImageButton titleButton, exitButton;
    private ImageView imageView;
    private Button addButton;
    private Spinner dynamicSpinner;
    private ArrayAdapter<String> adapter;
    private MediaType mediaType;
    private String titleName, imageUrl, imgBackg, releaseYear, overview, trailer, lenght, userID;
    private int mediaID;
    private List<String> genres;
    private static int firstID = 0;
    private ProgressDialog progressDialog;
    private TvInterfaceService tvInterfaceService;
    private MovieApiService movieApiService;
    private FirebaseAnalytics firebaseAnalytics;

    public AddMediaFragment(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_media, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        initRetrofit();
        findViews(view);
        initView();
    }

    public void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        initApi(retrofit);
    }

    private void initApi(Retrofit retrofit) {
        if (mediaType == MediaType.MOVIES)
            movieApiService = retrofit.create(MovieApiService.class);
        else if (mediaType == MediaType.TV_SHOWS)
            tvInterfaceService = retrofit.create(TvInterfaceService.class);
    }

    public void findViews(View view) {
        titleTvOrMovie = view.findViewById(R.id.titleTvOrMovie);
        titleEditText = view.findViewById(R.id.titleEditText);
        titleButton = view.findViewById(R.id.titleButton);
        dynamicSpinner = view.findViewById(R.id.dynamicSpinner);
        imageView = view.findViewById(R.id.imageView);
        addButton = view.findViewById(R.id.addButton);
        exitButton = view.findViewById(R.id.exitButton);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);
    }

    public void initView() {
        if (mediaType == MediaType.MOVIES)
            titleTvOrMovie.setText("ADD A MOVIE!");
        onTitleButtonClick();
        onAddButtonClick();
        onBackButtonClick();
    }

    private void onBackButtonClick() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void onTitleButtonClick() {
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if (!title.equals("")) {
                    dynamicSpinner.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    initProgressDialog();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    searchMedia(title, mediaType);
                } else
                    Toast.makeText(getContext(), "YOU HAVE TO FILL THE TITLE!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMedia(String title, MediaType mediaType) {
        if (mediaType == MediaType.MOVIES) {
            movieApiService.searchMovies("e7bc0f9166ef27fb13b4271519c0b354", title, "popularity.desc", 1).enqueue(new Callback<MovieSearchResponse>() {
                @Override
                public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                    handleSearchResponse(response);
                }

                @Override
                public void onFailure(Call<MovieSearchResponse> call, Throwable throwable) {
                }
            });
        } else if (mediaType == MediaType.TV_SHOWS) {
            tvInterfaceService.searchTv("e7bc0f9166ef27fb13b4271519c0b354", title, "popularity.desc", 1).enqueue(new Callback<TvSearchResponse>() {
                @Override
                public void onResponse(Call<TvSearchResponse> call, Response<TvSearchResponse> response) {
                    handleSearchResponse(response);
                }

                @Override
                public void onFailure(Call<TvSearchResponse> call, Throwable throwable) {
                }
            });
        }
    }

    private void handleSearchResponse(Response<?> response) {
        progressDialog.dismiss();

        if (response.isSuccessful() && response.body() != null) {
            if (response.body() instanceof MovieSearchResponse) {
                ArrayList<RootForSearchMovie> movieInfos = ((MovieSearchResponse) response.body()).results;
                handleMovieSearchResult(movieInfos);
            } else if (response.body() instanceof TvSearchResponse) {
                ArrayList<RootForSearch> tvShowInfos = ((TvSearchResponse) response.body()).results;
                handleTvSearchResult(tvShowInfos);
            }
        } else {
            Toast.makeText(getContext(), "No matches found!", Toast.LENGTH_SHORT).show();
            titleEditText.setText("");
        }
    }

    private void handleTvSearchResult(ArrayList<RootForSearch> tvShowInfos) {
        if (tvShowInfos != null && !tvShowInfos.isEmpty()) {
            for (RootForSearch tvShowInfo : tvShowInfos) {
                adapter.add(tvShowInfo.name);
            }
            adapter.notifyDataSetChanged();//Notify adapter after adding all items

            dynamicSpinner.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mediaID = tvShowInfos.get(position).id;
                    getDetailsTv(mediaID);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    private void handleMovieSearchResult(ArrayList<RootForSearchMovie> movieInfos) {
        if (movieInfos != null && !movieInfos.isEmpty()) {
            for (RootForSearchMovie movieInfo : movieInfos) {
                String title = movieInfo.title;
                if (movieInfo.release_date != null && !movieInfo.release_date.isEmpty())
                    title += " (" + movieInfo.release_date.substring(0, 4) + ")";
                adapter.add(title);
            }
            adapter.notifyDataSetChanged();  // Notify adapter after adding all items

            dynamicSpinner.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mediaID = movieInfos.get(position).id;
                    getDetailsMovies(mediaID);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void getDetailsMovies(int itemID) {
        movieApiService.getMovieDetails(itemID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForSpecific>() {
            @Override
            public void onResponse(Call<RootForSpecific> call, Response<RootForSpecific> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<String> genresStringList = new ArrayList<String>();
                    RootForSpecific specificMovie = response.body();
                    Picasso.get().load("https://image.tmdb.org/t/p/w500/" + specificMovie.poster_path).into(imageView);
                    addButton.setVisibility(View.VISIBLE);
                    titleName = specificMovie.title;
                    setYear(specificMovie);
                    setImgMovie(specificMovie);
                    setMovieLen(specificMovie);
                    setGenres(specificMovie, genresStringList);
                    overview = specificMovie.overview;
                    getTrailerKey(itemID, mediaType);
                }
            }

            @Override
            public void onFailure(Call<RootForSpecific> call, Throwable throwable) {

            }
        });
    }

    private void getDetailsTv(int itemID) {
        tvInterfaceService.getTvDetails(itemID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForSpecificTv>() {
            @Override
            public void onResponse(Call<RootForSpecificTv> call, Response<RootForSpecificTv> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> genresStringList = new ArrayList<String>();
                    RootForSpecificTv specificTv = response.body();
                    Picasso.get().load("https://image.tmdb.org/t/p/w500/" + specificTv.poster_path).into(imageView);
                    addButton.setVisibility(View.VISIBLE);
                    titleName = specificTv.name;
                    setNumOfSeasons(specificTv);
                    setImgTv(specificTv);
                    setYear(specificTv);
                    setGenres(specificTv, genresStringList);
                    overview = specificTv.overview;
                    getTrailerKey(itemID, mediaType);
                }
            }

            @Override
            public void onFailure(Call<RootForSpecificTv> call, Throwable throwable) {
            }
        });
    }

    private void getTrailerKey(int itemID, MediaType mediaType) {
        Callback<RootForVideo> callback = new Callback<RootForVideo>() {
            @Override
            public void onResponse(Call<RootForVideo> call, Response<RootForVideo> response) {
                handleTrailerResponse(response);
            }

            @Override
            public void onFailure(Call<RootForVideo> call, Throwable throwable) {

            }
        };

        if (mediaType == MediaType.MOVIES) {
            movieApiService.getVideoDetails(itemID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(callback);
        } else if (mediaType == MediaType.TV_SHOWS) {
            tvInterfaceService.getVideoDetails(itemID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(callback);
        }
    }

    private void handleTrailerResponse(Response<RootForVideo> response) {
        String trailerKey = null;
        String clipKey = null;

        for (int i = 0; i < response.body().results.size(); i++) {
            ResultForVideo details = response.body().results.get(i);
            if (details.type.toLowerCase().equals("trailer")) {
                trailerKey = details.key;
                break;
            } else if (details.type.toLowerCase().equals("clip") && clipKey == null) {
                //Store the first clip
                clipKey = details.key;
            }
        }

        //Use trailer if available, otherwise use the clip
        if (trailerKey != null) {
            trailer = onTrailerLoaded(trailerKey);
        } else if (clipKey != null) {
            trailer = onTrailerLoaded(clipKey);
        }
    }

    private void setMovieLen(RootForSpecific specificMovie) {
        int movieLenghtInMinutes = specificMovie.runtime;
        String hours = calcHours(movieLenghtInMinutes);
        String minutes = calcMin(movieLenghtInMinutes);
        lenght = hours + "h " + minutes + "m";
    }

    private static String calcMin(int movieLenght) {
        return movieLenght % 60 + "";
    }

    private static String calcHours(int movieLenght) {
        return movieLenght / 60 + "";
    }

    private void setImages(String posterPath, String backdropPath) {
        imageUrl = posterPath;
        imgBackg = (backdropPath == null || backdropPath.isEmpty()) ? posterPath : backdropPath;
    }

    private void setImgMovie(RootForSpecific specificMovie) {
        setImages(specificMovie.poster_path, specificMovie.backdrop_path);
    }

    private void setImgTv(RootForSpecificTv specificTv) {
        setImages(specificTv.poster_path, specificTv.backdrop_path);
    }

    private void setYear(RootForSpecific specificMovie) {
        if (specificMovie.release_date == null || specificMovie.release_date.isEmpty())
            releaseYear = "";
        else
            releaseYear = specificMovie.release_date.substring(0, 4);
    }

    private void setYear(RootForSpecificTv specificTv) {
        if (specificTv.first_air_date == null || specificTv.first_air_date.isEmpty())
            releaseYear = "";
        else
            releaseYear = specificTv.first_air_date.substring(0, 4);
    }

    private void setGenres(RootForSpecific specificMovie, List<String> genresStringList) {
        for (int i = 0; i < specificMovie.genres.size(); i++) {
            genresStringList.add(specificMovie.genres.get(i).name);
        }
        genres = genresStringList;
    }

    private void setGenres(RootForSpecificTv specificTv, List<String> genresStringList) {
        for (int i = 0; i < specificTv.genres.size(); i++) {
            genresStringList.add(specificTv.genres.get(i).name);
        }
        genres = genresStringList;
    }


    private void setNumOfSeasons(RootForSpecificTv specificTv) {
        int seasons = specificTv.number_of_seasons;
        if (seasons > 1)
            lenght = seasons + " seasons";
        else {
            lenght = seasons + " season";
        }
    }

    private void onAddButtonClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //analyticsFirebase();
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String childPath = mediaType == MediaType.MOVIES ? "movies" : "tv shows";
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(childPath);
                checkIfExist();
            }
        });
    }

    private void checkIfExist() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isExist = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (mediaType == MediaType.MOVIES) {
                        MovieInfo movieInfo = dataSnapshot.getValue(MovieInfo.class);
                        if (movieInfo.getMediaID() == mediaID) {
                            String txt = "The movie " + movieInfo.getName() + " is already in your list!";
                            Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
                            isExist = true;
                            break; //Exit loop after removing the reference
                        }
                    } else if (mediaType == MediaType.TV_SHOWS) {
                        TvShowInfo tvShowInfo = dataSnapshot.getValue(TvShowInfo.class);
                        if (tvShowInfo.getMediaID() == mediaID) {
                            String txt = "The tv show " + tvShowInfo.getName() + " is already in your list!";
                            Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
                            isExist = true;
                            break; //Exit loop after removing the reference
                        }
                    }
                }
                if (!isExist)
                    shiftSerialIDs();
                else {
                    if (mediaType == MediaType.MOVIES)
                        replaceFragment(new MediaFragment<MovieInfo>(mediaType, MovieInfo.class));
                    else if (mediaType == MediaType.TV_SHOWS)
                        replaceFragment(new MediaFragment<TvShowInfo>(mediaType, TvShowInfo.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void shiftSerialIDs() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (mediaType == MediaType.MOVIES) {
                        MovieInfo movie = snapshot.getValue(MovieInfo.class);
                        if (movie != null) {
                            int currentSerialId = movie.getSerialID();
                            movie.setSerialID(currentSerialId + 1);
                            databaseReference.child(movie.getSerialID() + "").setValue(movie);
                        }
                    } else if (mediaType == MediaType.TV_SHOWS) {
                        TvShowInfo tvShow = snapshot.getValue(TvShowInfo.class);
                        if (tvShow != null) {
                            int currentSerialId = tvShow.getSerialID();
                            tvShow.setSerialID(currentSerialId + 1);
                            databaseReference.child(tvShow.getSerialID() + "").setValue(tvShow);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (mediaType == MediaType.MOVIES)
            createMovie(userID);
        else if (mediaType == MediaType.TV_SHOWS)
            createTv(userID);
    }

    private void createTv(String userID) {
        TvShowInfo tvShowInfo = new TvShowInfo(mediaID, titleName, imageUrl, imgBackg, releaseYear, genres, overview, trailer, false, lenght);
        tvShowInfo.setUserID(userID);
        tvShowInfo.setSerialID(firstID);
        databaseReference.child(firstID + "").setValue(tvShowInfo);
        replaceFragment(new MediaFragment<TvShowInfo>(mediaType, TvShowInfo.class));
    }

    private void createMovie(String userID) {
        MovieInfo movieInfo = new MovieInfo(mediaID, titleName, imageUrl, imgBackg, releaseYear, genres, overview, trailer, false, lenght);
        movieInfo.setUserID(userID);
        movieInfo.setSerialID(firstID);
        databaseReference.child(firstID + "").setValue(movieInfo);
        replaceFragment(new MediaFragment<MovieInfo>(mediaType, MovieInfo.class));
    }

    private void analyticsFirebase() {
        //Log event for add button click
        Bundle params = new Bundle();
        params.putString("button_clicked", "add_button_tv_show_fragment");
        firebaseAnalytics.logEvent("add_button_clicked", params);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();
    }

    @Override
    public String onTrailerLoaded(String trailerKey) {
        return trailerKey;
    }
}
