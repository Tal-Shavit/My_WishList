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

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.Movie.MovieFragment;
import com.talshavit.my_wishlist.Settings.SettingFragment;
import com.talshavit.my_wishlist.TvShow.TvShowsFragment;

import java.util.concurrent.TimeUnit;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation.ReselectListener;

public class MainActivity extends AppCompatActivity {

    private FrameLayout bannerFrame, frame_layout;
    private RewardedAd rewardedAd;
    private Handler adHandler;
    private String userID;
    private DatabaseReference databaseReference;
    private final long adDelay = TimeUnit.MINUTES.toMillis(1); // 3 minutes
    private FirebaseAnalytics firebaseAnalytics;
    private MeowBottomNavigation bottomNavigation;
    private ReselectListener reselectListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        findViews();
        replaceFragment(new LottieFragment());
        adsFunc();
        bottomNav();
    }

    private void bottomNav() {
        initBottomNav();

        bottomNavigation.setOnReselectListener(item -> {
            // Check if the listener is not null before invoking the method
            if (reselectListener != null)
                reselectListener.onReselectItem(item);
        });

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case 1:
                        replaceFragment(new MovieFragment());
                        break;
                    case 2:
                        replaceFragment(new TvShowsFragment());
                        break;
                    case 3:
                        replaceFragment(new WatchedFragment());
                        break;
                    case 4:
                        replaceFragment(new SettingFragment());
                        break;
                }
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
            }
        });
    }

    private void initBottomNav() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.movie));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.tvshow));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.view));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.baseline_settings_24));
    }


    private void findViews() {
        frame_layout = findViewById(R.id.frame_layout);
        bannerFrame = findViewById(R.id.bannerFrame);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void adsFunc() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("paymentForAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check if the dataSnapshot exists and contains a value
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
            loadRewardedVideoAdv(); // Load another rewarded ad if the current one wasn't ready
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