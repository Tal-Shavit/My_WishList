package com.talshavit.my_wishlist;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.Movie.MovieFragment;
import com.talshavit.my_wishlist.Settings.SettingFragment;
import com.talshavit.my_wishlist.TvShow.TvShowsFragment;
import com.talshavit.my_wishlist.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FrameLayout bannerFrame, frame_layout;

    private AdView adView;

    private RewardedAd rewardedAd;
    private Handler adHandler;
    private String userID;
    private DatabaseReference databaseReference;
    private final long adDelay = TimeUnit.MINUTES.toMillis(1); // 3 minutes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViews();
        replaceFragment(new LottieFragment());
        adsFunc();

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.movies:
                    replaceFragment(new MovieFragment());
                    break;

                case R.id.TvShows:
                    replaceFragment(new TvShowsFragment());
                    break;

                case R.id.Seen:
                    replaceFragment(new WatchedFragment());
                    break;

                case R.id.setting:
                    replaceFragment(new SettingFragment());
                    break;
            }
            return true;
        });
    }

    private void findViews() {
        frame_layout = findViewById(R.id.frame_layout);
        bannerFrame = findViewById(R.id.bannerFrame);
    }

    private void adsFunc() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("paymentForAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot exists and contains a value
                if (dataSnapshot.exists()) {
                    boolean paymentForAds = dataSnapshot.getValue(Boolean.class);
                    if (paymentForAds != true) {
                        loadAd();
                        //Initialize the handler
                        adHandler = new Handler(Looper.getMainLooper());
                        scheduleAdLoad();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void scheduleAdLoad() {
        adHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRewardedVideoAdv();
                scheduleAdLoad();
            }
        }, adDelay);
    }

    private void showRewardedVideoAdv() {
        if (rewardedAd != null) {
            Activity activityContext = MainActivity.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    private void loadRewardedVideoAdv() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd _rewardedAd) {
                        super.onAdLoaded(rewardedAd);
                        rewardedAd = _rewardedAd;
                        showRewardedVideoAdv();
                    }
                });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        AdView adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
        adView.setAdSize(AdSize.SMART_BANNER);
        bannerFrame.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to prevent memory leaks
        adHandler.removeCallbacksAndMessages(null);
    }

    public void disableAds() {
        databaseReference.setValue(true);

        //Remove banner ads
        bannerFrame.removeAllViews();

        //Stop loading rewarded ads and remove any pending callbacks
        if (rewardedAd != null) {
            rewardedAd = null;
        }
        if (adHandler != null) {
            adHandler.removeCallbacksAndMessages(null);
        }
    }

}