package com.talshavit.my_wishlist.TvShow;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TvShowsFragment extends Fragment implements MyAdapterGenres.GenreClickListener{
    private RecyclerView recyclerViewAll, recyclerViewGenresButtons, recyclerViewBySpecificGenre;
    private List<TvShowInfo> allTvShowInfos;
    private DatabaseReference databaseReference;
    private MyAdapterAllItems<TvShowInfo> myAdapterAllItems;
    private MyAdapterGenres myAdapterGenres;
    private MyAdapterSpecificGenge<TvShowInfo> myAdapterSpecificGenre;
    private Context context;
    private String userID;
    private List<String> genresList;
    private List<TvShowInfo> allTvShowsByGenre;
    private TextView genreTextView, allTvTitle;
    private String selectedGenre;
    private FloatingActionButton addButton;

    GeneralFunctions<TvShowInfo> generalFunctions = new GeneralFunctions<>();

    public TvShowsFragment() {
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

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        context = view.getContext();

        initViews();
    }
    private void initViews() {
        allTvTitle.setText("ALL TV SHOWS");
        allTvShowInfos = new ArrayList<TvShowInfo>();
        myAdapterAllItems = new MyAdapterAllItems<>(getActivity().getApplicationContext(), requireContext(), allTvShowInfos, "tv shows");
        initAdapter(recyclerViewAll, myAdapterAllItems);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTvShowInfos.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    TvShowInfo tvShowInfo = itemSnapshot.getValue(TvShowInfo.class);
                    allTvShowInfos.add(tvShowInfo);
                }

                if (allTvShowInfos.size() == 0) {
                    replaceFragment(new AddTvShowFragment());
                } else {
                    //Sort the list based on serialID in descending order - last in show first
                    Collections.sort(allTvShowInfos, new Comparator<TvShowInfo>() {
                        @Override
                        public int compare(TvShowInfo tvShowInfo1, TvShowInfo tvShowInfo2) {
                            return Integer.compare(tvShowInfo2.getSerialID(), tvShowInfo1.getSerialID());
                        }
                    });
                    createGenres(allTvShowInfos);
                    myAdapterAllItems.notifyDataSetChanged();

                    if (selectedGenre != null)
                        updateGenreList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddTvShowFragment());
            }
        });
        generalFunctions.setSwipeToDelete("DELETE TV SHOW","Do you want to delete \"",
                context,allTvShowInfos,myAdapterAllItems,
                databaseReference,recyclerViewAll,userID,"tv shows");
    }


    private void findViews(View view) {
        recyclerViewAll = view.findViewById(R.id.recyclerViewAllMovies);
        recyclerViewGenresButtons = view.findViewById(R.id.recyclerViewGenresButtons);
        recyclerViewBySpecificGenre = view.findViewById(R.id.recyclerViewMoviesBySpecificGenre);
        genreTextView = view.findViewById(R.id.genreTextView);
        addButton = view.findViewById(R.id.add_button);
        allTvTitle = view.findViewById(R.id.allTvTitle);
    }

    private void createGenres(List<TvShowInfo> allTvShowInfos) {
        genresList = new ArrayList<String>();
        for(int i=0; i<allTvShowInfos.size(); i++){
            if (!(allTvShowInfos.get(i).getGenres() == null) && !(allTvShowInfos.get(i).getGenres().isEmpty())) {
                for (int j=0; j<allTvShowInfos.get(i).getGenres().size(); j++) {
                    if (!(genresList.contains(allTvShowInfos.get(i).getGenres().get(j)))) {
                        genresList.add(allTvShowInfos.get(i).getGenres().get(j));
                    }
                }
            }
        }
        myAdapterGenres = new MyAdapterGenres(context, genresList,this);
        initAdapter(recyclerViewGenresButtons, myAdapterGenres);
    }

    @Override
    public void onGenreClick(String genre) {
        genreTextView.setText(genre.toUpperCase());
        selectedGenre = genre;
        if(selectedGenre != null)
            updateGenreList();
    }

    private void updateGenreList() {
        if(getActivity() != null && !getActivity().isFinishing()) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null) {
                genreTextView.setText(selectedGenre.toUpperCase());
                allTvShowsByGenre = new ArrayList<TvShowInfo>();
                for (int i = 0; i < allTvShowInfos.size(); i++) {
                    List<String> tvShowGenres = allTvShowInfos.get(i).getGenres();

                    if (tvShowGenres != null) {
                        if (tvShowGenres.contains(selectedGenre)) {
                            if (!(allTvShowsByGenre.contains(allTvShowInfos.get(i))))
                                allTvShowsByGenre.add(allTvShowInfos.get(i));
                        }
                    }
                }
                myAdapterSpecificGenre = new MyAdapterSpecificGenge<>(context, allTvShowsByGenre, fragmentManager, "tv shows");
                initAdapter(recyclerViewBySpecificGenre, myAdapterSpecificGenre);
            }
        }
    }

    private void initAdapter(RecyclerView recyclerView, RecyclerView.Adapter myAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}