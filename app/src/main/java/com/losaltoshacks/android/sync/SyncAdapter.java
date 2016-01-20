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

package com.losaltoshacks.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.losaltoshacks.android.R;
import com.losaltoshacks.android.data.FileHelper;
import com.losaltoshacks.android.data.Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
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
        Log.d(LOG_TAG, "onPerformSync");
        Context context = getContext();
        String syncType = extras.getString(SYNC_TYPE_KEY, "");
        URL syncUrl = null;
        HttpURLConnection httpURLConnection = null;

        try {
            if (syncType.equals(SYNC_UPDATES)) {
                syncUrl = new URL(getContext().getString(R.string.sync_updates_url));
            } else {
                Log.e(LOG_TAG, "Invalid sync type: " + syncType);
                return;
            }

            httpURLConnection = (HttpURLConnection) syncUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream in = httpURLConnection.getInputStream();

            FileHelper fileHelper = new FileHelper(context);

            fileHelper.writeUpdates(new JSONArray(Utility.readInputStream(in)));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed syncing url.");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Invalid JSON data from URL: " + syncUrl.toString());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(SyncAdapter.SYNC_TYPE_KEY, SyncAdapter.SYNC_UPDATES);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(SyncAdapter.getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
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