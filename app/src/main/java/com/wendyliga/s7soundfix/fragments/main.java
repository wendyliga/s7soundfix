package com.wendyliga.s7soundfix.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.wendyliga.s7soundfix.DeveloperKey;
import com.wendyliga.s7soundfix.MediaPlayerService;
import com.wendyliga.s7soundfix.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.view.View.GONE;


public class main extends Fragment {

    private CountDownTimer waitTimer;
    private CircularProgressButton btn_fix;
    private ProgressBar pb;
    private MediaPlayerService player;
    boolean serviceBound = false;
    //admob
    private AdView mAdView;
    // YouTube player view
    private YouTubePlayer YPlayer;

    private FragmentActivity myContext;
    private static final String PREFERENCES_KEY = "com.wendyliga.s7soundfix";


    public main() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {

        if (activity instanceof FragmentActivity) {
            myContext = (FragmentActivity) activity;
        }

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        //admob -- iklan
        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        pb = (ProgressBar) view.findViewById(R.id.pb);


        btn_fix = (CircularProgressButton) view.findViewById(R.id.btn_fix);
        btn_fix.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           btn_fix.setIndeterminateProgressMode(true);
                                           //pb.setVisibility(View.VISIBLE);
                                           //btn_fix.setVisibility(GONE);
                                           Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
                                           getActivity().stopService(playerIntent);
                                           playAudio();
                waitTimer = new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        btn_fix.setProgress(50);
                    }

                    public void onFinish() {
                        //btn_fix.setVisibility(View.VISIBLE);
                        //pb.setVisibility(View.GONE);
                        btn_fix.setProgress(100);
                        Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
                        getActivity().stopService(playerIntent);
                        getActivity().unbindService(serviceConnection);
                        btn_fix.setProgress(0);

                    }
                }.start();
                                       }
                                   }
        );
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_layout, youTubePlayerFragment).commit();
        youTubePlayerFragment.initialize("DEVELOPER_KEY", new OnInitializedListener() {
            @Override
            public void onInitializationSuccess(Provider arg0, YouTubePlayer youTubePlayer, boolean b) {
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.setFullscreen(false);
                    YPlayer.cueVideo("-CtvnBfs7O8");
                    //YPlayer.play();
                }
            }

            @Override
            public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });

        check_status_app();

        return view;
    }

    public void playAudio() {
        //Check is service is active
        Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
        getActivity().startService(playerIntent);
        getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //Binding this Client to the AudioPlayer Service
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public boolean initSound() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {


                File dir = new File(sdcard_dir(), "/Android/data/" + getActivity().getApplicationContext().getPackageName() + "/sound");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File dest = new File(sdcard_dir(), "/Android/data/" + getActivity().getApplicationContext().getPackageName() + "/sound/sound01.m4a");
                if (!dest.exists()) {
                    dest.createNewFile();
                }
                InputStream src = getResources().openRawResource(R.raw.sound01);
                OutputStream dst = new FileOutputStream(dest);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = src.read(buf)) > 0) {
                    dst.write(buf, 0, len);
                }
                src.close();
                dst.close();

            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String sdcard_dir() {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        return extStorageDirectory;
    }

    private String sound_location() {
        String location = sdcard_dir() + "/Android/data/" + getActivity().getApplicationContext().getPackageName() + "/sound/sound01.mp3";
        return location;
    }

    private void resize_layout(LinearLayout layout, int height, int width) {

        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = 100;
        params.width = 100;
        layout.setLayoutParams(params);

    }

    private SharedPreferences get_sp() {
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sp;
    }

    private SharedPreferences.Editor get_ed(SharedPreferences sp) {
        SharedPreferences.Editor ed = sp.edit();
        return ed;
    }

    private void check_status_app() {
        //baca
        SharedPreferences sp = get_sp();
        SharedPreferences.Editor ed = get_ed(get_sp());
        int sp_status_aplikasi = sp.getInt("status_aplikasi", 0);

        //inisialiasi awal agar dapat membedakan aplikasi baru atau sudah lama
        if (sp_status_aplikasi == 0) {     //status_aplikasi=1 berati aplikasi sudah pernah dipakai
            ed.putInt("status_aplikasi", 1);
            ed.putInt("selected_sound", 4);
            ed.commit();
        }
    }


}

