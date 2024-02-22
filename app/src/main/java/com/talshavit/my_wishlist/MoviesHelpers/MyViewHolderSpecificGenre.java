package com.talshavit.my_wishlist.MoviesHelpers;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderSpecificGenre extends RecyclerView.ViewHolder {

    ImageButton movieImageButton;
    TextView movieTextView;

    ImageView seenImageView;


    public MyViewHolderSpecificGenre(@NonNull View itemView) {
        super(itemView);
        movieImageButton = itemView.findViewById(R.id.movieImageButton);
        movieTextView = itemView.findViewById(R.id.movieTextView);
        seenImageView = itemView.findViewById(R.id.seenImageView);
    }
}
