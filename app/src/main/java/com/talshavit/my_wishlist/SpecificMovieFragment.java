package com.talshavit.my_wishlist;

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

import com.squareup.picasso.Picasso;

public class SpecificMovieFragment extends Fragment {

    private ImageView movieImageView, imageBackground;
    private TextView movieTitle, movieLenght, movieReleaseYear, movieOverview, movieGenre;
    private ImageButton backButton;

    private WebView webView;

    private ScrollView scrollView;


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
        String imgUrl = arguments.get("IMGURL").toString();
        String title = arguments.get("TITLE").toString();
        String lenght = arguments.get("LENGHT").toString();
        String year = arguments.get("YEAR").toString();
        String overview = arguments.get("OVERVIEW").toString();
        String genre = arguments.get("GENRE").toString();
        String trailerKey = arguments.get("TRAILER").toString();

        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imgUrl).into(movieImageView);
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imgUrl).into(imageBackground);
        movieTitle.setText(title);
        movieLenght.setText(lenght);
        movieReleaseYear.setText(year);

        if(overview.equals(""))
            movieOverview.setText("There is no overview");
        else
            movieOverview.setText(overview);

        movieGenre.setText(genre);
        if (trailerKey == null || trailerKey.isEmpty() || trailerKey.equals("") ){
            webView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
    }
}