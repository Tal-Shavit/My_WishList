package com.talshavit.my_wishlist.Book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class MyAdapterMovie extends RecyclerView.Adapter<MyViewHolderMovie> {

    private Context context;
    public List<MovieInfo> movieInfoList;

    //private Dialog dialogMovieDetails;
    //TextView movieTitle1, movieDescription, movieGenre, movieLenght;

    private int selectedPosition = RecyclerView.NO_POSITION; // Initially, no item is selected

    public MyAdapterMovie(Context context, List<MovieInfo> movieInfoList) {
        this.context = context;
        this.movieInfoList = movieInfoList;
    }

    @NonNull
    @Override
    public MyViewHolderMovie onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderMovie(LayoutInflater.from(context).inflate(R.layout.recycler_movie_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderMovie holder, int position) {
        holder.movieTitle.setText(movieInfoList.get(position).getMovieName());
        holder.movieDescription.setText(movieInfoList.get(position).getOverview());

        holder.movieLenght.setText(movieInfoList.get(position).getMovieLenght());

        String imageUrl = movieInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.movieImageButton);

        List<String> genres = movieInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        holder.movieGenre.setText(formattedGenres);

        if (position == selectedPosition) {
            holder.textCardView.setVisibility(View.VISIBLE);
            holder.imageCardView.setVisibility(View.INVISIBLE);
        } else {
            holder.textCardView.setVisibility(View.INVISIBLE);
            holder.imageCardView.setVisibility((View.VISIBLE));
        }


        holder.movieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                notifyDataSetChanged();  // Notify the adapter to rebind views

            }
        });

//        holder.bookImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //selectBook.onItemBookClicked(bookItems.get(position));
//                selectedPosition = position;// Update the selected position
//                notifyDataSetChanged(); // Notify the adapter to rebind views
//            }
//        });

    }


    private String formatGenres(List<String> genres) {
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


