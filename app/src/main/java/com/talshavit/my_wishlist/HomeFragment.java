package com.talshavit.my_wishlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.talshavit.my_wishlist.Movie.MovieFragment;

public class HomeFragment extends Fragment {

    private Button booksButton;
    private Button moviesButton;

    private String isItBookOrMovie;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ///////////////////////////////////
        /////////////////////////////////
        ////////////////////////////
        /////////////////fragment setting!!!!!!!!!!!1
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    private void initViews() {
        booksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isItBookOrMovie = "book";
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.passData(isItBookOrMovie);

                Bundle bundle = new Bundle();
                bundle.putString("keyCategory", "all");

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MovieFragment bookFragment = new MovieFragment();
                bookFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frame_layout, bookFragment).commit();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, BookFragment.class, null)
//                        .setReorderingAllowed(true).addToBackStack("")
//                        .commit();


            }
        });

//        moviesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                isItBookOrMovie = "movie";
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.passData(isItBookOrMovie);
//
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frame_layout, MovieFragment.class, null)
//                        .setReorderingAllowed(true).addToBackStack("")
//                        .commit();
//            }
//        });
    }

    private void findViews(View view) {
        //booksButton = view.findViewById(R.id.books_button);
        //moviesButton = view.findViewById(R.id.movies_button);
    }

}