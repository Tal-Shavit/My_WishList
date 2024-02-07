package com.talshavit.my_wishlist.Genres;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talshavit.my_wishlist.PassGenresInterface;
import com.talshavit.my_wishlist.R;

import java.util.List;
import java.util.Locale;

public class genresMovieFragment extends Fragment implements PassGenresInterface {

private List<String> genresList;
    public genresMovieFragment() {
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
        return inflater.inflate(R.layout.fragment_genres_movie, container, false);
    }

    @Override
    public void passGenres(List<String> genres) {
        genresList = genres;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (genresList != null) {
            Log.d("lala", "lalallalala");
        }
    }
}