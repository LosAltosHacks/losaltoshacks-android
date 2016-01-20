package com.losaltoshacks.android;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONObject;

public class NotificationHandler implements OneSignal.NotificationOpenedHandler {
    private static final String LOG_TAG = NotificationHandler.class.getSimpleName();

    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        Log.d(LOG_TAG, message);
        if (additionalData != null) {
            Log.d(LOG_TAG, additionalData.toString());
        }
    }
}
