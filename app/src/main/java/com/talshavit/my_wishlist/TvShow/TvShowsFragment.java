package com.talshavit.my_wishlist.TvShow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.Movie.AddMovieFragment;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TvShowsFragment extends Fragment {

    private FloatingActionButton addButton;

    private Context context;

    private List<TvShowInfo> allTvShowInfos;

    private List<String> genresList;

    private String userID;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewAllTVGenres;
    private MyAdapterGenre myAdapterGenre;

    public TvShowsFragment() {
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
        return inflater.inflate(R.layout.fragment_tv_shows, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        context = view.getContext();

        initViews();
    }

    private void initViews() {
        allTvShowInfos = new ArrayList<TvShowInfo>();
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
    }


    private void findViews(View view) {
        addButton = view.findViewById(R.id.add_button);
        recyclerViewAllTVGenres = view.findViewById(R.id.recyclerViewAllTVGenres);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void createGenres(List<TvShowInfo> allTvShowInfos) {
        genresList = new ArrayList<String>();
        for (int i = 0; i < allTvShowInfos.size(); i++) {
            if (!(allTvShowInfos.get(i).getGenres() == null) && !(allTvShowInfos.get(i).getGenres().isEmpty())) {
                for (int j = 0; j < allTvShowInfos.get(i).getGenres().size(); j++) {
                    if (!(genresList.contains(allTvShowInfos.get(i).getGenres().get(j)))) {
                        genresList.add(allTvShowInfos.get(i).getGenres().get(j));
                    }
                }
            }
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        LinearLayoutManager linearLayoutManagerG = new LinearLayoutManager(getContext());
        linearLayoutManagerG.setOrientation(linearLayoutManagerG.VERTICAL);
        recyclerViewAllTVGenres.setLayoutManager(linearLayoutManagerG);
        myAdapterGenre = new MyAdapterGenre(context, genresList, allTvShowInfos, fragmentManager);
        recyclerViewAllTVGenres.setAdapter(myAdapterGenre);

    }
}