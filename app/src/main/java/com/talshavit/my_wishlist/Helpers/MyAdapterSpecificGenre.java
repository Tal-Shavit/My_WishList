package com.talshavit.my_wishlist.Helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.Movie.MyViewHolderMovie;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class MyAdapterSpecificGenre extends RecyclerView.Adapter<MyViewHolderSpecificGenre> {

    private Context context;
    public List<MovieInfo> movieInfoList;
    private int selectedPosition = RecyclerView.NO_POSITION; // Initially, no item is selected

    public MyAdapterSpecificGenre(Context context, List<MovieInfo> movieInfoList) {
        this.context = context;
        this.movieInfoList = movieInfoList;
    }

    @NonNull
    @Override
    public MyViewHolderSpecificGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSpecificGenre(LayoutInflater.from(context).inflate(R.layout.recycler_specific_genre,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSpecificGenre holder, int position) {
        String title = movieInfoList.get(position).getMovieName() + " (" + movieInfoList.get(position).getReleaseYear() +")";
        holder.movieTitle.setText(title);

        holder.movieTextView.setText(title);

        String overview = movieInfoList.get(position).getOverview();
        if(overview.equals(""))
            holder.movieDescription.setText("There is no overview");
        else
            holder.movieDescription.setText(overview);

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

        String trailerKey = movieInfoList.get(position).getTrailer();
        if (trailerKey == null || trailerKey.isEmpty()){
            holder.trailerImageButton.setVisibility(View.INVISIBLE);
        }
        holder.trailerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrailerInWebBrowser(trailerKey);
            }
        });
    }

    private void openTrailerInWebBrowser(String trailerKey) {
        if (trailerKey != null && !trailerKey.isEmpty()) {
            String trailerUrl = "https://www.youtube.com/watch?v=" + trailerKey;
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            myIntent.setPackage("com.android.chrome");
            context.startActivity(myIntent);
        } else {
            // Handle the case where no trailer key is available
            Toast.makeText(context, "No trailer available", Toast.LENGTH_SHORT).show();
        }
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
