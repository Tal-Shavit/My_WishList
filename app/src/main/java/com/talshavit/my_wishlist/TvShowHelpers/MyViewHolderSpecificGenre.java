package com.talshavit.my_wishlist.TvShowHelpers;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderSpecificGenre extends RecyclerView.ViewHolder {

    ImageButton tvShowImageButton;
    TextView tvShowTextView;

    ImageView seenImageView;

    public MyViewHolderSpecificGenre(@NonNull View itemView) {
        super(itemView);
        tvShowImageButton  = itemView.findViewById(R.id.tvShowImageButton);
        tvShowTextView = itemView.findViewById(R.id.tvShowTextView);
        seenImageView = itemView.findViewById(R.id.seenImageView);
    }

}
