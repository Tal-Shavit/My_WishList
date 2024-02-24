package com.talshavit.my_wishlist.GeneralHelpers;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderSpecificGenre extends RecyclerView.ViewHolder {

    ImageButton imageButton;
    TextView titleTextView;
    ImageView seenImageView;


    public MyViewHolderSpecificGenre(@NonNull View itemView) {
        super(itemView);
        imageButton = itemView.findViewById(R.id.imageButton);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        seenImageView = itemView.findViewById(R.id.seenImageView);
    }
}
