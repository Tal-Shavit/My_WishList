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
import com.talshavit.my_wishlist.GeneralHelpers.TmdbApiClientGeneral;
import com.talshavit.my_wishlist.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddMovieFragment extends Fragment {

    private DatabaseReference databaseReference;
    private EditText titleEditText;
    private ImageButton titleButton;
    private static Spinner dynamicSpinner;
    private ArrayAdapter<String> adapter;
    private ImageView movieImageView;
    private static Button addButton;
    private static String titleNameMovie;
    private static String releaseYearMovie;
    private static String imgMovie;
    private static int movieID;
    private static String movieLenght;
    private static List<String> genres;
    private static String overview;
    private static String trailer;
    private static int nextID;

    public AddMovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_movie, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initView();
    }

    private void initView() {
        checkLastSerialNumber();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);

        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if(!title.equals("")){
                    TmdbApiClientMovie.title = title;

                    dynamicSpinner.setVisibility(View.INVISIBLE);
                    movieImageView.setVisibility(View.INVISIBLE);

                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    new MyAsyncTask(AddMovieFragment.this, progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else
                    Toast.makeText(getContext(), "YOU HAVE TO FILL THE TITLE!", Toast.LENGTH_SHORT).show();
            }

        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
                MovieInfo movieInfo = new MovieInfo(movieID,titleNameMovie,releaseYearMovie,imgMovie,movieLenght,genres,overview,trailer,false);
                movieInfo.setUserID(userID);
                movieInfo.setSerialID(nextID);

                databaseReference.child(movieID+"").setValue(movieInfo);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, MovieFragment.class, null)
                        .setReorderingAllowed(true).addToBackStack(null)
                        .commit();

            }
        });
    }

    private void checkLastSerialNumber() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
        databaseReference.orderByChild("serialID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MovieInfo lastAddedMovie = snapshot.getValue(MovieInfo.class);
                    if (lastAddedMovie != null) {
                        nextID = lastAddedMovie.getSerialID()+1;
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
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<MovieInfo>> {
        private final WeakReference<AddMovieFragment> fragmentReference;
        private ProgressDialog progressDialog;
        MyAsyncTask(AddMovieFragment fragment, ProgressDialog progressDialog) {
            this.fragmentReference = new WeakReference<>(fragment);
            this.progressDialog = progressDialog;
        }

        @Override
        protected List<MovieInfo> doInBackground(Void... voids) {
            try {
                Log.d("MovieName", "doInBackground executed");
                return TmdbApiClientMovie.getAllPopularMovies();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MovieName", "Exception in doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfos) {
            AddMovieFragment fragment = fragmentReference.get();
            if (fragment != null) {
                Log.d("MovieName", "Fragment is still valid");
                Log.d("MovieName", "onPostExecute executed");

                progressDialog.dismiss();

                if (movieInfos != null && !movieInfos.isEmpty()) {
                    for (MovieInfo movieInfo : movieInfos) {
                        fragment.adapter.add(movieInfo.getMovieName() + " (" + movieInfo.getReleaseYear() + ")");
                    }
                    fragment.adapter.notifyDataSetChanged();  // Notify adapter after adding all items

                    fragment.dynamicSpinner.setVisibility(View.VISIBLE);
                    fragment.movieImageView.setVisibility(View.VISIBLE);

                        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movieInfos.get(position).getImageUrl()).into(fragment.movieImageView);
                                addButton.setVisibility(View.VISIBLE);
                                movieID = movieInfos.get(position).getMovieID();
                                titleNameMovie = movieInfos.get(position).getMovieName();
                                releaseYearMovie = movieInfos.get(position).getReleaseYear();
                                imgMovie = movieInfos.get(position).getImageUrl();
                                movieLenght = movieInfos.get(position).getMovieLenght();
                                genres = movieInfos.get(position).getGenres();
                                overview = movieInfos.get(position).getOverview();
                                trailer = movieInfos.get(position).getTrailer();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                } else {
                    Toast.makeText(fragment.getContext(), "No matches found!", Toast.LENGTH_SHORT).show();
                    fragment.titleEditText.setText("");
                    //Log.e("MovieName", "Error getting movie names");
                    //Toast.makeText(fragment.getContext(), "Error getting movie info", Toast.LENGTH_SHORT).show();
                }
            }else {
                Log.e("MovieName", "Fragment reference is null");
            }
        }
    }
}
