package com.losaltoshacks.losaltoshacks.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.losaltoshacks.losaltoshacks.R;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();
    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceId = InstanceID.getInstance(this);
        try {
            String token = instanceId.getToken(getString(R.string.gcm_senderID),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get registration token");
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {

    }
}
