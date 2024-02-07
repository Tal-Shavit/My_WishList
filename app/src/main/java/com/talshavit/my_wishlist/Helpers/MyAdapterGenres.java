package com.talshavit.my_wishlist.Helpers;//package com.talshavit.my_wishlist.Helpers;
//
//        import android.content.Context;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//
//        import androidx.annotation.NonNull;
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.RecyclerView;
//
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//        import com.talshavit.my_wishlist.Movie.MovieInfo;
//        import com.talshavit.my_wishlist.Movie.MyAdapterMovie;
//        import com.talshavit.my_wishlist.R;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//public class MyAdapterGenres extends RecyclerView.Adapter<MyViewHolderGenres>{
//
//    private Context context;
//
//    public List<String> allGenres;
//    public List<MovieInfo> allMoviesByGenre;
//
//    private View view;
//
//    private DatabaseReference databaseReference;
//
//    private RecyclerView recyclerViewSpecificGenre;
//    private MyAdapterSpecificGenre myAdapterSpecificGenre;
//
//
//    public MyAdapterGenres(Context context, List<String> allGenres) {
//        this.context = context;
//        this.allGenres = allGenres;
//        this.allMoviesByGenre = new ArrayList<>();
//        this.myAdapterSpecificGenre = new MyAdapterSpecificGenre(context, allMoviesByGenre);
//        recyclerViewSpecificGenre = view.findViewById(R.id.recyclerViewCpecificGenre);
//        //recyclerViewSpecificGenre = new RecyclerView(context);
//        initRecyclerView();
//    }
//
//    private void initRecyclerView() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
//        recyclerViewSpecificGenre.setLayoutManager(linearLayoutManager);
//        myAdapterSpecificGenre = new MyAdapterSpecificGenre(context, allMoviesByGenre);
//        recyclerViewSpecificGenre.setAdapter(myAdapterSpecificGenre);
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolderGenres onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        allMoviesByGenre = new ArrayList<MovieInfo>();
//        return new MyViewHolderGenres(LayoutInflater.from(context).inflate(R.layout.recycler_by_genres_item,parent,false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolderGenres holder, int position) {
//        String genre = allGenres.get(position);
//        holder.genreTextView.setText(genre);
//
//        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
//        databaseReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                allMoviesByGenre.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
//                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
//                    allMoviesByGenre.add(movieInfo);
//
//                    // Check if the movie belongs to the current genre
//                    if (movieInfo != null && movieInfo.getGenres() != null && movieInfo.getGenres().contains(genre)) {
//                        allMoviesByGenre.add(movieInfo);
//                    }
//                }
//
////                // Sort the list based on serialNumber
////                Collections.sort(allMoviesItems, new Comparator<MovieInfo>() {
////                    @Override
////                    public int compare(MovieInfo movie1, MovieInfo movie2) {
////                        return Integer.compare(movie1.getSerialNumber(), movie2.getSerialNumber());
////                    }
////                });
//                //myAdapterMovie.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        //initRecyclerView();
//    }
//
//    @Override
//    public int getItemCount() {
//        return allGenres.size();
//    }
//}

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;

import java.util.ArrayList;
import java.util.List;

public  class MyAdapterGenres extends RecyclerView.Adapter<MyAdapterGenres.MyAdapterGenresViewHolder> {

    private Context context;

    private List<String> allGenres;

    private List<MovieInfo> allMoviesByGenre;
    private List<MovieInfo> allMovies;

    private GenreClickListener genreClickListener;
    private DatabaseReference databaseReference;
    private MyAdapterSpecificGenre myAdapterSpecificGenre;

    public MyAdapterGenres(Context context, List<String> allGenres, GenreClickListener genreClickListener) {
        this.context = context;
        this.allGenres = allGenres;
        this.genreClickListener = genreClickListener;
    }

    @NonNull
    @Override
    public MyAdapterGenresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_by_genres_item,parent,false);
        return new MyAdapterGenresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterGenresViewHolder holder, int position) {
        allMoviesByGenre = new ArrayList<MovieInfo>();
        allMovies = new ArrayList<MovieInfo>();
        String genre = allGenres.get(position);
        holder.genreButton.setText(genre);

        holder.genreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = allGenres.get(position);
                if (genreClickListener != null) {
                    genreClickListener.onGenreClick(genre);
                }
            }
        });

//        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Movies");
//        databaseReference.orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                allMoviesByGenre.clear();
//                allMovies.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()){
//                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
//                    allMovies.add(movieInfo);
//
//                    // Check if the movie belongs to the current genre
//                    if (movieInfo != null && movieInfo.getGenres() != null && movieInfo.getGenres().contains(genre)) {
//                            if(!allMoviesByGenre.contains(movieInfo))
//                                allMoviesByGenre.add(movieInfo);
//                    }
//
//                }
//                for(int i =0; i<allMoviesByGenre.size();i++){
//                    Log.d("lala", allMoviesByGenre.get(i).getMovieName() +" "+allMoviesByGenre.get(i).getGenres());
//                }



//                allMovies.clear();
//                allGenres.clear();
//                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
//                    MovieInfo movieInfo = itemSnapshot.getValue(MovieInfo.class);
//                    allMovies.add(movieInfo);
//
//                    for (int i = 0; i < allMovies.size(); i++) {
//                        for (int j = 0; j < allGenres.size(); j++) {
//                            if (allMovies.get(i).getGenres().get(j).contains(genre) && !allMoviesByGenre.contains(allMovies.get(i))) {
//                                allMoviesByGenre.add(allMovies.get(i));
//                            }
//                        }
//                    }
//                    myAdapterSpecificGenre.notifyDataSetChanged();
//                }

                    // Check if the movie belongs to the current genre
//                    for(int i=0; i<movieInfo.size();i++){
//                        if (movieInfo != null && movieInfo.getGenres() != null && movieInfo.getGenres().contains(genre)) {
//                            if (!(allMoviesByGenre.contains(movieInfo.getGenres().get(i)))) {
//                                allMoviesByGenre.add(movieInfo.get(i).getGenres().get(j));
//                            }
//                            allMoviesByGenre.add(movieInfo);
//                        }
//                        for(int i=0; i<allMoviesByGenre.size();i++){
//                            Log.d("lala",allMoviesByGenre.get(i).getMovieName() +" "+genre);
//                        }
//                    }
//                    if (movieInfo != null && movieInfo.getGenres() != null) {
//                        // Check if the movie belongs to the current genre
//                        for (String movieGenre : movieInfo.getGenres()) {
//                            if (movieGenre.equals(genre)) {
//                                allMoviesByGenre.add(movieInfo);
//                                break; // Break after finding a match to avoid duplicates
//                            }
//                        }
//                    }
//                }
                //myAdapterSpecificGenre.notifyDataSetChanged(); // Notify adapter after updating the list




//                // Sort the list based on serialNumber
//                Collections.sort(allMoviesItems, new Comparator<MovieInfo>() {
//                    @Override
//                    public int compare(MovieInfo movie1, MovieInfo movie2) {
//                        return Integer.compare(movie1.getSerialNumber(), movie2.getSerialNumber());
//                    }
//                });
                //myAdapterMovie.notifyDataSetChanged();




//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
//        holder.recyclerViewSpecificGenre.setLayoutManager(linearLayoutManager);
//        myAdapterSpecificGenre = new MyAdapterSpecificGenre(new MovieFragment().getContext(), allMoviesByGenre);
//        holder.recyclerViewSpecificGenre.setAdapter(myAdapterSpecificGenre);
    }




    @Override
    public int getItemCount() {
        return allGenres.size();
    }

    public interface GenreClickListener {
        void onGenreClick(String genre);
    }

    public class MyAdapterGenresViewHolder extends RecyclerView.ViewHolder{
//        private LinearLayout linearLayout;
//        private LinearLayout expandLinear;
        //private TextView genreTextView;
        //private RecyclerView recyclerViewSpecificGenre;
        private Button genreButton;

        public MyAdapterGenresViewHolder(@NonNull View itemView) {
            super(itemView);
            genreButton = itemView.findViewById(R.id.genreButton);
            //recyclerViewSpecificGenre =itemView.findViewById(R.id.recyclerViewCpecificGenre);
        }
    }

}
