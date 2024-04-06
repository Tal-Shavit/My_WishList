package com.talshavit.my_wishlist.GeneralHelpers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class MyAdapterAllItems <T extends GenerealInterfaces> extends RecyclerView.Adapter<MyViewHolderAllItems<T>> {

    private Context context;
    private Context context1;
    private List<T> itemInfoList;

    private String itemType;

    private int selectedPosition = RecyclerView.NO_POSITION; //Initially, no item is selected

    public MyAdapterAllItems(Context context, Context context1, List<T> itemInfoList, String itemType) {
        this.context = context;
        this.itemInfoList = itemInfoList;
        this.context1 = context1;
        this.itemType = itemType;
    }


    @NonNull
    @Override
    public MyViewHolderAllItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderAllItems(LayoutInflater.from(context).inflate(R.layout.recycler_all_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderAllItems holder, int position) {
        String title;
        if(itemType.equals("movies")){
            if(itemInfoList.get(position).getReleaseYear()!="")
                title = itemInfoList.get(position).getName() + " (" + itemInfoList.get(position).getReleaseYear() +")";
            else
                title = itemInfoList.get(position).getName();
        }
        else
            title = itemInfoList.get(position).getName();

        holder.titleTxt.setText(title);
        holder.titleTextView.setText(title);
        String overview = itemInfoList.get(position).getOverview();
        if(overview.equals("") || overview.isEmpty())
            holder.overviewTxt.setText("There is no overview");
        else
            holder.overviewTxt.setText(overview);

        holder.lenghtTxt.setText(itemInfoList.get(position).getLenght());

        String imageUrl = itemInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.imageButton);

        List<String> genres = itemInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        holder.genreTxt.setText(formattedGenres);

        if (position == selectedPosition) {
            holder.textCardView.setVisibility(View.VISIBLE);
            holder.imageCardView.setVisibility(View.INVISIBLE);
        } else {
            holder.textCardView.setVisibility(View.INVISIBLE);
            holder.imageCardView.setVisibility((View.VISIBLE));
        }
        if(itemInfoList.get(position).isWatched()){
            holder.seenImageView.setVisibility(View.VISIBLE);
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                notifyDataSetChanged();  // Notify the adapter to rebind views
            }
        });

        String trailerKey = itemInfoList.get(position).getTrailer();
        if (trailerKey == null || trailerKey.isEmpty()){
            holder.trailerImageButton.setVisibility(View.INVISIBLE);
        }
        holder.trailerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrailerInWebView(trailerKey, holder.imageCardView, holder.textCardView);
            }
        });

    }

    private void openTrailerInWebView(String trailerKey, CardView imageCardView, CardView textCardView) {
        Dialog dialog = new Dialog(context1);
        dialog.setContentView(R.layout.dialog_trailer);

        YouTubePlayerView youTubePlayerView = dialog.findViewById(R.id.youTubePlayer);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(trailerKey, 0);
            }
        });

        dialog.show();

        textCardView.setVisibility(View.INVISIBLE);
        imageCardView.setVisibility((View.VISIBLE));

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
        return itemInfoList.size();
    }
}
