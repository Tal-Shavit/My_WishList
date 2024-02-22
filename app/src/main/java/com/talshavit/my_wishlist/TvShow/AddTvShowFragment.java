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
import com.talshavit.my_wishlist.Movie.MovieFragment;
import com.talshavit.my_wishlist.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddTvShowFragment extends Fragment {
    private DatabaseReference databaseReference;
    private EditText titleEditText;
    private ImageButton titleButton;
    private ImageView tvImageView;
    private static Button addButton;
    private static Spinner dynamicSpinner;
    private ArrayAdapter<String> adapter;
    private static String tvShowName,imageUrl,releaseYear, overview,trailer,numOfSeasons;
    private static int tvShowID;
    private static List<String> genres;
    private static int nextID;


    public AddTvShowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_tv_show, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initView();
    }

    private void initView() {
        checkLastSerialNumber();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);

        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if(!title.equals("")){
                    TmdbApiClientTvShow.title = title;
                    dynamicSpinner.setVisibility(View.INVISIBLE);
                    tvImageView.setVisibility(View.INVISIBLE);

                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


                    adapter.clear();
                    adapter.notifyDataSetChanged();

                    new MyAsyncTask(AddTvShowFragment.this, progressDialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else
                    Toast.makeText(getContext(), "YOU HAVE TO FILL THE TITLE!", Toast.LENGTH_SHORT).show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
                TvShowInfo tvShowInfo = new TvShowInfo(tvShowID, tvShowName, imageUrl, releaseYear,numOfSeasons,genres,overview,trailer,false);
                tvShowInfo.setUserID(userID);
                tvShowInfo.setSerialID(nextID);

                databaseReference.child(tvShowID+"").setValue(tvShowInfo);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, TvShowsFragment.class, null)
                        .setReorderingAllowed(true).addToBackStack(null)
                        .commit();

            }
        });

    }


    private void findViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        titleButton = view.findViewById(R.id.titleButton);
        dynamicSpinner = view.findViewById(R.id.dynamicSpinner);
        tvImageView = view.findViewById(R.id.tvImageView);
        addButton = view.findViewById(R.id.addButton);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, List<TvShowInfo>> {
        private final WeakReference<AddTvShowFragment> fragmentReference;
        private ProgressDialog progressDialog;
        MyAsyncTask(AddTvShowFragment fragment, ProgressDialog progressDialog) {
            this.fragmentReference = new WeakReference<>(fragment);
            this.progressDialog = progressDialog;
        }

        @Override
        protected List<TvShowInfo> doInBackground(Void... voids) {
            try {
                Log.d("TvShowName", "doInBackground executed");
                return TmdbApiClientTvShow.getAllPopularTvShows();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TvShowName", "Exception in doInBackground: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<TvShowInfo> tvShowInfos) {
            com.talshavit.my_wishlist.TvShow.AddTvShowFragment fragment = fragmentReference.get();
            if (fragment != null) {
                Log.d("TvShowName", "Fragment is still valid");
                Log.d("TvShowName", "onPostExecute executed");

                progressDialog.dismiss();

                if (tvShowInfos != null && !tvShowInfos.isEmpty()) {
                    for (TvShowInfo tvShowInfo : tvShowInfos) {
                        fragment.adapter.add(tvShowInfo.getTvShowName());
                    }
                    fragment.adapter.notifyDataSetChanged();  // Notify adapter after adding all items

                    fragment.dynamicSpinner.setVisibility(View.VISIBLE);
                    fragment.tvImageView.setVisibility(View.VISIBLE);

                    dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + tvShowInfos.get(position).getImageUrl()).into(fragment.tvImageView);
                            addButton.setVisibility(View.VISIBLE);
                            tvShowID = tvShowInfos.get(position).getTvShowID();
                            tvShowName = tvShowInfos.get(position).getTvShowName();
                            numOfSeasons = tvShowInfos.get(position).getNumOfSeasons();
                            imageUrl = tvShowInfos.get(position).getImageUrl();
                            releaseYear = tvShowInfos.get(position).getReleaseYear();
                            genres = tvShowInfos.get(position).getGenres();
                            overview = tvShowInfos.get(position).getOverview();
                            trailer = tvShowInfos.get(position).getTrailer();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    Toast.makeText(fragment.getContext(), "No matches found!", Toast.LENGTH_SHORT).show();
                    fragment.titleEditText.setText("");
                }

            }else {
                Log.e("TvShowName", "Fragment reference is null");
            }
        }
    }

    private void checkLastSerialNumber() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userID).child("tv shows");
        databaseReference.orderByChild("serialID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TvShowInfo lastAddedTvShow = snapshot.getValue(TvShowInfo.class);
                    if (lastAddedTvShow != null) {
                        nextID = lastAddedTvShow.getSerialID()+1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
