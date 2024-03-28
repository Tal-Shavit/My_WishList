package com.talshavit.my_wishlist.Movie;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.Interfaces.MovieApiService;
import com.talshavit.my_wishlist.Movie.Interfaces.TrailerCallback;
import com.talshavit.my_wishlist.Movie.Models.MovieSearchResponse;
import com.talshavit.my_wishlist.Movie.Models.ResultForVideo;
import com.talshavit.my_wishlist.Movie.Models.RootForSearch;
import com.talshavit.my_wishlist.Movie.Models.RootForSpecific;
import com.talshavit.my_wishlist.Movie.Models.RootForVideo;
import com.talshavit.my_wishlist.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddMovieFragment extends Fragment implements TrailerCallback {

    private DatabaseReference databaseReference;
    private EditText titleEditText;
    private ImageButton titleButton;
    private Spinner dynamicSpinner;
    private ArrayAdapter<String> adapter;
    private ImageView movieImageView;
    private Button addButton;
    private String titleNameMovie;
    private String releaseYearMovie;
    private String imgMovie;
    private int movieID;
    private String movieLenght;
    private List<String> genresList;
    private String overview;
    private String trailer;
    private static int nextID;
    private MovieApiService movieApiService;
    private ProgressDialog progressDialog;

    public AddMovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_movie, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRetrofit();
        findViews(view);
        initView();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieApiService = retrofit.create(MovieApiService.class);
    }

    private void initView() {
        checkLastSerialNumber();
        onTitleButtonClick();
        onAddButtonClick();
    }

    private void getMovieDetails(int id) {
        movieApiService.getMovieDetails(id, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForSpecific>() {
            @Override
            public void onResponse(Call<RootForSpecific> call, Response<RootForSpecific> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<String> genresStringList = new ArrayList<String>();
                    RootForSpecific specificMovie = response.body();
                    Picasso.get().load("https://image.tmdb.org/t/p/w500/" + specificMovie.poster_path).into(movieImageView);
                    addButton.setVisibility(View.VISIBLE);
                    titleNameMovie = specificMovie.title;
                    String releaseYear = specificMovie.release_date.substring(0, 4);
                    if (releaseYear != null)
                        releaseYearMovie = releaseYear;
                    else
                        releaseYearMovie = "";
                    imgMovie = specificMovie.poster_path;
                    int movieLenghtInMinutes = specificMovie.runtime;
                    String hours = calcHours(movieLenghtInMinutes);
                    String minutes = calcMin(movieLenghtInMinutes);
                    movieLenght = hours + "h " + minutes + "m";
                    for (int i = 0; i < specificMovie.genres.size(); i++) {
                        genresStringList.add(specificMovie.genres.get(i).name);
                    }
                    genresList = genresStringList;
                    overview = specificMovie.overview;
                    getMovieTrailerKey(id);
                }
            }

            @Override
            public void onFailure(Call<RootForSpecific> call, Throwable throwable) {

            }
        });
    }

    private static String calcMin(int movieLenght) {
        return movieLenght % 60 + "";
    }

    private static String calcHours(int movieLenght) {
        return movieLenght / 60 + "";
    }

    private void checkLastSerialNumber() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
        databaseReference.orderByChild("serialID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MovieInfo lastAddedMovie = snapshot.getValue(MovieInfo.class);
                    if (lastAddedMovie != null) {
                        nextID = lastAddedMovie.getSerialID() + 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void findViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        titleButton = view.findViewById(R.id.titleButton);
        dynamicSpinner = view.findViewById(R.id.dynamicSpinner);
        movieImageView = view.findViewById(R.id.movieImageView);
        addButton = view.findViewById(R.id.addButton);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);
    }

    private void getMovieTrailerKey(int id) {
        movieApiService.getVideoDetails(id, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForVideo>() {
            @Override
            public void onResponse(Call<RootForVideo> call, Response<RootForVideo> response) {
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

            @Override
            public void onFailure(Call<RootForVideo> call, Throwable throwable) {

            }
        });
    }

    private void onTitleButtonClick(){
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if (!title.equals("")) {
                    dynamicSpinner.setVisibility(View.INVISIBLE);
                    movieImageView.setVisibility(View.INVISIBLE);

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    getAllSearchMovies(title);
                } else
                    Toast.makeText(getContext(), "YOU HAVE TO FILL THE TITLE!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAllSearchMovies(String title) {
        movieApiService.searchMovies("e7bc0f9166ef27fb13b4271519c0b354", title, "popularity.desc", 1).enqueue(new Callback<MovieSearchResponse>() {
            @Override
            public void onResponse(Call<MovieSearchResponse> call, Response<MovieSearchResponse> response) {
                progressDialog.dismiss();

                ArrayList<RootForSearch> movieInfos = response.body().results;
                if (movieInfos != null && !movieInfos.isEmpty()) {
                    for (RootForSearch movieInfo : movieInfos) {
                        if (movieInfo.release_date != null && !movieInfo.release_date.isEmpty())
                            adapter.add(movieInfo.title + " (" + movieInfo.release_date.substring(0, 4) + ")");
                        else {
                            adapter.add(movieInfo.title);
                        }
                    }
                    adapter.notifyDataSetChanged();  // Notify adapter after adding all items

                    dynamicSpinner.setVisibility(View.VISIBLE);
                    movieImageView.setVisibility(View.VISIBLE);

                    dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            movieID = movieInfos.get(position).id;
                            getMovieDetails(movieID);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No matches found!", Toast.LENGTH_SHORT).show();
                    titleEditText.setText("");
                }
            }

            @Override
            public void onFailure(Call<MovieSearchResponse> call, Throwable throwable) {
                Log.d("lala", "no");
            }
        });
    }

    private void onAddButtonClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");

                MovieInfo movieInfo = new MovieInfo(movieID, titleNameMovie, releaseYearMovie, imgMovie, movieLenght, genresList, overview, trailer, false);
                movieInfo.setUserID(userID);
                movieInfo.setSerialID(nextID);

                databaseReference.child(movieID + "").setValue(movieInfo);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, MovieFragment.class, null)
                        .setReorderingAllowed(true).addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public String onTrailerLoaded(String trailerKey) {
        return trailerKey;
    }
}





