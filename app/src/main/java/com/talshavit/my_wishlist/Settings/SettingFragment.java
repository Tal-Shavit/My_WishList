package com.talshavit.my_wishlist.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.talshavit.my_wishlist.MainActivity;
import com.talshavit.my_wishlist.R;
import com.talshavit.my_wishlist.Signup_Login.StartActivity;

import java.io.IOException;
import java.io.InputStream;

public class SettingFragment extends Fragment {

    private LinearLayout linearPrivacyPolicy, deleteAccount, LogOut, payment, rateUs, shareApp;
    private TextView changePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initView();
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        onPrivacyPolicy();
        onChangePassword();
        onDeleteAccount();
        onLogOut();
        onPayment();
        onRating();
        onShareApp();
    }

    private void onShareApp() {
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share App");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Never Miss a Must-Watch with WatchNext! Check out this app!");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }

    // For this time it doesnt do anything
    private void onRating() {
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.rate_us_dialog);
                CardView rateCardView = dialog.findViewById(R.id.rateCardView);
                dialog.show();

                rateCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void onPayment() {
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).disableAds();
                }
            }
        });
    }

    private void onLogOut() {
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartActivity();
            }
        });
    }

    private void onDeleteAccount() {
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("DELETE ACCOUNT");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setCancelable(false);
                positiveButton(builder);
                negativeButton(builder);
                builder.show();
            }
        });
    }

    private void negativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void positiveButton(AlertDialog.Builder builder) {
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (user != null) {
                    String userID = user.getUid();
                    //Delete user from real-time firebase
                    DatabaseReference userToDelete = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                    userToDelete.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            firebaseAuth.signOut();
                                            openStartActivity();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void onChangePassword() {
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, ChangePasswordFragment.class, null)
                        .setReorderingAllowed(true).addToBackStack(null)
                        .commit();
            }
        });
    }

    private void onPrivacyPolicy() {
        linearPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
    }

    private void initDialog() {
        String htmlContent = loadHtmlFromAsset("privacy_policy.html");

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            materialAlertDialogBuilder.setMessage(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
        } else {
            materialAlertDialogBuilder.setMessage(Html.fromHtml(htmlContent));
        }
        materialAlertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //Close the dialog
            }
        });

        materialAlertDialogBuilder.setCancelable(false);
        materialAlertDialogBuilder.show();
    }

    private void findViews(View view) {
        linearPrivacyPolicy = view.findViewById(R.id.privacy_policy);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        LogOut = view.findViewById(R.id.LogOut);
        payment = view.findViewById(R.id.payment);
        changePassword = view.findViewById(R.id.changePassword);
        rateUs = view.findViewById(R.id.rateUs);
        shareApp = view.findViewById(R.id.shareApp);
    }

    private String loadHtmlFromAsset(String filename) {
        String textFile = "";
        try {
            InputStream inputStream = requireContext().getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            textFile = new String(buffer);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textFile;
    }

    private void openStartActivity() {
        Intent myIntent = new Intent(getContext(), StartActivity.class);
        startActivity(myIntent);
    }

}