package com.talshavit.my_wishlist.GeneralHelpers;

import android.content.Context;
import android.os.Bundle;
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

public class MyAdapterSpecificGenge <T extends GenerealInterfaces> extends RecyclerView.Adapter<MyViewHolderSpecificGenre> {

    private Context context;

    private List<T> itemInfoList;

    private String imageUrl;

    private FragmentManager fragmentManager;

    private String itemType;

    public MyAdapterSpecificGenge(Context context, List<T> itemInfoList, FragmentManager fragmentManager,String itemType) {
        this.context = context;
        this.itemInfoList = itemInfoList;
        this.fragmentManager = fragmentManager;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public MyViewHolderSpecificGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSpecificGenre(LayoutInflater.from(context).inflate(R.layout.recycler_specific_genre,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSpecificGenre holder, int position) {
        holder.titleTextView.setText(itemInfoList.get(position).getName());
        imageUrl = itemInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.imageButton);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpecificItem(position);
            }
        });

        if(itemInfoList.get(position).isWatched()){
            holder.seenImageView.setVisibility(View.VISIBLE);
        }
    }

    private void showSpecificItem(int position) {
        T item = itemInfoList.get(position);
        imageUrl = item.getImageUrl();
        String title = item.getName();
        String lenght = item.getLenght();
        String releaseYear = item.getReleaseYear();
        String overview = item.getOverview();
        List<String> genres = item.getGenres();
        String trailerKey = item.getTrailer();
        int ID = item.getID();
        boolean isWatched = item.isWatched();

        Bundle bundle=new Bundle();

        if(itemType.equals("tv shows")){
            TvShowInfo tvShowInfo = new TvShowInfo(ID, title, imageUrl, releaseYear,lenght,genres,overview,trailerKey, isWatched);
            bundle.putSerializable("MEDIA_INFO", tvShowInfo);
            SpecificFragmentGeneral<TvShowInfo> specificFragmentGeneral = new SpecificFragmentGeneral<>("tv shows");
            specificFragmentGeneral.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, specificFragmentGeneral, null)
                    .setReorderingAllowed(true).addToBackStack(null)
                    .commit();
        }
       else{
            MovieInfo movieInfo = new MovieInfo(ID,title,releaseYear,imageUrl,lenght,genres,overview,trailerKey, isWatched);
            bundle.putSerializable("MEDIA_INFO", movieInfo);
            SpecificFragmentGeneral<MovieInfo> specificFragmentGeneral = new SpecificFragmentGeneral<>("movies");
            specificFragmentGeneral.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, specificFragmentGeneral, null)
                    .setReorderingAllowed(true).addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public int getItemCount() {
        return itemInfoList.size();
    }
}
