package com.talshavit.my_wishlist.Movie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class SpecificMovieFragment extends Fragment {

    private ImageView movieImageView, imageBackground;
    private TextView movieTitle, movieLenght, movieReleaseYear, movieOverview, movieGenre;
    private ImageButton backButton;
    private WebView webView;
    private ScrollView scrollView;
    private Button deleteMovieButton, watchedMovieButton;

    private DatabaseReference databaseReference;
    public SpecificMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        //initView();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initView(arguments);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void initView(Bundle arguments) {
        MovieInfo movieInfo = (MovieInfo) arguments.getSerializable("MOVIE_INFO");

        int movieID = movieInfo.getMovieID();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+movieInfo.getImageUrl()).into(movieImageView);
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+movieInfo.getImageUrl()).into(imageBackground);
        movieTitle.setText(movieInfo.getMovieName());
        movieLenght.setText(movieInfo.getMovieLenght());
        movieReleaseYear.setText(movieInfo.getReleaseYear());
        if(movieInfo.getOverview().equals(""))
            movieOverview.setText("There is no overview");
        else
            movieOverview.setText(movieInfo.getOverview());
        List<String> genres = movieInfo.getGenres();
        String formattedGenres = formatGenres(genres);
        movieGenre.setText(formattedGenres);
        String trailerKey = movieInfo.getTrailer();
        if (trailerKey == null || trailerKey.isEmpty() || trailerKey.equals("") ){
            webView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            layoutParams.height = (int) (370 * getResources().getDisplayMetrics().density);
            //layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // Apply the updated layout parameters
            scrollView.setLayoutParams(layoutParams);
        }
        else{
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            // Load URL with trailer key
            String url = "https://www.youtube.com/embed/" + trailerKey;
            webView.loadUrl(url);
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies").child(String.valueOf(movieID));
        deleteMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("DELETE MOVIE");
                builder.setMessage("Do you want to delete \"" + movieInfo.getMovieName().toUpperCase() + "\"?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMovieFromFirebase(movieID);
                        requireActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        watchedMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieInfo.setWatched(true);
                databaseReference.child("watched").setValue(true);

            }
        });

        databaseReference.child("watched").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isWatched = snapshot.getValue(Boolean.class);
                if(isWatched != null && isWatched)
                    watchedMovieButton.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteMovieFromFirebase(int movieID) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference specificTvShowReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies").child(movieID+"");
        specificTvShowReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void findViews(View view) {
        movieImageView = view.findViewById(R.id.movieImageView);
        imageBackground = view.findViewById(R.id.imageBackground);
        movieTitle = view.findViewById(R.id.movieTitle);
        movieLenght = view.findViewById(R.id.movieLenght);
        movieReleaseYear = view.findViewById(R.id.movieReleaseYear);
        movieOverview = view.findViewById(R.id.movieOverview);
        movieGenre = view.findViewById(R.id.movieGenre);
        backButton = view.findViewById(R.id.backButton);
        webView = view.findViewById(R.id.webView);
        scrollView = view.findViewById(R.id.scrollView);
        deleteMovieButton = view.findViewById(R.id.deleteMovieButton);
        watchedMovieButton = view.findViewById(R.id.watchedMovieButton);
    }

    private String formatGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "There is no genres"; //When genres is null or empty
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String genre : genres) {
            stringBuilder.append(genre).append(", ");
        }

        // Remove the trailing comma and space
        if (stringBuilder.length() > 1) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }
}