package com.talshavit.my_wishlist.GeneralHelpers;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.talshavit.my_wishlist.Movie.MovieInfo;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.TvShow.TvShowInfo;

import java.util.ArrayList;
import java.util.List;

public class GeneralFunctions<T extends GenerealInterfaces> {

    private FirebaseAnalytics firebaseAnalytics;

    public GeneralFunctions(Context context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void setSwipeToDelete(String title,String messege, Context context,
                         List<T> list, RecyclerView.Adapter<?> adapter,
                         DatabaseReference databaseReference, RecyclerView recyclerView,
                         String userID, String childPath) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                int position = viewHolder.getAdapterPosition();

                logSwipeToDeleteEvent();
                
                builder.setTitle(title);
                builder.setMessage(messege + list.get(position).getName().toUpperCase() +"\"?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFromFirebase(list.get(position).getSerialID(), userID,childPath, context);
                        list.remove(position);
                        updateSerialIds(list, childPath, databaseReference);
                        adapter.notifyItemRemoved(position);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemChanged(position);
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

                setWatchedIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive, context);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void logSwipeToDeleteEvent() {
        Bundle params = new Bundle();
        params.putString("delete", "swipe_to_delete");
        firebaseAnalytics.logEvent("swipe_to_delete_event", params);
    }

    public void updateSerialIds(List<T> list, String childPath, DatabaseReference databaseReference) {
        if(childPath.equals("movies")){
            ArrayList<MovieInfo> movies = new ArrayList<>();
            movies = (ArrayList<MovieInfo>) list;
            for (int i = 0; i < list.size(); i++) {
                movies.get(i).setSerialID(i);
            }
            databaseReference.setValue(movies);
        }else{
            ArrayList<TvShowInfo> tvs = new ArrayList<>();
            tvs = (ArrayList<TvShowInfo>) list;
            for (int i = 0; i < list.size(); i++) {
                tvs.get(i).setSerialID(i);
            }
            databaseReference.setValue(tvs);
        }

    }

    private static void setWatchedIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive, Context context) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#85F32C2C");
        Drawable viewDrawable = ContextCompat.getDrawable(context,  R.drawable.baseline_delete_24);
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

    public void deleteFromFirebase(int id, String userID,String childPath, Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child(childPath).child(String.valueOf(id));
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public String formatGenres(List<String> genres) {
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
}
