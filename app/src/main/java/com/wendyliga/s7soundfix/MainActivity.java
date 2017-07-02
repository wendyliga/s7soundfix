package com.wendyliga.s7soundfix;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;

import android.os.Handler;

import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.wendyliga.s7soundfix.fragments.main;
import com.wendyliga.s7soundfix.fragments.about;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private String[] activityTitles;
    private MediaPlayerService player;
    private CountDownTimer waitTimer;
    boolean serviceBound = false;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView txtName, txtWebsite;
    private static final String TAG_1 = "main";
    private static final String TAG_2 = "about";
    private static final String TAG_3 = "hide icon";
    private static final String TAG_4 = "change sound";
    private static final String TAG_5 = "share";
    private static final String TAG_6 = "bug";
    private static final String TAG_7 = "rate";
    private static final String TAG_8 = "check_my_other_apps";
    private static final String TAG_9 = "test";
    public static String CURRENT_TAG = TAG_1;
    public static int navItemIndex = 0;
    private boolean shouldLoadHomeFragOnBackPress = true;


    private Handler mHandler;

    //admob
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_1;
            loadHomeFragment();
        }


    }
    private void loadNavHeader() {
        // name, website
        txtName.setText(getString(R.string.app_name));
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        txtWebsite.setText(getString(R.string.version)+": " + versionName +"("+versionCode+")");


        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }



    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // main
                main main_fragment = new main();
                return  main_fragment;
            case 1:
                // about
                about about_fragment = new about();
                return about_fragment;

            default:
                return new main();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_1;
                        break;
                    case R.id.nav_about:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_2;
                        break;
                    case R.id.nav_hide_icon:

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(getString(R.string.hide_icon))
                                .setMessage(getString(R.string.hide_icon_message))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        PackageManager p = getPackageManager();
                                        ComponentName componentName = new ComponentName(MainActivity.this, com.wendyliga.s7soundfix.MainActivity.class);
                                        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                                    }})
                                .setNegativeButton(android.R.string.no, null).show();

                        return true;
                    case R.id.nav_success_sound:
                        choose_sound_dialog();
                        return true;
                    case R.id.nav_share:
                        String message = getString(R.string.share_message);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, message);
                        startActivity(Intent.createChooser(share, getString(R.string.title_share)));
                        return true;
                    case R.id.nav_bug:
                        Intent intentEmail = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "wendy.devs@gmail.com", null));
                        intentEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"wendy.devs@gmail.com"});
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.title_submit_bug_email));
                        intentEmail.setType("message/rfc822");


                        String versionName = BuildConfig.VERSION_NAME;
                        int versionCode = BuildConfig.VERSION_CODE;
                        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                        String package_name = getApplicationContext().getPackageName();

                        //make_device_detail
                        String bug_detail = "";
                        bug_detail += "\n" + "<--Don't delete this Message-->";
                        bug_detail += "\n" + package_name;
                        bug_detail += "\n" + android_id;
                        bug_detail += "\n" + getString(R.string.version)+": " + versionName +"("+versionCode+")";
                        bug_detail += "\n" + "<--Don't delete this Message-->";
                        bug_detail += "\n";
                        bug_detail += "\n" + "Your Message Here :";


                        intentEmail.putExtra(android.content.Intent.EXTRA_TEXT,(bug_detail));
                        startActivity(Intent.createChooser(intentEmail, getString(R.string.title_submit_bug)));
                        return true;
                    case R.id.nav_rate:
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainActivity.this, getString(R.string.error_message_rate_app), Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.check_my_other_apps:
                        Intent goToMarket;
                        goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:\"Wendy Liga\""));
                        startActivity(goToMarket);
                        return true;

                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_1;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private void choose_sound_dialog() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.wendyliga.s7soundfix", Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_success_sound);
        final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        dialog.setCancelable(true);

        final RadioGroup rg_sound = (RadioGroup) dialog.findViewById(R.id.rg_sound);
        int selected_sound = sp.getInt("selected_sound", 0);

        switch (selected_sound) {
            case 0:
                rg_sound.check(R.id.r_0);

                break;
            case 1:
                rg_sound.check(R.id.r_1);
                break;
            case 2:
                rg_sound.check(R.id.r_2);
                break;
            case 3:
                rg_sound.check(R.id.r_3);
                break;
            case 4:
                rg_sound.check(R.id.r_4);
                break;
        }
        rg_sound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                View radioButton = rg_sound.findViewById(checkedId);
                int index = rg_sound.indexOfChild(radioButton);
                ed.putInt("selected_sound", index);
                ed.commit();
                Intent playerIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(playerIntent);
                startService(playerIntent);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent playerIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(playerIntent);

            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            if (CURRENT_TAG == TAG_2){
                getMenuInflater().inflate(R.menu.menu_about, menu);
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.lisence) {
            startActivity(new Intent(MainActivity.this, lisence.class));
        }
        return super.onOptionsItemSelected(item);
    }
}