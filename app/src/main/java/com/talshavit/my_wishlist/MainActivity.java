package com.talshavit.my_wishlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.Movie.MovieFragment;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FloatingActionButton addButton;
    private Dialog dialog;
    private List<MovieInfo> allMoviesItems;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViews();
        initViews();


        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.category:
                        replaceFragment(new CategoryFragment());
                    break;


                case R.id.setting:
                    replaceFragment(new SettingFragment());
                    break;

            }

            return true;
        });
    }

//    //search internet with the default search app
//    private void searchNet(String words){
//        try{
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY,words);
//            startActivity(intent);
//        }catch (ActivityNotFoundException e){
//            e.printStackTrace();
//            searchNetCompat(words);
//        }
//    }
//
//    //search internet with the browser if there's no  search app
//    private void searchNetCompat(String words){
//        try{
//            Uri uri = Uri.parse("http://www.google.com/#q=" + words);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
//        }catch (ActivityNotFoundException e){
//            e.printStackTrace();
//            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void initViews() {


        allMoviesItems = new ArrayList<MovieInfo>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
        databaseReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
                    allMoviesItems.add(movieInfo);
                }
                if(allMoviesItems.size() == 0){
                    replaceFragment(new addMovieFragment());
                    dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(R.layout.dialog_add_movie);
                    dialog.getWindow().setLayout(1000,1200);
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
                    dialog.setCancelable(true);
                    dialog.show();
                }
                else
                    replaceFragment(new MovieFragment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



//        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
//        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                allMoviesItems.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
//                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
//                    allMoviesItems.add(movieInfo);
//                }
//                if(allMoviesItems.size() == 0)
//                    replaceFragment(new addMovieFragment());
//                else
//                    replaceFragment(new MovieFragment());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(isItBookOrMovie.equals("book"))
                    //dialogBook.show();
                    replaceFragment(new addMovieFragment());
//                else if(isItBookOrMovie.equals("movie"))
//                    dialogMovie.show();
//                else
//                    Toast.makeText(MainActivity.this, "YOU HAVE TO CHOOSE BOOK OR MOVIE FIRST!", Toast.LENGTH_SHORT).show();

                //dialog.dismiss();

            }
        });

    }

    private void findViews() {
        addButton = findViewById(R.id.add_button);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}