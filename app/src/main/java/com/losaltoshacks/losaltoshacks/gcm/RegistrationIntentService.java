/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * The following code is a derivative work of code from Google Inc., which is
 * licensed under the Apache License 2.0. This project is licensed under the
 * ISC license. See the LICENSE file or http://opensource.org/licenses/ISC.
 *
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        String[] topics = getResources().getStringArray(R.array.subscription_topics);
        for (String topic : topics) {
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
        String[] topics = getResources().getStringArray(R.array.subscription_topics);
        for (String topic : topics) {
            pubSub.unsubscribe(token, "/topics/" + topic);
        }
    }
}
