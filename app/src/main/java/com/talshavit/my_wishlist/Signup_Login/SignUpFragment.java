package com.talshavit.my_wishlist.Signup_Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.talshavit.my_wishlist.MainActivity;
import com.talshavit.my_wishlist.R;

public class SignUpFragment extends Fragment {

    private EditText emailSignUp, passwordSignup, confirmSignup;
    private Button signupButton;

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
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailSignUp.getText().toString().trim();
                String password = passwordSignup.getText().toString().trim();
                String confirmPassword = confirmSignup.getText().toString().trim();
                boolean isValid = true;

                if(email.isEmpty()){
                    emailSignUp.setError("Email can not be empty!");
                    isValid = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailSignUp.setError("The email address is badly formatted");
                    isValid = false;
                }
                if(password.isEmpty()){
                    passwordSignup.setError("Password can not be empty!");
                    isValid = false;
                }else if (password.length() < 6) {
                    passwordSignup.setError("Password should be at least 6 characters");
                    isValid = false;
                }
                if(confirmPassword.isEmpty()){
                    confirmSignup.setError("Password can not be empty!");
                    isValid = false;
                }else if(confirmPassword.length() <6){
                    confirmSignup.setError("Password should be at least 6 characters");
                    isValid = false;
                } else if (!password.equals(confirmPassword)) {
                    confirmSignup.setError("Password and confirmPassword are not match");
                        isValid = false;
                    }
                if(isValid){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                openMainActivity();
                            } else
                                Toast.makeText(getContext(), "failed: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void findViews(View view) {
        signupButton = view.findViewById(R.id.signupButton);
        emailSignUp = view.findViewById(R.id.emailSignUp);
        passwordSignup = view.findViewById(R.id.passwordSignup);
        confirmSignup = view.findViewById(R.id.confirmSignup);
    }

    private void openMainActivity() {
        Intent myIntent = new Intent(getContext(), MainActivity.class);
        startActivity(myIntent);
    }
}
