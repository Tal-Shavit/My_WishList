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

import com.talshavit.my_wishlist.Book.MovieFragment;

public class CategoryFragment extends Fragment {

    private Button romance_button;
    private Button all_button;
    private Button horror_button;

    private Button fiction_button;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        romance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("keyCategory", "romance");

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MovieFragment bookFragment = new MovieFragment();
                bookFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frame_layout, bookFragment).commit();
            }
        });

       horror_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("keyCategory", "horror");

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MovieFragment bookFragment = new MovieFragment();
                bookFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frame_layout, bookFragment).commit();
            }
        });

        all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("keyCategory", "all");

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MovieFragment bookFragment = new MovieFragment();
                bookFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frame_layout, bookFragment).commit();
            }
        });
    }

    private void findViews(View view) {
        romance_button = view.findViewById(R.id.category_romance);
        all_button = view.findViewById(R.id.category_all);
        horror_button = view.findViewById(R.id.category_horror);
        fiction_button = view.findViewById(R.id.category_fiction);


    }
}