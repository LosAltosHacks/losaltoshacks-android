/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * The following code is a derivative work of code from the Android Open Source
 * Project and Udacity, which is licensed under the Apache License 2.0. This
 * project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 *
 * Copyright (c) 2016, The Android Open Source Project
 * Copyright (c) 2016, Udacity
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.losaltoshacks.losaltoshacks.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.losaltoshacks.losaltoshacks.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    public static final String SYNC_TYPE_KEY = "sync_type";
    public static final String SYNC_UPDATES = "sync_updates";

    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Context context = getContext();
        String syncType = extras.getString(SYNC_TYPE_KEY, "");
        URL updates = null;
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            String syncFilename;
            if (syncType.equals(SYNC_UPDATES)) {
                updates = new URL(getContext().getString(R.string.sync_updates_url));
                syncFilename = context.getString(R.string.updates_filename);
            } else {
                Log.e(LOG_TAG, "Invalid sync type: " + syncType);
                return;
            }

            httpURLConnection = (HttpURLConnection) updates.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream in = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(in));

            FileOutputStream fileOutputStream =
                    context.openFileOutput(syncFilename, Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));

            String line = bufferedReader.readLine();
            while (line != null) {
                bufferedWriter.append(line + "\n");
                line = bufferedReader.readLine();
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed syncing url.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to close buffered reader or writer.");
                e.printStackTrace();
            }
        }
    }

    public static Account getSyncAccount(Context context) {
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if(accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
                return null;
            }
        }
        return newAccount;
    }
}