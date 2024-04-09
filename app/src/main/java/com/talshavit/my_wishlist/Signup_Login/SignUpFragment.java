package com.talshavit.my_wishlist.Signup_Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.talshavit.my_wishlist.MainActivity;
import com.talshavit.my_wishlist.R;

import java.io.IOException;
import java.io.InputStream;

public class SignUpFragment extends Fragment {

    private EditText emailSignUp, passwordSignup, confirmSignup;
    private Button signupButton;

    private TextView privacypolicytXT, termTxt;

    private String email, password;

    private FirebaseAuth firebaseAuth;


    public SignUpFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        findViews(view);
        initViews();
    }

    private void initViews() {
        onSingUp();
        onPrivacyPolicy();
        onTerms();
    }

    private void onTerms() {
        termTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String htmlContent = loadHtmlFromAsset("terms_of_conditions.html");
                dialogFunc(htmlContent);
            }
        });
    }

    private void onPrivacyPolicy() {
        privacypolicytXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String htmlContent = loadHtmlFromAsset("privacy_policy.html");
                dialogFunc(htmlContent);

            }
        });
    }

    private void dialogFunc(String htmlContent) {
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

    private void onSingUp() {
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailSignUp.getText().toString().trim();
                password = passwordSignup.getText().toString().trim();
                String confirmPassword = confirmSignup.getText().toString().trim();
                boolean isValid = true;

                if (email.isEmpty()) {
                    emailSignUp.setError("Email can not be empty!");
                    isValid = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailSignUp.setError("The email address is badly formatted");
                    isValid = false;
                }
                if (password.isEmpty()) {
                    passwordSignup.setError("Password can not be empty!");
                    isValid = false;
                } else if (password.length() < 6) {
                    passwordSignup.setError("Password should be at least 6 characters");
                    isValid = false;
                }
                if (confirmPassword.isEmpty()) {
                    confirmSignup.setError("Password can not be empty!");
                    isValid = false;
                } else if (confirmPassword.length() < 6) {
                    confirmSignup.setError("Password should be at least 6 characters");
                    isValid = false;
                } else if (!password.equals(confirmPassword)) {
                    confirmSignup.setError("Password and confirmPassword are not match");
                    isValid = false;
                }
                if (isValid) {
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("paymentForAds");
                    databaseReference.setValue(false);
                    openMainActivity();
                } else
                    Toast.makeText(getContext(), "failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViews(View view) {
        signupButton = view.findViewById(R.id.signupButton);
        emailSignUp = view.findViewById(R.id.emailSignUp);
        passwordSignup = view.findViewById(R.id.passwordSignup);
        confirmSignup = view.findViewById(R.id.confirmSignup);
        privacypolicytXT = view.findViewById(R.id.privacypolicytXT);
        termTxt = view.findViewById(R.id.termTxt);
    }

    private void openMainActivity() {
        Intent myIntent = new Intent(getContext(), MainActivity.class);
        startActivity(myIntent);
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
}
