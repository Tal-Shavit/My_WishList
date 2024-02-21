package com.talshavit.my_wishlist.Movie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talshavit.my_wishlist.MoviesHelpers.MyAdapterGenres;
import com.talshavit.my_wishlist.MoviesHelpers.MyAdapterSpecificGenre;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MovieFragment extends Fragment implements MyAdapterGenres.GenreClickListener{
    private RecyclerView recyclerViewAll, recyclerViewGenresButtons, recyclerViewMoviesBySpecificGenre;
    private List<MovieInfo> allMoviesItems;
    private DatabaseReference databaseReference;
    private MyAdapterMovie myAdapterMovie;
    private MyAdapterGenres myAdapterGenres;
    private MyAdapterSpecificGenre myAdapterSpecificGenre;
    private Context context;
    private String userID;
    private List<String> genresList;
    private List<MovieInfo> allMoviesByGenre;
    private TextView genreTextView;
    private String selectedGenre;
    private FloatingActionButton addButton;

        public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        context = view.getContext();

        allMoviesItems = new ArrayList<MovieInfo>();
        myAdapterMovie = new MyAdapterMovie(getActivity().getApplicationContext(), requireContext(), allMoviesItems);
        initAdapter(recyclerViewAll, myAdapterMovie);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
                    allMoviesItems.add(movieInfo);
                }

                if (allMoviesItems.size() == 0) {
                    replaceFragment(new AddMovieFragment());
                } else {
                    //Sort the list based on serialID in descending order - last in show first
                    Collections.sort(allMoviesItems, new Comparator<MovieInfo>() {
                        @Override
                        public int compare(MovieInfo movie1, MovieInfo movie2) {
                            return Integer.compare(movie2.getSerialID(), movie1.getSerialID());
                        }
                    });
                    createGenres(allMoviesItems);
                    myAdapterMovie.notifyDataSetChanged();

                    if (selectedGenre != null)
                        updateGenreList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddMovieFragment());
            }
        });
        setSwipeToWatched("DELETE MOVIE","Do you want to delete \"",ItemTouchHelper.DOWN, "#85F32C2C", R.drawable.baseline_delete_24);
        setSwipeToWatched("WATCHED THIS MOVIE","Did you watched \"",ItemTouchHelper.UP, "#CDF4F269", R.drawable.baseline_remove_red_eye_24);
    }

    private void initAdapter(RecyclerView recyclerView, RecyclerView.Adapter myAdapter) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(myAdapter);
        }

    private void createGenres(List<MovieInfo> allMoviesItems) {
        genresList = new ArrayList<String>();
        for(int i=0; i<allMoviesItems.size(); i++){
            if (!(allMoviesItems.get(i).getGenres() == null) && !(allMoviesItems.get(i).getGenres().isEmpty())) {
                for (int j=0; j<allMoviesItems.get(i).getGenres().size(); j++) {
                    if (!(genresList.contains(allMoviesItems.get(i).getGenres().get(j)))) {
                        genresList.add(allMoviesItems.get(i).getGenres().get(j));
                    }
                }
            }
        }
        myAdapterGenres = new MyAdapterGenres(context, genresList,this);
        initAdapter(recyclerViewGenresButtons, myAdapterGenres);
    }

    private void findViews (View view){
        recyclerViewAll = view.findViewById(R.id.recyclerViewAllMovies);
        recyclerViewGenresButtons = view.findViewById(R.id.recyclerViewGenresButtons);
        recyclerViewMoviesBySpecificGenre = view.findViewById(R.id.recyclerViewMoviesBySpecificGenre);
        genreTextView = view.findViewById(R.id.genreTextView);
        addButton = view.findViewById(R.id.add_button);
        }
    @Override
    public void onGenreClick(String genre) {
            genreTextView.setText(genre.toUpperCase());
            selectedGenre = genre;
            if(selectedGenre != null)
                updateGenreList();
    }
    private void updateGenreList() {
            if(getActivity() != null && !getActivity().isFinishing()) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    genreTextView.setText(selectedGenre.toUpperCase());
                    allMoviesByGenre = new ArrayList<MovieInfo>();
                    for (int i = 0; i < allMoviesItems.size(); i++) {
                        List<String> movieGenres = allMoviesItems.get(i).getGenres();

                        if (movieGenres != null) {
                            if (movieGenres.contains(selectedGenre)) {
                                if (!(allMoviesByGenre.contains(allMoviesItems.get(i))))
                                    allMoviesByGenre.add(allMoviesItems.get(i));
                            }
                        }
                    }
                    myAdapterSpecificGenre = new MyAdapterSpecificGenre(context, fragmentManager, allMoviesByGenre);
                    initAdapter(recyclerViewMoviesBySpecificGenre, myAdapterSpecificGenre);
                }
            }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setSwipeToWatched(String title,String messege, int swipeDirection, String color, int drawable) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipeDirection) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                int position = viewHolder.getAdapterPosition();
                builder.setTitle(title);
                builder.setMessage(messege + allMoviesItems.get(position).getMovieName().toUpperCase() + "\"?");
                builder.setCancelable(false);
                if(swipeDirection  == ItemTouchHelper.DOWN){
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMovieFromFirebase(allMoviesItems.get(position).getMovieID());
                            allMoviesItems.remove(position);
                            myAdapterMovie.notifyItemRemoved(position);
                        }
                    });
                }else{
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            allMoviesItems.get(position).setWatched(true);
                            databaseReference.child(String.valueOf(allMoviesItems.get(position).getMovieID())).child("watched").setValue(true);
                            myAdapterMovie.notifyItemChanged(position);
                        }
                    });
                }

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myAdapterMovie.notifyItemChanged(position);
                    }
                });
                builder.show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                setWatchedIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, color, drawable);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerViewAll);
    }

    private void setWatchedIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, String color, int drawable) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor(color);
        Drawable viewDrawable = ContextCompat.getDrawable(getContext(), drawable);
        int intrinsicWidth = viewDrawable.getIntrinsicWidth();
        int intrinsicHeight = viewDrawable.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dY == 0 && !isCurrentlyActive;  // Check the vertical displacement (dY) instead of horizontal (dX)

        if (isCancelled) {
            c.drawRect((float) itemView.getLeft(), itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom(), mClearPaint);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getRight()-30, itemView.getBottom());
        mBackground.draw(c);

        int viewIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int viewIconBottom = viewIconTop + intrinsicHeight;
        // Calculate the horizontal center of the swiped item
        int centerX = (itemView.getRight() + itemView.getLeft()) / 2;
        // Calculate the half width of the delete icon
        int halfviewIconWidth = intrinsicWidth / 2;
        int additionalLeftMargin = 50;
        int viewIconLeft = centerX - halfviewIconWidth - additionalLeftMargin;
        int viewIconRight = centerX + halfviewIconWidth;

        viewDrawable.setBounds(viewIconLeft, viewIconTop, viewIconRight, viewIconBottom);
        viewDrawable.draw(c);
    }

    private void deleteMovieFromFirebase(int movieId) {
        DatabaseReference movieReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("movies").child(movieId+"");
        movieReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed to delet from firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
