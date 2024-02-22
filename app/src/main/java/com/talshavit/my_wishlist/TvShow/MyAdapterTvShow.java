package com.talshavit.my_wishlist.TvShow;

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

import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class MyAdapterTvShow extends RecyclerView.Adapter<MyViewHolderTvShow> {

    private Context context;
    private Context context1;
    public List<TvShowInfo> tvShowInfoList;

    private int selectedPosition = RecyclerView.NO_POSITION; // Initially, no item is selected

    public MyAdapterTvShow(Context context, Context context1, List<TvShowInfo> tvShowInfoList) {
        this.context = context;
        this.tvShowInfoList = tvShowInfoList;
        this.context1 = context1;
    }

    @NonNull
    @Override
    public MyViewHolderTvShow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderTvShow(LayoutInflater.from(context).inflate(R.layout.recycler_all_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTvShow holder, int position) {
        String title = tvShowInfoList.get(position).getTvShowName();
        holder.titleTxt.setText(title);

        holder.titleTextView.setText(title);

        String overview = tvShowInfoList.get(position).getOverview();
        if(overview.equals(""))
            holder.overviewTxt.setText("There is no overview");
        else
            holder.overviewTxt.setText(overview);

        holder.lenghtTxt.setText(tvShowInfoList.get(position).getNumOfSeasons());

        String imageUrl = tvShowInfoList.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.imageButton);

        List<String> genres = tvShowInfoList.get(position).getGenres();
        String formattedGenres = formatGenres(genres);
        holder.genreTxt.setText(formattedGenres);

        if (position == selectedPosition) {
            holder.textCardView.setVisibility(View.VISIBLE);
            holder.imageCardView.setVisibility(View.INVISIBLE);
        } else {
            holder.textCardView.setVisibility(View.INVISIBLE);
            holder.imageCardView.setVisibility((View.VISIBLE));
        }
        if(tvShowInfoList.get(position).isWatched()){
            holder.seenImageView.setVisibility(View.VISIBLE);
        }

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;  // Update the selected position
                notifyDataSetChanged();  // Notify the adapter to rebind views

            }
        });

        String trailerKey = tvShowInfoList.get(position).getTrailer();
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
        WebView webView = dialog.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

            // Load URL with trailer key
        String url = "https://www.youtube.com/embed/" + trailerKey;
        webView.loadUrl(url);
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
        return tvShowInfoList.size();
    }
}