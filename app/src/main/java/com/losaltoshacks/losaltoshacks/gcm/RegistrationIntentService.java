package com.losaltoshacks.losaltoshacks.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.losaltoshacks.losaltoshacks.R;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();

    private static final String[] TOPICS = {
            "schedule_update"
    };

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");
        InstanceID instanceId = InstanceID.getInstance(this);
        try {
            String token = instanceId.getToken(getString(R.string.gcm_senderID),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(getString(R.string.pref_registration_token), token).apply();
            subscribe();
            Log.d(LOG_TAG, token);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get registration token");
            e.printStackTrace();
        }
    }

    public void subscribe() throws IOException {
        Log.d(LOG_TAG, "subscribe");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString(getString(R.string.pref_registration_token),
                getString(R.string.pref_registration_error));

        if (token.equals(getString(R.string.pref_registration_error))) {
            return;
        }

        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
            Log.d(LOG_TAG, "Subscribed to /topics/" + topic);
        }
    }

    private void unsubscribe() throws IOException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString(getString(R.string.pref_registration_token),
                getString(R.string.pref_registration_error));

        if (token.equals(getString(R.string.pref_registration_error))) {
            return;
        }

        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.unsubscribe(token, "/topics/" + topic);
        }
    }
}
