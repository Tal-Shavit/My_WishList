package com.talshavit.my_wishlist.GeneralHelpers;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.ArrayList;
import java.util.List;

public class SpecificFragmentGeneral<T extends GenerealInterfaces> extends Fragment{

    private ImageView imageView, imageBackground;
    private TextView titleTxt, lenghtTxt, releaseYearTxt, overviewTxt, genreTxt;
    private int serialID;
    private ImageButton backButton;
    private YouTubePlayerView youTubePlayerView;
    private ScrollView scrollView;
    private Button deleteButton, watchedButton, notWatchedButton;

    private DatabaseReference databaseReference, databaseReferenceAllItems;

    GeneralFunctions<T> generalFunctions = new GeneralFunctions<>(getContext());

    private String itemType, userID;

    private T mediaInfo;
    private FirebaseAnalytics firebaseAnalytics;

    public SpecificFragmentGeneral() {
        // Required empty public constructor
    }

    public SpecificFragmentGeneral(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specific_general, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        findViews(view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            initView(arguments);
        }
        onBackButton();
    }

    private void onBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.release();
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void initView(Bundle arguments) {
        mediaInfo = (T) arguments.getSerializable("MEDIA_INFO");
        serialID = (int) arguments.getSerializable("SERIAL_ID");
        setImgs();
        titleTxt.setText(mediaInfo.getName());
        lenghtTxt.setText(mediaInfo.getLenght());
        releaseYearTxt.setText(mediaInfo.getReleaseYear());
        setOverview();
        setGenres();
        setTrailer();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(itemType).child(serialID+"");
        databaseReferenceAllItems = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(itemType);
        onDeleteButton();
        onWatchButton();
        onNotWatchButton();
        changeWatchButtonVisibility();
    }

    private void setImgs() {
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+mediaInfo.getImageUrl()).into(imageView);
        Picasso.get().load("https://image.tmdb.org/t/p/w500/"+mediaInfo.getImageUrlBackground()).into(imageBackground);
    }

    private void setTrailer() {
        String trailerKey = mediaInfo.getTrailer();
        if (trailerKey == null || trailerKey.isEmpty() || trailerKey.equals("") ){
            youTubePlayerView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            layoutParams.height = (int) (370 * getResources().getDisplayMetrics().density);
            scrollView.setLayoutParams(layoutParams);
        }
        else{
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    super.onReady(youTubePlayer);
                    youTubePlayer.loadVideo(trailerKey, 0);
                }
            });
        }
    }

    private void setGenres() {
        List<String> genres = mediaInfo.getGenres();
        String formattedGenres = generalFunctions.formatGenres(genres);
        genreTxt.setText(formattedGenres);
    }

    private void setOverview() {
        if(mediaInfo.getOverview().equals(""))
            overviewTxt.setText("There is no overview");
        else
            overviewTxt.setText(mediaInfo.getOverview());
    }

    private void changeWatchButtonVisibility() {
        databaseReference.child("watched").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean isWatched = snapshot.getValue(Boolean.class);
                if(isWatched != null && isWatched){
                    watchedButton.setVisibility(View.GONE);
                    notWatchedButton.setVisibility(View.VISIBLE);
                }
                else if(isWatched != null && !isWatched){
                    watchedButton.setVisibility(View.VISIBLE);
                    notWatchedButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onNotWatchButton() {
        notWatchedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogFunc("REMOVE FROM WATCH LIST", "Remove \"", "remove", itemType);
            }
        });
    }

    private void onWatchButton() {
        watchedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemType.equals("movies"))
                    AlertDialogFunc("WATCHED THIS MOVIE","Did you watched \"", "add", itemType);
                else
                    AlertDialogFunc("WATCHED THIS TV SHOW","Did you watched \"", "add", itemType);

            }
        });
    }

    private void onDeleteButton() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemType.equals("movies"))
                    AlertDialogFunc("DELETE MOVIE","Do you want to delete \"", "delete", itemType);
                else
                    AlertDialogFunc("DELETE TV SHOW","Do you want to delete \"", "delete", itemType);
            }
        });
    }

    private void deleteFromFirebase(int itemID, String itemType) {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference specificTvShowReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(itemType).child(serialID+"");
        specificTvShowReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateSerialIds(userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void updateSerialIds(String userID) {
        databaseReferenceAllItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(itemType.equals("movies")){
                    List<MovieInfo> movies = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        MovieInfo movieInfo = snapshot.getValue(MovieInfo.class);
                        movies.add(movieInfo);
                    }
                    for (int i = 0; i < movies.size(); i++) {
                        movies.get(i).setSerialID(i);
                    }
                    databaseReferenceAllItems.setValue(movies);
                }
                else{
                    List<TvShowInfo> tvs = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        TvShowInfo tvShowInfo = snapshot.getValue(TvShowInfo.class);
                        tvs.add(tvShowInfo);
                    }
                    for (int i = 0; i < tvs.size(); i++) {
                        tvs.get(i).setSerialID(i);
                    }
                    databaseReferenceAllItems.setValue(tvs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AlertDialogFunc(String title,String messege, String removeOrAddOrDelete, String itemType){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage( messege + mediaInfo.getName().toUpperCase() +"\"?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickDialog(removeOrAddOrDelete);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
            }
        });
        builder.show();

    }

    private void onClickDialog(String removeOrAddOrDelete) {
        if (removeOrAddOrDelete.equals("add")) {
            mediaInfo.setWatched(true);
            databaseReference.child("watched").setValue(true);
            watchedButton.setVisibility(View.GONE);
            notWatchedButton.setVisibility(View.VISIBLE);
            logClickOnWatchEvent();
        } else if (removeOrAddOrDelete.equals("remove")){
            mediaInfo.setWatched(false);
            databaseReference.child("watched").setValue(false);
            watchedButton.setVisibility(View.VISIBLE);
            notWatchedButton.setVisibility(View.GONE);
        }
        else{
            deleteFromFirebase(serialID, itemType);
            logClickToDeleteEvent();
            youTubePlayerView.release();
            requireActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private void logClickOnWatchEvent() {
        Bundle params = new Bundle();
        params.putString("watch", "click_on_watch");
        firebaseAnalytics.logEvent("click_on_watch", params);
    }

    private void findViews(View view) {
        imageView = view.findViewById(R.id.imageView);
        imageBackground = view.findViewById(R.id.imageBackground);
        titleTxt = view.findViewById(R.id.titleTxt);
        lenghtTxt = view.findViewById(R.id.lenghtTxt);
        releaseYearTxt = view.findViewById(R.id.releaseYearTxt);
        overviewTxt = view.findViewById(R.id.overviewTxt);
        genreTxt = view.findViewById(R.id.genreTxt);
        backButton = view.findViewById(R.id.backButton);
        youTubePlayerView = view.findViewById(R.id.youTubePlayer);
        scrollView = view.findViewById(R.id.scrollView);
        deleteButton = view.findViewById(R.id.deleteButton);
        watchedButton = view.findViewById(R.id.watchedButton);
        notWatchedButton = view.findViewById(R.id.notWatchedButton);
    }

    private void logClickToDeleteEvent() {
        Bundle params = new Bundle();
        params.putString("delete", "click_to_delete");
        firebaseAnalytics.logEvent("click_to_delete_event", params);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        youTubePlayerView.release();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}