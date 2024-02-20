package com.talshavit.my_wishlist.TvShow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.Movie.MyAdapterMovie;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShowHelpers.MyAdapterSpecificGenre;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterGenre extends RecyclerView.Adapter<MyViewHolderGenre> {

    private Context context;

    private List<String> allGenres;
    private List<TvShowInfo> allTvShowItems;
    private List<TvShowInfo> allTvShowByGenre;
    private MyAdapterSpecificGenre myAdapterSpecificGenre;

    private String selectedGenre;

    private FragmentManager fragmentManager;

    public MyAdapterGenre(Context context, List<String> allGenres, List<TvShowInfo> allTvShowItems, FragmentManager fragmentManager) {
        this.context = context;
        this.allGenres = allGenres;
        this.allTvShowItems = allTvShowItems;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public MyViewHolderGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_tv_shows_genres,parent,false);
        return new MyViewHolderGenre(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderGenre holder, int position) {
        String genre = allGenres.get(position);
        holder.genreTextView.setText(genre);

        allTvShowByGenre = new ArrayList<TvShowInfo>();
        for (int i = 0; i < allTvShowItems.size(); i++) {
            List<String> tvShowsGenres = allTvShowItems.get(i).getGenres();

            if (tvShowsGenres != null) {
                if (tvShowsGenres.contains(genre)) {
                    if (!(allTvShowByGenre.contains(allTvShowItems.get(i))))
                        allTvShowByGenre.add(allTvShowItems.get(i));
                }
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
            holder.recyclerSpecificGenre.setLayoutManager(linearLayoutManager);

//            // Disable nested scrolling for the inner RecyclerView
//            holder.recyclerSpecificGenre.setNestedScrollingEnabled(false);

            myAdapterSpecificGenre = new MyAdapterSpecificGenre(context, allTvShowByGenre, fragmentManager);//,requireContext(),
            holder.recyclerSpecificGenre.setAdapter(myAdapterSpecificGenre);


        }
        //setSwipeToDelete();
    }

    @Override
    public int getItemCount() {
        return allGenres.size();
    }
}

