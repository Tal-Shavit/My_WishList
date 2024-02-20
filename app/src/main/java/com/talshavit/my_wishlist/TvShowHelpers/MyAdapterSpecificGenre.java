package com.talshavit.my_wishlist.TvShowHelpers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.SpecificTvShowFragment;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.List;

public class MyAdapterSpecificGenre extends RecyclerView.Adapter<MyViewHolderSpecificGenre> {

    private Context context;

    private List<TvShowInfo> tvShowInfos;

    private String imageUrl;

    private FragmentManager fragmentManager;

    public MyAdapterSpecificGenre(Context context, List<TvShowInfo> tvShowInfos, FragmentManager fragmentManager) {
        this.context = context;
        this.tvShowInfos = tvShowInfos;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolderSpecificGenre onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSpecificGenre(LayoutInflater.from(context).inflate(R.layout.recycler_specific_genre_tv_show,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSpecificGenre holder, int position) {
        holder.tvShowTextView.setText(tvShowInfos.get(position).getTvShowName());
        imageUrl = tvShowInfos.get(position).getImageUrl();
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+imageUrl).into(holder.tvShowImageButton);

        holder.tvShowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpecificMovie(position);
            }
        });
    }

    private void showSpecificMovie(int position) {
        imageUrl = tvShowInfos.get(position).getImageUrl();
        String title = tvShowInfos.get(position).getTvShowName();
        int numOfSeries = tvShowInfos.get(position).getNumOfSeasons();
        String releaseYear = tvShowInfos.get(position).getReleaseYear();
        String overview = tvShowInfos.get(position).getOverview();
        List<String> genres = tvShowInfos.get(position).getGenres();
        //String formattedGenres = formatGenres(genres);
        String trailerKey = tvShowInfos.get(position).getTrailer();
        int tvShowID = tvShowInfos.get(position).getTvShowID();
        boolean isWatched = tvShowInfos.get(position).isWatched();

        TvShowInfo tvShowInfo = new TvShowInfo(tvShowID, title, imageUrl, releaseYear,numOfSeries,genres,overview,trailerKey, isWatched);

        Bundle bundle=new Bundle();
        bundle.putSerializable("TVSHOW_INFO", tvShowInfo);
//        bundle.putString("IMGURL",imageUrl);
//        bundle.putString("TITLE",title);
//        bundle.putString("NUMSERIAL",numOfSeries);
//        bundle.putString("YEAR",releaseYear);
//        bundle.putString("OVERVIEW",overview);
//        bundle.putString("GENRE", formattedGenres);
//        if (trailerKey == null || trailerKey.isEmpty()){
//            bundle.putString("TRAILER", "");
//        }
//        else
//            bundle.putString("TRAILER", trailerKey);
//        bundle.putInt("ID", tvShowID);

        SpecificTvShowFragment specificTvShowFragment = new SpecificTvShowFragment();
        specificTvShowFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, specificTvShowFragment, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return tvShowInfos.size();
    }

}
