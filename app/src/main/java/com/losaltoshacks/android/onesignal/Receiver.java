/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.onesignal;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.losaltoshacks.android.R;
import com.losaltoshacks.android.sync.SyncAdapter;


public class Receiver extends BroadcastReceiver {
    private static final String LOG_TAG = Receiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        Bundle bundle = new Bundle();
        ContentResolver.requestSync(SyncAdapter.getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
}
