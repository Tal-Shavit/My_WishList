package com.talshavit.my_wishlist.Helpers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderSpecificGenre extends RecyclerView.ViewHolder {

    ImageButton movieImageButton;
    TextView movieTextView;


    public MyViewHolderSpecificGenre(@NonNull View itemView) {
        super(itemView);
        movieImageButton = itemView.findViewById(R.id.movieImageButton);
        movieTextView = itemView.findViewById(R.id.movieTextView);
    }
}
