package com.talshavit.my_wishlist.Settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.talshavit.my_wishlist.R;

public class ChangePasswordFragment extends Fragment {

    private ImageView newPasswordImage, confirmPasswordImage;
    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button confirmButton;
    private ImageButton exitButton;

    public ChangePasswordFragment() {
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
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initView();
    }

    private void initView() {
        onExitButtonClick();
        onShowPasswordButton();
        onShowConfirmButton();
        onConfirmButton();
}

    private void onConfirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                String newPW = newPasswordEditText.getText().toString().trim();;
                String confirmPW = confirmPasswordEditText.getText().toString().trim();;
                if (newPW.isEmpty()) {
                    newPasswordEditText.setError("You must fill password!");
                    isValid = false;
                }
                else if (newPW.length() < 6) {
                    newPasswordEditText.setError("Password should be at least 6 characters");
                    isValid = false;
                }
                if (confirmPW.isEmpty()) {
                    confirmPasswordEditText.setError("You must fill password!");
                    isValid = false;
                }
                else if (confirmPW.length() < 6) {
                    confirmPasswordEditText.setError("Password should be at least 6 characters");
                    isValid = false;
                }
                else if(!newPW.equals(confirmPW)){
                    confirmPasswordEditText.setError("Password and confirmPassword are not match!");
                    isValid = false;
                }
                if(isValid){
                    updatePassword(newPW);
                }
            }
        });
    }

    private void onShowConfirmButton() {
        confirmPasswordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisability(confirmPasswordEditText,confirmPasswordImage);
            }
        });
    }

    private void onShowPasswordButton() {
        newPasswordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisability(newPasswordEditText,newPasswordImage);
            }
        });
    }

    private void onExitButtonClick() {
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void updatePassword(String newPW) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPW)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Password updated successfully
                            changeFragment();
                        }
                    }
                });
    }

    private void changeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, SettingFragment.class, null)
                .setReorderingAllowed(true).addToBackStack(null)
                .commit();
    }

    private void findViews(View view) {
        newPasswordImage = view.findViewById(R.id.newPasswordImage);
        newPasswordEditText = view.findViewById(R.id.newPassword);
        confirmPasswordImage = view.findViewById(R.id.confirmPasswordImage);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);
        confirmButton = view.findViewById(R.id.confirmButton);
        exitButton = view.findViewById(R.id.exitButton);
    }

    private void passwordVisability(EditText editText, ImageView imageView){
        if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            //Password is visible, so hide it
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.hide); //Change the image to hide password
        } else {
            //Password is hidden, so show it
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.show); //Change the image to show password
        }
        //Move cursor to the end of the text
        editText.setSelection(newPasswordEditText.getText().length());
    }
}