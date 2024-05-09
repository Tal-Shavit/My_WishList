package com.talshavit.my_wishlist.Movie;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.GeneralHelpers.GeneralFunctions;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterAllItems;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterGenres;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterSpecificGenge;
import com.talshavit.my_wishlist.GeneralHelpers.MyViewHolderAllItems;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MovieFragment extends Fragment implements MyAdapterGenres.GenreClickListener {
    private RecyclerView recyclerViewAll, recyclerViewGenresButtons, recyclerViewMoviesBySpecificGenre;
    private List<MovieInfo> allMoviesItems;
    private DatabaseReference databaseReference;
    private MyAdapterAllItems<MovieInfo> myAdapterAllItems;
    private MyAdapterGenres myAdapterGenres;
    private MyAdapterSpecificGenge<MovieInfo> myAdapterSpecificGenre;
    private Context context;
    private String userID;
    private List<String> genresList;
    private List<MovieInfo> allMoviesByGenre;
    private TextView genreTextView;
    private String selectedGenre;
    private FloatingActionButton addButton;
    GeneralFunctions<MovieInfo> generalFunctions = new GeneralFunctions<>(context);

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        context = view.getContext();
        initViews(view);
    }

    private void initViews(View view) {
        allMoviesItems = new ArrayList<MovieInfo>();
        myAdapterAllItems = new MyAdapterAllItems<>(getActivity().getApplicationContext(), requireContext(), allMoviesItems, "movies");
        initAdapter(recyclerViewAll, myAdapterAllItems);
        getMoviesFromDB();
        onAddButtonClick();
        swipeToDelete();
        dragMovie();
    }

    private void dragMovie() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(allMoviesItems, position_dragged, position_target);

                updateSerialIds();
                updateInFirebaseDatabase();
                myAdapterAllItems.notifyDataSetChanged();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        helper.attachToRecyclerView(recyclerViewAll);
    }


    private void updateInFirebaseDatabase() {
        databaseReference.setValue(allMoviesItems);
    }

    private void updateSerialIds() {
        for (int i = 0; i < allMoviesItems.size(); i++) {
            allMoviesItems.get(i).setSerialID(i);
        }
    }

    private void swipeToDelete() {
        generalFunctions.setSwipeToDelete("DELETE MOVIE", "Do you want to delete \"", context, allMoviesItems, myAdapterAllItems,
                databaseReference, recyclerViewAll, userID, "movies");
    }

    private void onAddButtonClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddMovieFragment());
            }
        });
    }

    private void getMoviesFromDB() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
                    allMoviesItems.add(movieInfo);
                }

                if (allMoviesItems.size() == 0) {
                    replaceFragment(new AddMovieFragment());
                } else {
                    createGenres(allMoviesItems);
                    myAdapterAllItems.notifyDataSetChanged();

                    if (selectedGenre != null)
                        updateGenreList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initAdapter(RecyclerView recyclerView, RecyclerView.Adapter myAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    private void createGenres(List<MovieInfo> allMoviesItems) {
        genresList = new ArrayList<String>();
        for (int i = 0; i < allMoviesItems.size(); i++) {
            if (!(allMoviesItems.get(i).getGenres() == null) && !(allMoviesItems.get(i).getGenres().isEmpty())) {
                for (int j = 0; j < allMoviesItems.get(i).getGenres().size(); j++) {
                    if (!(genresList.contains(allMoviesItems.get(i).getGenres().get(j)))) {
                        genresList.add(allMoviesItems.get(i).getGenres().get(j));
                    }
                }
            }
        }
        myAdapterGenres = new MyAdapterGenres(context, genresList, this);
        initAdapter(recyclerViewGenresButtons, myAdapterGenres);
    }

    private void findViews(View view) {
        recyclerViewAll = view.findViewById(R.id.recyclerViewAllMovies);
        recyclerViewGenresButtons = view.findViewById(R.id.recyclerViewGenresButtons);
        recyclerViewMoviesBySpecificGenre = view.findViewById(R.id.recyclerViewMoviesBySpecificGenre);
        genreTextView = view.findViewById(R.id.genreTextView);
        addButton = view.findViewById(R.id.add_button);
    }

    @Override
    public void onGenreClick(String genre) {
        genreTextView.setText(genre.toUpperCase());
        selectedGenre = genre;
        if (selectedGenre != null)
            updateGenreList();
    }

    private void updateGenreList() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null) {
                genreTextView.setText(selectedGenre.toUpperCase());
                allMoviesByGenre = new ArrayList<MovieInfo>();
                for (int i = 0; i < allMoviesItems.size(); i++) {
                    List<String> movieGenres = allMoviesItems.get(i).getGenres();

                    if (movieGenres != null) {
                        if (movieGenres.contains(selectedGenre)) {
                            if (!(allMoviesByGenre.contains(allMoviesItems.get(i))))
                                allMoviesByGenre.add(allMoviesItems.get(i));
                        }
                    }
                }
                myAdapterSpecificGenre = new MyAdapterSpecificGenge<>(context, allMoviesByGenre, fragmentManager, "movies");
                initAdapter(recyclerViewMoviesBySpecificGenre, myAdapterSpecificGenre);
            }
        }
        if (allMoviesByGenre.isEmpty()) {
            genreTextView.setText("");
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
