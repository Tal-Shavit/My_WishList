package com.talshavit.my_wishlist.GeneralHelpers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderAllItems<T> extends RecyclerView.ViewHolder {

    ImageButton imageButton, backButton;
    Button trailerImageButton;
    TextView titleTxt, overviewTxt, genreTxt, lenghtTxt, titleTextView;
    CardView textCardView,imageCardView;
    ImageView seenImageView;


    public MyViewHolderAllItems(@NonNull View itemView) {
        super(itemView);
        imageButton = itemView.findViewById(R.id.imageButton);
        backButton = itemView.findViewById(R.id.backButton);
        trailerImageButton = itemView.findViewById(R.id.trailerImageButton);
        titleTxt = itemView.findViewById(R.id.titleTxt);
        genreTxt = itemView.findViewById(R.id.genreTxt);
        overviewTxt = itemView.findViewById(R.id.overviewTxt);
        lenghtTxt = itemView.findViewById(R.id.lenghtTxt);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        textCardView = itemView.findViewById(R.id.textCardView);
        imageCardView = itemView.findViewById(R.id.imageCardView);
        seenImageView = itemView.findViewById(R.id.seenImageView);
    }
}
