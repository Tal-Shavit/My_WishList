package com.talshavit.my_wishlist.Media;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.GeneralHelpers.GeneralFunctions;
import com.talshavit.my_wishlist.GeneralHelpers.GenerealInterfaces;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterAllItems;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterGenres;
import com.talshavit.my_wishlist.GeneralHelpers.MyAdapterSpecificGenge;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaFragment<T extends GenerealInterfaces> extends Fragment implements MyAdapterGenres.GenreClickListener {
    private RecyclerView recyclerViewAll, recyclerViewGenresButtons, recyclerViewBySpecificGenre;
    private List<T> allMediaInfos;
    private DatabaseReference databaseReference;
    private MyAdapterAllItems<?> myAdapterAllItems;
    private MyAdapterGenres myAdapterGenres;
    private MyAdapterSpecificGenge<T> myAdapterSpecificGenre;
    private Context context;
    private String userID;
    private List<String> genresList;
    private List<T> alllistByGenre;
    private TextView genreTextView, allTvTitle;
    private String selectedGenre;
    private FloatingActionButton addButton;

    private String mediaType;

    GeneralFunctions<T> generalFunctions;

    private Class<T> genericType;

    public MediaFragment(String mediaType, Class<T> genericType) {
        this.mediaType = mediaType;
        this.genericType = genericType;
        allMediaInfos = new ArrayList<>();
        generalFunctions = new GeneralFunctions<>(context);
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
        initViews(view);
    }

    private void initViews(View view) {
        allTvTitle.setText("ALL " + mediaType.toUpperCase());
        myAdapterAllItems = new MyAdapterAllItems<>(getActivity().getApplicationContext(), requireContext(), allMediaInfos, mediaType);
        initAdapter(recyclerViewAll, myAdapterAllItems);
        getTvsFromDB();
        onAddButtonClick();
        swipeToDelete();
        dragTv();
    }

    private void dragTv() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(allMediaInfos, position_dragged, position_target);

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
        databaseReference.setValue(allMediaInfos);
    }

    private void updateSerialIds() {
        for (int i = 0; i < allMediaInfos.size(); i++) {
            allMediaInfos.get(i).setSerialID(i);
        }
    }

    private void getTvsFromDB() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(mediaType);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMediaInfos.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    T mediaInfo = itemSnapshot.getValue(genericType);
                    allMediaInfos.add(mediaInfo);
                }

                if (allMediaInfos.size() == 0) {
                    replaceFragment(new AddMediaFragment(mediaType));
                } else {
                    createGenres(allMediaInfos);
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

    private void swipeToDelete() {
        generalFunctions.setSwipeToDelete("DELETE " + mediaType.toUpperCase(), "Do you want to delete \"",
                context, allMediaInfos, myAdapterAllItems,
                databaseReference, recyclerViewAll, userID, mediaType);
    }

    private void onAddButtonClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddMediaFragment(mediaType));
            }
        });
    }

    private void findViews(View view) {
        recyclerViewAll = view.findViewById(R.id.recyclerViewAllItems);
        recyclerViewGenresButtons = view.findViewById(R.id.recyclerViewGenresButtons);
        recyclerViewBySpecificGenre = view.findViewById(R.id.recyclerViewMoviesBySpecificGenre);
        genreTextView = view.findViewById(R.id.genreTextView);
        addButton = view.findViewById(R.id.add_button);
        allTvTitle = view.findViewById(R.id.allTvTitle);
    }

    private void createGenres(List<T> allMediaInfos) {
        genresList = new ArrayList<String>();
        for (int i = 0; i < allMediaInfos.size(); i++) {
            if (!(allMediaInfos.get(i).getGenres() == null) && !(allMediaInfos.get(i).getGenres().isEmpty())) {
                for (int j = 0; j < allMediaInfos.get(i).getGenres().size(); j++) {
                    if (!(genresList.contains(allMediaInfos.get(i).getGenres().get(j)))) {
                        genresList.add(allMediaInfos.get(i).getGenres().get(j));
                    }
                }
            }
        }
        myAdapterGenres = new MyAdapterGenres(context, genresList, this);
        initAdapter(recyclerViewGenresButtons, myAdapterGenres);
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
                alllistByGenre = new ArrayList<T>();
                for (int i = 0; i < allMediaInfos.size(); i++) {
                    List<String> listGenres = allMediaInfos.get(i).getGenres();

                    if (listGenres != null) {
                        if (listGenres.contains(selectedGenre)) {
                            if (!(alllistByGenre.contains(allMediaInfos.get(i))))
                                alllistByGenre.add(allMediaInfos.get(i));
                        }
                    }
                }
                myAdapterSpecificGenre = new MyAdapterSpecificGenge<>(context, alllistByGenre, fragmentManager, mediaType);
                initAdapter(recyclerViewBySpecificGenre, myAdapterSpecificGenre);
            }
        }
        if (alllistByGenre.isEmpty()) {
            genreTextView.setText("");
        }
    }

    private void initAdapter(RecyclerView recyclerView, RecyclerView.Adapter myAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
