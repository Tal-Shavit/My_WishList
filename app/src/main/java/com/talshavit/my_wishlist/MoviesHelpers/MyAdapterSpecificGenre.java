package com.talshavit.my_wishlist.MoviesHelpers;

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
import com.talshavit.my_wishlist.Movie.SpecificMovieFragment;

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
                selectedPosition = position;  //Update the selected position
                showSpecificMovie(selectedPosition);
                notifyDataSetChanged();  //Notify the adapter to rebind views

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
        String trailerKey = movieInfoList.get(position).getTrailer();
        int movieID = movieInfoList.get(position).getMovieID();
        boolean isWatched = movieInfoList.get(position).isWatched();

        MovieInfo movieInfo = new MovieInfo(movieID,title,releaseYear,imageUrl,lenght,genres,overview,trailerKey, isWatched);

        Bundle bundle=new Bundle();
        bundle.putSerializable("MOVIE_INFO", movieInfo);

        SpecificMovieFragment specificMovieFragment = new SpecificMovieFragment();
        specificMovieFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, specificMovieFragment, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return movieInfoList.size();
    }
}
