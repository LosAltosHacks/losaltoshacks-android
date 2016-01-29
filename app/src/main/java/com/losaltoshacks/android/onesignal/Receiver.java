/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.onesignal;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.losaltoshacks.android.data.Contract;
import com.losaltoshacks.android.data.Utility;
import com.losaltoshacks.android.sync.SyncAdapter;

import java.util.Date;


public class Receiver extends BroadcastReceiver {
    private static final String LOG_TAG = Receiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive");
        Bundle dataBundle = intent.getBundleExtra("data");

        ContentValues updateValues = new ContentValues();

        updateValues.put(Contract.UpdatesEntry.COLUMN_TITLE, dataBundle.getString("title"));
        updateValues.put(Contract.UpdatesEntry.COLUMN_DESCRIPTION, dataBundle.getString("alert"));
        updateValues.put(Contract.UpdatesEntry.COLUMN_TIME, new Date().getTime() / 1000);
        updateValues.put(Contract.UpdatesEntry.COLUMN_TAG, "");

        context.getContentResolver().insert(Contract.UpdatesEntry.CONTENT_URI, updateValues);

        if (!dataBundle.getBoolean("isActive")) {
            Utility.openUpdates = true;
        }

        SyncAdapter.syncImmediately(context);
    }
}
