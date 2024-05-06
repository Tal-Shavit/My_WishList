package com.talshavit.my_wishlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterSpecificGenge;
import com.talshavit.my_wishlist.Movie.AddMovieFragment;
import com.talshavit.my_wishlist.Movie.MovieInfo;
//import com.talshavit.my_wishlist.MoviesHelpers.MyAdapterSpecificGenre;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WatchedFragment extends Fragment {

    private Context context;

    private RecyclerView allWatchedMovies;
    private RecyclerView allWatchedTvShows;
    private MyAdapterSpecificGenge<MovieInfo> myAdapterSpecificGenreMovie;
    private MyAdapterSpecificGenge<TvShowInfo> myAdapterSpecificGenreTv;
    private DatabaseReference databaseReference;
    private List<MovieInfo> allMoviesItems;
    private List<TvShowInfo> allTvShowItems;
    private FragmentManager fragmentManager;

    public WatchedFragment() {
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
        return inflater.inflate(R.layout.fragment_watched, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        context = view.getContext();
        allMoviesItems = new ArrayList<MovieInfo>();
        allTvShowItems = new ArrayList<TvShowInfo>();
        loadWatchedMoviesFromFirebase();
        loadWatchedTvShowsFromFirebase();
        fragmentManager = getActivity().getSupportFragmentManager();
        allMoviesList();
        allTvList();
    }

    private void allTvList() {
        LinearLayoutManager linearLayoutManagerTv = new LinearLayoutManager(context);
        linearLayoutManagerTv.setOrientation(linearLayoutManagerTv.HORIZONTAL);
        allWatchedTvShows.setLayoutManager(linearLayoutManagerTv);
        myAdapterSpecificGenreTv = new MyAdapterSpecificGenge<>(context, allTvShowItems, fragmentManager,"tv shows");
        allWatchedTvShows.setAdapter(myAdapterSpecificGenreTv);
    }

    private void allMoviesList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        allWatchedMovies.setLayoutManager(linearLayoutManager);
        myAdapterSpecificGenreMovie = new MyAdapterSpecificGenge<>(context,  allMoviesItems,fragmentManager, "movies");
        allWatchedMovies.setAdapter(myAdapterSpecificGenreMovie);
    }

    private void loadWatchedMoviesFromFirebase() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
        databaseReference.orderByChild("watched").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
                    if(movieInfo != null){
                        allMoviesItems.add(movieInfo);
                    }
                }
                sortMovieList();
                myAdapterSpecificGenreMovie.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sortMovieList() {
        //Sort the list based on serialID in descending order - last in show first
        Collections.sort(allMoviesItems, new Comparator<MovieInfo>() {
            @Override
            public int compare(MovieInfo movieInfo1, MovieInfo movieInfo2) {
                return Integer.compare(movieInfo2.getSerialID(), movieInfo1.getSerialID());
            }
        });
    }

    private void loadWatchedTvShowsFromFirebase() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
        databaseReference.orderByChild("watched").equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTvShowItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    TvShowInfo tvShowInfo = itemSnapshot.getValue(TvShowInfo.class);
                    if(tvShowInfo != null){
                        allTvShowItems.add(tvShowInfo);
                    }
                }
                sortTvList();
                myAdapterSpecificGenreTv.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sortTvList() {
        //Sort the list based on serialID in descending order - last in show first
        Collections.sort(allTvShowItems, new Comparator<TvShowInfo>() {
            @Override
            public int compare(TvShowInfo tvShowInfo1, TvShowInfo tvShowInfo2) {
                return Integer.compare(tvShowInfo2.getSerialID(), tvShowInfo1.getSerialID());
            }
        });
    }

    private void findViews(View view) {
        allWatchedMovies = view.findViewById(R.id.allWatchedMovies);
        allWatchedTvShows = view.findViewById(R.id.allWatchedTvShows);
    }

}