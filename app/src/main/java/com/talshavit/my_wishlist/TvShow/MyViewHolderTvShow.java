package com.talshavit.my_wishlist.TvShow;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderTvShow extends RecyclerView.ViewHolder {

    ImageButton movieImageButton;
    Button trailerImageButton;
    TextView movieTitle, movieDescription, movieGenre, movieLenght, movieTextView;
    CardView textCardView,imageCardView;


    public MyViewHolderTvShow(@NonNull View itemView) {
        super(itemView);
        movieImageButton = itemView.findViewById(R.id.movieImageButton);
        trailerImageButton = itemView.findViewById(R.id.trailerImageButton);
        movieTitle = itemView.findViewById(R.id.movieTitle);
        movieDescription = itemView.findViewById(R.id.movieDescription);
        movieGenre = itemView.findViewById(R.id.movieGenre);
        movieLenght = itemView.findViewById(R.id.movieLenght);
        movieTextView = itemView.findViewById(R.id.movieTextView);
        textCardView = itemView.findViewById(R.id.textCardView);
        imageCardView = itemView.findViewById(R.id.imageCardView);
    }
}
