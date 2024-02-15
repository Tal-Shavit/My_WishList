package com.talshavit.my_wishlist.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.Movie.MyViewHolderMovie;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.SpecificMovieFragment;

import java.util.List;

public class MyAdapterSpecificGenre extends RecyclerView.Adapter<MyViewHolderSpecificGenre> {

    private Context context;
    private FragmentManager fragmentManager;
    public List<MovieInfo> movieInfoList;
    private String imageUrl;
    private int selectedPosition = RecyclerView.NO_POSITION; // Initially, no item is selected


    public MyAdapterSpecificGenre(Context context, FragmentManager fragmentManager, List<MovieInfo> movieInfoList) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.movieInfoList = movieInfoList;

    }

    @NonNull
    @Override
    public MyViewHolderSpecificGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSpecificGenre(LayoutInflater.from(context).inflate(R.layout.recycler_specific_genre,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSpecificGenre holder, int position) {
        holder.movieTextView.setText(movieInfoList.get(position).getMovieName());

        imageUrl = movieInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.movieImageButton);

        holder.movieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                showSpecificMovie(selectedPosition);
                notifyDataSetChanged();  // Notify the adapter to rebind views

            }
        });
    }

    private void showSpecificMovie(int position) {
        imageUrl = movieInfoList.get(position).getImageUrl();
        String title = movieInfoList.get(position).getMovieName();
        String lenght = movieInfoList.get(position).getMovieLenght();
        String releaseYear = movieInfoList.get(position).getReleaseYear();
        String overview = movieInfoList.get(position).getOverview();
        List<String> genres = movieInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        String trailerKey = movieInfoList.get(position).getTrailer();

        Bundle bundle=new Bundle();
        bundle.putString("IMGURL",imageUrl);
        bundle.putString("TITLE",title);
        bundle.putString("LENGHT",lenght);
        bundle.putString("YEAR",releaseYear);
        bundle.putString("OVERVIEW",overview);
        bundle.putString("GENRE", formattedGenres);
        if (trailerKey == null || trailerKey.isEmpty()){
            bundle.putString("TRAILER", "");
        }
        else
            bundle.putString("TRAILER", trailerKey);

        SpecificMovieFragment specificMovieFragment = new SpecificMovieFragment();
        specificMovieFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, specificMovieFragment, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();

    }

    private String formatGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return "There is no genres"; //When genres is null or empty
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String genre : genres) {
            stringBuilder.append(genre).append(", ");
        }

        // Remove the trailing comma and space
        if (stringBuilder.length() > 1) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }
}
