package com.wendyliga.s7soundfix.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wendyliga.s7soundfix.BuildConfig;
import com.wendyliga.s7soundfix.R;


public class about extends Fragment {
    private TextView txt_version,txt_wendyliga_com;
    private ImageView img_twitter,img_instagram;

    public about() {
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
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        txt_version=(TextView) view.findViewById(R.id.txt_version);
        txt_wendyliga_com=(TextView) view.findViewById(R.id.txt_wendyliga_com);
        img_twitter=(ImageView) view.findViewById(R.id.img_twitter);
        img_instagram=(ImageView) view.findViewById(R.id.img_instagram);

        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        txt_version.setText(getString(R.string.version)+": " + versionName +"("+versionCode+")");

        img_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/wendyliga";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        img_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.instagram.com/wendyliga/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        txt_wendyliga_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.wendyliga.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return view;
        }
}
