package com.wendyliga.s7soundfix;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Service;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class quicktile extends TileService {
    CountDownTimer waitTimer;
    private static final String PREFERENCES_KEY = "com.wendyliga.s7soundfix";
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        Tile tile = this.getQsTile();
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.drawable.ic_phonelink_ring_black_24dp));
        tile.setLabel(getString(R.string.fix_sound));
        tile.setContentDescription(
                getString(R.string.fix_sound));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();

    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        boolean isActive= true;
        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();
        updateTile();
        final Intent playerIntent = new Intent(this, MediaPlayerService.class);
        stopService(playerIntent);
        startService(playerIntent);

        waitTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                boolean isActive = false;
                prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();
                updateTile();
                stopService(playerIntent);
            }
        }.start();





    }
    private boolean getServiceStatus() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }
    public void updateTile() {

        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {

            newLabel = String.format(Locale.US, "%s", getString(R.string.fixing));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_sync_black_24dp);

            newState = Tile.STATE_ACTIVE;

        } else {
            newLabel = String.format(Locale.US, "%s", getString(R.string.fix_sound));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.ic_phonelink_ring_black_24dp);

            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }
}