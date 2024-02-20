package com.talshavit.my_wishlist.TvShow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.List;

public class SpecificTvShowFragment extends Fragment {

    private ImageView tvShowImageView, imageBackground;
    private TextView tvShowTitle, tvShowNumSeries, tvShowReleaseYear, tvShowOverview, tvShowGenre;
    private ImageButton backButton;

    private WebView webView;
    private ScrollView scrollView;

    private Button deleteTvShow, watchedTvShowButton;

    private DatabaseReference databaseReference;

    public SpecificTvShowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_tv_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        //initView();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initView(arguments);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void initView(Bundle arguments) {
        TvShowInfo tvShowInfo = (TvShowInfo) arguments.getSerializable("TVSHOW_INFO");
        int tvShowID = tvShowInfo.getTvShowID();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows").child(String.valueOf(tvShowID));

        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+tvShowInfo.getImageUrl()).into(tvShowImageView);
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+tvShowInfo.getImageUrl()).into(imageBackground);
        tvShowTitle.setText(tvShowInfo.getTvShowName());
        tvShowNumSeries.setText(tvShowInfo.getNumOfSeasons()+"");
        tvShowReleaseYear.setText(tvShowInfo.getReleaseYear());

        if(tvShowInfo.getOverview().equals(""))
            tvShowOverview.setText("There is no overview");
        else
            tvShowOverview.setText(tvShowInfo.getOverview());

        List<String> genres = tvShowInfo.getGenres();
        String formattedGenres = formatGenres(genres);
        tvShowGenre.setText(formattedGenres);

        String trailerKey = tvShowInfo.getTrailer();
        if (trailerKey == null || trailerKey.isEmpty() || trailerKey.equals("") ){
            webView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            layoutParams.height = (int) (370 * getResources().getDisplayMetrics().density);
            //layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // Apply the updated layout parameters
            scrollView.setLayoutParams(layoutParams);
        }
        else{
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());

            // Load URL with trailer key
            String url = "https://www.youtube.com/embed/" + trailerKey;
            webView.loadUrl(url);
        }

        deleteTvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("DELETE TV SHOW");
                builder.setMessage("Do you want to delete \"" + tvShowInfo.getTvShowName().toUpperCase() + "\"?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTvFromFirebase(tvShowID);
                        requireActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        watchedTvShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvShowInfo.setWatched(true);
                databaseReference.child("watched").setValue(true);
            }
        });

        databaseReference.child("watched").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isWatched = snapshot.getValue(Boolean.class);
                if(isWatched != null && isWatched)
                    watchedTvShowButton.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteTvFromFirebase(int tvShowID) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference specificTvShowReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows").child(tvShowID+"");
        specificTvShowReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
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

    private void findViews(View view) {
        tvShowImageView = view.findViewById(R.id.tvShowImageView);
        imageBackground = view.findViewById(R.id.imageBackground);
        tvShowTitle = view.findViewById(R.id.tvShowTitle);
        tvShowNumSeries = view.findViewById(R.id.tvShowNumSeries);
        tvShowReleaseYear = view.findViewById(R.id.tvShowReleaseYear);
        tvShowOverview = view.findViewById(R.id.tvShowOverview);
        tvShowGenre = view.findViewById(R.id.tvShowGenre);
        backButton = view.findViewById(R.id.backButton);
        webView = view.findViewById(R.id.webView);
        scrollView = view.findViewById(R.id.scrollView);
        deleteTvShow = view.findViewById(R.id.deleteTvShow);
        watchedTvShowButton = view.findViewById(R.id.watchedTvShowButton);
    }
}