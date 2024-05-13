package com.talshavit.my_wishlist.GeneralHelpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.List;

public class MyAdapterSpecificGenge<T extends GenerealInterfaces> extends RecyclerView.Adapter<MyViewHolderSpecificGenre> {

    private Context context;
    private List<T> itemInfoList;
    private String imageUrl, imgBackg;
    private FragmentManager fragmentManager;
    private String itemType;
    private String title, lenght, releaseYear, overview, trailerKey;
    private int ID, serialID;
    private List<String> genres;
    private boolean isWatched;
    private Bundle bundle;

    public MyAdapterSpecificGenge(Context context, List<T> itemInfoList, FragmentManager fragmentManager, String itemType) {
        this.context = context;
        this.itemInfoList = itemInfoList;
        this.fragmentManager = fragmentManager;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public MyViewHolderSpecificGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSpecificGenre(LayoutInflater.from(context).inflate(R.layout.recycler_specific_genre, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSpecificGenre holder, int position) {
        holder.titleTextView.setText(itemInfoList.get(position).getName());
        imageUrl = itemInfoList.get(position).getImageUrl();
        imgBackg = itemInfoList.get(position).getImageUrlBackground();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/" + imageUrl).into(holder.imageButton);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpecificItem(position);
            }
        });

        if (itemInfoList.get(position).isWatched()) {
            holder.seenImageView.setVisibility(View.VISIBLE);
        }
    }

    private void showSpecificItem(int position) {
        T item = itemInfoList.get(position);
        imageUrl = item.getImageUrl();
        imgBackg = item.getImageUrlBackground();
        title = item.getName();
        lenght = item.getLenght();
        releaseYear = item.getReleaseYear();
        overview = item.getOverview();
        genres = item.getGenres();
        trailerKey = item.getTrailer();
        ID = item.getMediaID();
        serialID = item.getSerialID();
        isWatched = item.isWatched();

        bundle = new Bundle();

        initGeneralFragment();

    }

    private void initGeneralFragment() {
        if (itemType.equals("tv shows")) {
            TvShowInfo tvShowInfo = new TvShowInfo(ID, title, imageUrl, imgBackg, releaseYear, genres, overview, trailerKey, isWatched, lenght);
            bundle.putSerializable("MEDIA_INFO", tvShowInfo);
            bundle.putSerializable("SERIAL_ID", serialID);
            SpecificFragmentGeneral<TvShowInfo> specificFragmentGeneral = new SpecificFragmentGeneral<>("tv shows");
            specificFragmentGeneral.setArguments(bundle);
            replaceFragment((SpecificFragmentGeneral<T>) specificFragmentGeneral);
        } else {
            MovieInfo movieInfo = new MovieInfo(ID, title, imageUrl, imgBackg, releaseYear, genres, overview, trailerKey, isWatched,lenght);
            bundle.putSerializable("MEDIA_INFO", movieInfo);
            bundle.putSerializable("SERIAL_ID", serialID);
            SpecificFragmentGeneral<MovieInfo> specificFragmentGeneral = new SpecificFragmentGeneral<>("movies");
            specificFragmentGeneral.setArguments(bundle);
            replaceFragment((SpecificFragmentGeneral<T>) specificFragmentGeneral);
        }
    }

    private void replaceFragment(SpecificFragmentGeneral<T> specificFragmentGeneral) {
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, specificFragmentGeneral, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return itemInfoList.size();
    }
}
