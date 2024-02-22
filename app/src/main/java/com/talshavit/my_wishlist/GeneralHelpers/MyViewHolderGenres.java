package com.talshavit.my_wishlist.GeneralHelpers;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderGenres extends RecyclerView.ViewHolder {

    Button genreButton;

    public MyViewHolderGenres(@NonNull View itemView) {
        super(itemView);
        genreButton = itemView.findViewById(R.id.genreButton);
    }
}
