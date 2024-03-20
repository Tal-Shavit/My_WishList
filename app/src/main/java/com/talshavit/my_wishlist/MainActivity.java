package com.talshavit.my_wishlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        replaceFragment(new LottieFragment());

//        // Deselect all items in the bottom navigation view
//        Menu menu = binding.bottomNavigationView.getMenu();
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem menuItem = menu.getItem(i);
//            menuItem.setChecked(false);
//        }

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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}