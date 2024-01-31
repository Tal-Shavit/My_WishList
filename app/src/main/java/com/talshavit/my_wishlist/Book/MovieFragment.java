package com.talshavit.my_wishlist.Book;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.List;


public class MovieFragment extends Fragment {


    private RecyclerView recyclerView;
    private List<MovieInfo> allMoviesItems;

    private MyAdapterMovie adapter;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;


    public MovieFragment() {
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
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);


        allMoviesItems = new ArrayList<MovieInfo>();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new MyAdapterMovie(getActivity().getApplicationContext(), allMoviesItems));

        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
                    allMoviesItems.add(movieInfo);
                }
                recyclerView.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        private void findViews (View view){
            recyclerView = view.findViewById(R.id.recyclerViewBook);
    }
}
        //categoryLists();

        //Bundle bundle = getArguments();
        //String category = bundle.getString("keyCategory");
//        if (category.equals("romance")) {
//            for (int i = 0; i < bookItemsAll.size(); i++) {
//                if (bookItemsAll.get(i).getCategory().equals("romance"))
//                    bookItemsRomance.add(bookItemsAll.get(i));
//            }
//            recyclerView.setAdapter(new MyAdapterBook(getActivity().getApplicationContext(), bookItemsRomance));
//        }
//
//        if (category.equals("fiction")) {
//            for (int i = 0; i < bookItemsAll.size(); i++) {
//                if (bookItemsAll.get(i).getCategory().equals("fiction"))
//                    bookItemsFiction.add(bookItemsAll.get(i));
//            }
//            recyclerView.setAdapter(new MyAdapterBook(getActivity().getApplicationContext(), bookItemsFiction));
//        }
//
//        if (category.equals("horror")) {
//            for (int i = 0; i < bookItemsAll.size(); i++) {
//                if (bookItemsAll.get(i).getCategory().equals("horror"))
//                    bookItemsHorror.add(bookItemsAll.get(i));
//            }
//            recyclerView.setAdapter(new MyAdapterBook(getActivity().getApplicationContext(), bookItemsHorror));
//        }
//
//        if (category.equals("all"))


//    private void categoryLists() {
//        movieAllItems = new ArrayList<MovieInfo>();
//    }
