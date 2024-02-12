package com.talshavit.my_wishlist.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    private String title;

    private TextView movieTitle;
    private TextView movieDescription;
    private TextView movieLenght;
    private TextView movieGenre;
    private Button button;
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
        holder.movieTextView.setText(movieInfoList.get(position).getMovieName());

        String imageUrl = movieInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.movieImageButton);

        holder.movieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                showDialog(selectedPosition);
                notifyDataSetChanged();  // Notify the adapter to rebind views

            }
        });
    }

    private void showDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_specific_movie);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        findViews(dialog);
        initDialog(position);
        dialog.show();
    }

    private void initDialog(int position) {
        String title = movieInfoList.get(position).getMovieName() + " (" + movieInfoList.get(position).getReleaseYear() +")";
        String lenght = movieInfoList.get(position).getMovieLenght();
        String overview = movieInfoList.get(position).getOverview();
        List<String> genres = movieInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        String trailerKey = movieInfoList.get(position).getTrailer();

        movieTitle.setText(title);
        if(overview.equals(""))
            movieDescription.setText("There is no overview");
        else
            movieDescription.setText(overview);
        movieLenght.setText(lenght);
        movieGenre.setText(formattedGenres);
        if (trailerKey == null || trailerKey.isEmpty()){
            button.setVisibility(View.INVISIBLE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrailerInWebBrowser(trailerKey);
            }
        });
    }

    private void findViews(Dialog dialog) {
        movieTitle = dialog.findViewById(R.id.movieTitle);
        movieDescription = dialog.findViewById(R.id.movieDescription);
        movieLenght = dialog.findViewById(R.id.movieLenght);
        movieGenre = dialog.findViewById(R.id.movieGenre);
        button = dialog.findViewById(R.id.trailer);
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
