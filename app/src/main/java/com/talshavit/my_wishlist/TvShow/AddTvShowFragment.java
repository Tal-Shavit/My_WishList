package com.talshavit.my_wishlist.TvShow;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.Interfaces.TrailerCallback;
import com.talshavit.my_wishlist.TvShow.Interfaces.TvInterfaceService;
import com.talshavit.my_wishlist.TvShow.Models.ResultForVideo;
import com.talshavit.my_wishlist.TvShow.Models.RootForSearch;
import com.talshavit.my_wishlist.TvShow.Models.RootForSpecificTv;
import com.talshavit.my_wishlist.TvShow.Models.RootForVideo;
import com.talshavit.my_wishlist.TvShow.Models.TvSearchResponse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTvShowFragment extends Fragment implements TrailerCallback {
    private DatabaseReference databaseReference;
    private EditText titleEditText;
    private ImageButton titleButton;
    private ImageView tvImageView;
    private Button addButton;
    private Spinner dynamicSpinner;
    private ArrayAdapter<String> adapter;
    private String tvShowName, imageUrl, releaseYear, overview, trailer, numOfSeasons;
    private int tvShowID;
    private List<String> genres;
    private static int nextID;
    private ProgressDialog progressDialog;

    private TvInterfaceService tvInterfaceService;


    public AddTvShowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_tv_show, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRetrofit();
        findViews(view);
        initView();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tvInterfaceService = retrofit.create(TvInterfaceService.class);
    }

    private void findViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        titleButton = view.findViewById(R.id.titleButton);
        dynamicSpinner = view.findViewById(R.id.dynamicSpinner);
        tvImageView = view.findViewById(R.id.tvImageView);
        addButton = view.findViewById(R.id.addButton);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);
    }

    private void initView() {
        checkLastSerialNumber();
        onTitleButtonClick();
        onAddButtonClick();
    }

    private void checkLastSerialNumber() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
        databaseReference.orderByChild("serialID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TvShowInfo lastAddedTvShow = snapshot.getValue(TvShowInfo.class);
                    if (lastAddedTvShow != null) {
                        nextID = lastAddedTvShow.getSerialID() + 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onTitleButtonClick() {
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if (!title.equals("")) {
                    dynamicSpinner.setVisibility(View.INVISIBLE);
                    tvImageView.setVisibility(View.INVISIBLE);

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    getAllSearchTv(title);
                } else
                    Toast.makeText(getContext(), "YOU HAVE TO FILL THE TITLE!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllSearchTv(String title) {
        tvInterfaceService.searchTv("e7bc0f9166ef27fb13b4271519c0b354", title, "popularity.desc", 1).enqueue(new Callback<TvSearchResponse>() {
            @Override
            public void onResponse(Call<TvSearchResponse> call, Response<TvSearchResponse> response) {
                progressDialog.dismiss();

                ArrayList<RootForSearch> tvShowInfos = response.body().results;
                if (tvShowInfos != null && !tvShowInfos.isEmpty()) {
                    for (RootForSearch tvShowInfo : tvShowInfos) {
                        adapter.add(tvShowInfo.name);
                    }
                    adapter.notifyDataSetChanged();//Notify adapter after adding all items

                    dynamicSpinner.setVisibility(View.VISIBLE);
                    tvImageView.setVisibility(View.VISIBLE);

                    dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tvShowID = tvShowInfos.get(position).id;
                            getTvDetails(tvShowID);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No matches found!", Toast.LENGTH_SHORT).show();
                    titleEditText.setText("");
                }
            }

            @Override
            public void onFailure(Call<TvSearchResponse> call, Throwable throwable) {
            }
        });
    }

    private void getTvDetails(int tvShowID) {
        tvInterfaceService.getTvDetails(tvShowID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForSpecificTv>() {
            @Override
            public void onResponse(Call<RootForSpecificTv> call, Response<RootForSpecificTv> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> genresStringList = new ArrayList<String>();
                    RootForSpecificTv specificTv = response.body();
                    Picasso.get().load("https://image.tmdb.org/t/p/w500/" + specificTv.poster_path).into(tvImageView);
                    addButton.setVisibility(View.VISIBLE);
                    tvShowName = specificTv.name;
                    int seasons = specificTv.number_of_seasons;
                    if(seasons > 1)
                        numOfSeasons = seasons+" seasons";
                    else{
                        numOfSeasons = seasons+" season";
                    }
                    imageUrl = specificTv.poster_path;
                    if (specificTv.first_air_date == null || specificTv.first_air_date.isEmpty())
                        releaseYear = "";
                    else
                        releaseYear = specificTv.first_air_date.substring(0, 4);
                    for (int i = 0; i < specificTv.genres.size(); i++) {
                        genresStringList.add(specificTv.genres.get(i).name);
                    }
                    genres = genresStringList;
                    overview = specificTv.overview;
                    getTvTrailerKey(tvShowID);
                }
            }

            @Override
            public void onFailure(Call<RootForSpecificTv> call, Throwable throwable) {
            }
        });
    }

    private void onAddButtonClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
                TvShowInfo tvShowInfo = new TvShowInfo(tvShowID, tvShowName, imageUrl, releaseYear, numOfSeasons, genres, overview, trailer, false);
                tvShowInfo.setUserID(userID);
                tvShowInfo.setSerialID(nextID);

                databaseReference.child(tvShowID + "").setValue(tvShowInfo);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, TvShowsFragment.class, null)
                        .setReorderingAllowed(true).addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getTvTrailerKey(int tvShowID) {
        Log.d("lala", tvShowID+"");
        tvInterfaceService.getVideoDetails(tvShowID, "e7bc0f9166ef27fb13b4271519c0b354").enqueue(new Callback<RootForVideo>() {
            @Override
            public void onResponse(Call<RootForVideo> call, Response<RootForVideo> response) {
                String trailerKey = null;
                String clipKey = null;

                for (int i = 0; i < response.body().results.size(); i++) {
                    ResultForVideo details = response.body().results.get(i);
                    if (details.type.toLowerCase().equals("trailer")) {
                        trailerKey = details.key;
                        break;
                    } else if (details.type.toLowerCase().equals("clip") && clipKey == null) {
                        //Store the first clip
                        clipKey = details.key;
                    }
                }

                //Use trailer if available, otherwise use the clip
                if (trailerKey != null) {
                    trailer = onTrailerLoaded(trailerKey);
                } else if (clipKey != null) {
                    trailer = onTrailerLoaded(clipKey);
                }
            }

            @Override
            public void onFailure(Call<RootForVideo> call, Throwable throwable) {
            }
        });
    }

    @Override
    public String onTrailerLoaded(String trailerKey) {
        return trailerKey;
    }
}
