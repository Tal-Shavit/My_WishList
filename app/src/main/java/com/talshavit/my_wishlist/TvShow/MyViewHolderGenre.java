package com.talshavit.my_wishlist.TvShow;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talshavit.my_wishlist.R;

public class MyViewHolderGenre extends RecyclerView.ViewHolder {

    TextView genreTextView;
    RecyclerView recyclerSpecificGenre;

    public MyViewHolderGenre(@NonNull View itemView) {
        super(itemView);
        genreTextView = itemView.findViewById(R.id.genreTextView);
        recyclerSpecificGenre = itemView.findViewById(R.id.recycler_specific_genre_tv_show);
    }
}
