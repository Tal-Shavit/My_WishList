package com.talshavit.my_wishlist.GeneralHelpers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Media.MediaType;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class MyAdapterAllItems<T extends GenerealInterfaces> extends RecyclerView.Adapter<MyViewHolderAllItems<T>> {

    private Context context;
    private Context context1;
    private List<T> itemInfoList;
    private MediaType mediaType;

    private YouTubePlayerView youTubePlayerView;
    private int selectedPosition = RecyclerView.NO_POSITION; //Initially, no item is selected

    public MyAdapterAllItems(Context context, Context context1, List<T> itemInfoList, MediaType mediaType) {
        this.context = context;
        this.itemInfoList = itemInfoList;
        this.context1 = context1;
        this.mediaType = mediaType;
    }


    @NonNull
    @Override
    public MyViewHolderAllItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderAllItems(LayoutInflater.from(context).inflate(R.layout.recycler_all_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderAllItems holder, int position) {
        getTitle(holder, position);
        getOverview(holder, position);
        holder.lenghtTxt.setText(itemInfoList.get(position).getLenght());
        getImg(holder, position);
        getGenres(holder, position);
        changeCardView(holder, position);
        onImgButton(holder, position);
        onTrailer(holder, position);
        onBackButton(holder, position);
        updateSeenImageViewVisibility(holder, position);
    }

    private void updateSeenImageViewVisibility(MyViewHolderAllItems holder, int position) {
        if (itemInfoList.get(position).isWatched()) {
            holder.seenImageView.setVisibility(View.VISIBLE);
        } else {
            holder.seenImageView.setVisibility(View.GONE);
        }
    }

    private void onBackButton(MyViewHolderAllItems holder, int position) {
        holder.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.textCardView.setVisibility(View.INVISIBLE);
                holder.imageCardView.setVisibility((View.VISIBLE));
            }
        });
    }

    private void getImg(MyViewHolderAllItems holder, int position) {
        String imageUrl = itemInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/" + imageUrl).into(holder.imageButton);
    }

    private void getOverview(MyViewHolderAllItems holder, int position) {
        String overview = itemInfoList.get(position).getOverview();
        if (overview.equals("") || overview.isEmpty())
            holder.overviewTxt.setText("There is no overview");
        else
            holder.overviewTxt.setText(overview);
    }

    private void getTitle(MyViewHolderAllItems holder, int position) {
        String title = "";
        if (mediaType == MediaType.MOVIES) {
            if (itemInfoList.get(position).getReleaseYear() != "")
                title = itemInfoList.get(position).getName() + " (" + itemInfoList.get(position).getReleaseYear() + ")";
            else
                title = itemInfoList.get(position).getName();
        } else if (mediaType == MediaType.TV_SHOWS) {
            title = itemInfoList.get(position).getName();
        }
        holder.titleTxt.setText(title);
        holder.titleTextView.setText(title);
    }

    private void getGenres(MyViewHolderAllItems holder, int position) {
        List<String> genres = itemInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        holder.genreTxt.setText(formattedGenres);
    }

    private void changeCardView(MyViewHolderAllItems holder, int position) {
        if (position == selectedPosition) {
            holder.textCardView.setVisibility(View.VISIBLE);
            holder.imageCardView.setVisibility(View.INVISIBLE);
        } else {
            holder.textCardView.setVisibility(View.INVISIBLE);
            holder.imageCardView.setVisibility((View.VISIBLE));
        }
    }

    private void onImgButton(MyViewHolderAllItems holder, int position) {
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                notifyDataSetChanged();  // Notify the adapter to rebind views
            }
        });
    }

    private void onTrailer(MyViewHolderAllItems holder, int position) {
        String trailerKey = itemInfoList.get(position).getTrailer();
        if (trailerKey == null || trailerKey.isEmpty()) {
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

        //Dismiss the dialog when touched outside of it
        dialog.setCanceledOnTouchOutside(true);

        youTubePlayerView = dialog.findViewById(R.id.youTubePlayer);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(trailerKey, 0);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                youTubePlayerView.release();
            }
        });

        dialog.show();
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
