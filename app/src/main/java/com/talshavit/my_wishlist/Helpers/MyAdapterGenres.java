package com.talshavit.my_wishlist.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.List;

public  class MyAdapterGenres extends RecyclerView.Adapter<MyAdapterGenres.MyAdapterGenresViewHolder> {

    private Context context;

    private List<String> allGenres;

    private List<MovieInfo> allMoviesByGenre;

    private GenreClickListener genreClickListener;
    private DatabaseReference databaseReference;
    private MyAdapterSpecificGenre myAdapterSpecificGenre;

    public MyAdapterGenres(Context context, List<String> allGenres, GenreClickListener genreClickListener) {
        this.context = context;
        this.allGenres = allGenres;
        this.genreClickListener = genreClickListener;
    }

    @NonNull
    @Override
    public MyAdapterGenresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_by_genres_item,parent,false);
        return new MyAdapterGenresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterGenresViewHolder holder, int position) {
        allMoviesByGenre = new ArrayList<MovieInfo>();
        String genre = allGenres.get(position);
        holder.genreButton.setText(genre);

        holder.genreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = allGenres.get(position);
                if (genreClickListener != null) {
                    genreClickListener.onGenreClick(genre);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allGenres.size();
    }

    public interface GenreClickListener {
        void onGenreClick(String genre);
    }

    public class MyAdapterGenresViewHolder extends RecyclerView.ViewHolder{
        private Button genreButton;

        public MyAdapterGenresViewHolder(@NonNull View itemView) {
            super(itemView);
            genreButton = itemView.findViewById(R.id.genreButton);
        }
    }
}
