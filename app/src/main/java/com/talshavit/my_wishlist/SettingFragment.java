package com.talshavit.my_wishlist;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingFragment extends Fragment {

    private LinearLayout linearPrivacyPolicy;

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
        linearPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
    }

    private void initDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_privacy_policy);
        dialog.getWindow().setLayout(1000,1200);

        TextView textViewHtml = dialog.findViewById(R.id.textViewHtml);
        String htmlContent = loadHtmlFromAsset("privacy_policy");
        textViewHtml.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY));
        //Log.d("lala", textView.getText().toString());


        dialog.show();



    }

    private void findViews(View view) {
        linearPrivacyPolicy = view.findViewById(R.id.privacy_policy);
    }

    private String loadHtmlFromAsset(String filename){
        Log.d("lala", "a");
        String textFile = "";
        try {
            Log.d("lala", "b");
            InputStream inputStream = requireContext().getAssets().open(filename);
            Log.d("lala", "c");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            Log.d("lala", "d");
            textFile = new String(buffer);
            Log.d("lala", "e");
            //textView.setText(Html.fromHtml(textFile,Html.FROM_HTML_MODE_LEGACY));
            inputStream.close();
        }catch (IOException e){
            Log.d("lala", "f");
            e.printStackTrace();
        }
        return textFile;
    }

}