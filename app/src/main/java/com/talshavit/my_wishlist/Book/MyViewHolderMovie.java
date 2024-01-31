package com.talshavit.my_wishlist.Book;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderMovie extends RecyclerView.ViewHolder {

    ImageButton movieImageButton,trailerImageButton;
    TextView movieTitle, movieDescription, movieGenre, movieLenght;
    CardView textCardView;


    public MyViewHolderMovie(@NonNull View itemView) {
        super(itemView);
        movieImageButton = itemView.findViewById(R.id.movieImageButton);
        trailerImageButton = itemView.findViewById(R.id.trailerImageButton);
        movieTitle = itemView.findViewById(R.id.movieTitle);
        movieDescription = itemView.findViewById(R.id.movieDescription);
        movieGenre = itemView.findViewById(R.id.movieGenre);
        movieLenght = itemView.findViewById(R.id.movieLenght);
        textCardView = itemView.findViewById(R.id.textCardView);
    }
}
