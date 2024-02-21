package com.talshavit.my_wishlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.Movie.MovieFragment;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.Movie.AddMovieFragment;
import com.talshavit.my_wishlist.TvShow.AddTvShowFragment;
import com.talshavit.my_wishlist.TvShow.TvShowsFragment;
import com.talshavit.my_wishlist.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //findViews();
        //initViews();
        replaceFragment(new LottieFragment());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.movies:
                    replaceFragment(new MovieFragment());
                    break;

                case R.id.TvShows:
                    replaceFragment(new TvShowsFragment());
                    break;

                case R.id.Seen:
                    replaceFragment(new WatchedFragment());
                    break;

                case R.id.setting:
                    replaceFragment(new SettingFragment());
                    break;
            }
            return true;
        });
    }

//    private void initViews() {
//
//        allMoviesItems = new ArrayList<MovieInfo>();
//
//        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // Check if the activity is still valid
//                if (isFinishing() || isDestroyed()) {
//                    return;
//                }
//
//                allMoviesItems.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
//                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
//                    allMoviesItems.add(movieInfo);
//                }
//                if(allMoviesItems.size() == 0){
//                    /*replaceFragment(new addMovieFragment());
//                    dialog = new Dialog(MainActivity.this);
//                    dialog.setContentView(R.layout.dialog_add_movie);
//                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,1400);
//                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_add_bg));
//                    dialog.setCancelable(false);
//                    ImageButton exitButton = dialog.findViewById(R.id.exitButton);
//                    dialog.show();
//                    exitButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });*/
//                }
//                else{
//                    replaceFragment(new MovieFragment());
//                }
//                //replaceFragment(new AddTvShowFragment());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//        /*addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new AddMovieOrTvShowFragment());
//            }
//        });*/
//
//    }
//
//    private void findViews() {
//        //addButton = findViewById(R.id.add_button);
//    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}