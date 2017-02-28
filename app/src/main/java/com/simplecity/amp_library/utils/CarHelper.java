package com.simplecity.amp_library.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class CarHelper {

    private static final String TAG = "CarHelper";

    private CarHelper() {

    }

    private static final String AUTO_APP_PACKAGE_NAME = "com.google.android.projection.gearhead";

    /**
     * Action for an intent broadcast by Android Auto when a media app is connected or
     * disconnected. A "connected" media app is the one currently attached to the "media" facet
     * on Android Auto. So, this intent is sent by AA on:
     * <p>
     * - connection: when the phone is projecting and at the moment the app is selected from the
     * list of media apps
     * - disconnection: when another media app is selected from the list of media apps or when
     * the phone stops projecting (when the user unplugs it, for example)
     * <p>
     * The actual event (connected or disconnected) will come as an Intent extra,
     * with the key MEDIA_CONNECTION_STATUS (see below).
     */
    public static final String ACTION_MEDIA_STATUS = "com.google.android.gms.car.media.STATUS";

    /**
     * Key in Intent extras that contains the media connection event type (connected or disconnected)
     */
    public static final String MEDIA_CONNECTION_STATUS = "media_connection_status";

    /**
     * Value of the key MEDIA_CONNECTION_STATUS in Intent extras used when the current media app
     * is connected.
     */
    public static final String MEDIA_CONNECTED = "media_connected";

    /**
     * Value of the key MEDIA_CONNECTION_STATUS in Intent extras used when the current media app
     * is disconnected.
     */
    public static final String MEDIA_DISCONNECTED = "media_disconnected";

    public static boolean isValidCarPackage(String packageName) {
        return AUTO_APP_PACKAGE_NAME.equals(packageName);
    }

    /**
     * Returns true when running Android Auto or a car dock.
     * <p>
     * A preferable way of detecting if your app is running in the context of an Android Auto
     * compatible car is by registering a BroadcastReceiver for the action
     * {@link CarHelper#ACTION_MEDIA_STATUS}. See a sample implementation in
     * {@link MusicService#onCreate()}.
     *
     * @param c Context to detect UI Mode.
     * @return true when device is running in car mode, false otherwise.
     */
    public static boolean isCarUiMode(Context c) {
        UiModeManager uiModeManager = (UiModeManager) c.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
            Log.d(TAG, "Running in Car mode");
            return true;
        } else {
            Log.d(TAG, "Running on a non-Car mode");
            return false;
        }
    }

}
