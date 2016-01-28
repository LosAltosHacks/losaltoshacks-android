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
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.losaltoshacks.android.R;
import com.losaltoshacks.android.data.Contract.ScheduleEntry;
import com.losaltoshacks.android.data.Contract.UpdatesEntry;
import com.losaltoshacks.android.data.Provider;
import com.losaltoshacks.android.data.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        insertJSONData(downloadJSONFromURL(context.getString(R.string.sync_updates_url)),
                downloadJSONFromURL(context.getString(R.string.sync_schedule_url)),
                context);

        Log.d(LOG_TAG, "Sync finished.");
    }

    private JSONArray downloadJSONFromURL(String urlString) {
        HttpURLConnection httpURLConnection = null;
        URL url = null;

        try {
            url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream in = httpURLConnection.getInputStream();

            return new JSONArray(Utility.readInputStream(in));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed url: " + urlString);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Invalid JSON data from URL: " + url.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to download JSON data from URL: " + url.toString());
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    private static void insertJSONData(JSONArray updatesJSON, JSONArray scheduleJSON, Context context) {
        final String UPDATES_TITLE = "title";
        final String UPDATES_DESCRIPTION = "description";
        final String UPDATES_TIME = "date";
        final String UPDATES_TAG = "tag";

        final String SCHEDULE_EVENT = "event";
        final String SCHEDULE_TIME = "time";
        final String SCHEDULE_LOCATION = "location";
        final String SCHEDULE_TAG = "tag";

        ContentResolver contentResolver = context.getContentResolver();

        try {
            if (updatesJSON != null) {
                ContentValues[] updatesArray = new ContentValues[updatesJSON.length()];
                for (int i = 0; i < updatesJSON.length(); i++) {
                    JSONObject update = updatesJSON.getJSONObject(i);

                    ContentValues updateValues = new ContentValues();

                    updateValues.put(UpdatesEntry.COLUMN_TITLE, update.getString(UPDATES_TITLE));
                    updateValues.put(UpdatesEntry.COLUMN_DESCRIPTION, update.getString(UPDATES_DESCRIPTION));
                    updateValues.put(UpdatesEntry.COLUMN_TIME, update.getInt(UPDATES_TIME));
                    updateValues.put(UpdatesEntry.COLUMN_TAG, update.getString(UPDATES_TAG));

                    updatesArray[i] = updateValues;
                }

                Bundle updatesBundle = new Bundle();
                updatesBundle.putParcelableArray(Provider.CONTENT_VALUES_ARRAY, updatesArray);
                contentResolver.call(UpdatesEntry.CONTENT_URI,
                        Provider.BULK_DELETE_AND_INSERT, UpdatesEntry.CONTENT_URI.toString(), updatesBundle);
            } else {
                Log.e(LOG_TAG, "Could not insert updates data because it was null.");
            }

            if (scheduleJSON != null) {
                ContentValues[] scheduleArray = new ContentValues[scheduleJSON.length()];
                for (int i = 0; i < scheduleJSON.length(); i++) {
                    JSONObject event = scheduleJSON.getJSONObject(i);

                    ContentValues scheduleValues = new ContentValues();

                    scheduleValues.put(ScheduleEntry.COLUMN_EVENT, event.getString(SCHEDULE_EVENT));
                    scheduleValues.put(ScheduleEntry.COLUMN_TIME, event.getInt(SCHEDULE_TIME));
                    scheduleValues.put(ScheduleEntry.COLUMN_LOCATION, event.getString(SCHEDULE_LOCATION));
                    scheduleValues.put(ScheduleEntry.COLUMN_TAG, event.getString(SCHEDULE_TAG));

                    scheduleArray[i] = scheduleValues;
                }

                Bundle scheduleBundle = new Bundle();
                scheduleBundle.putParcelableArray(Provider.CONTENT_VALUES_ARRAY, scheduleArray);
                contentResolver.call(ScheduleEntry.CONTENT_URI,
                        Provider.BULK_DELETE_AND_INSERT, ScheduleEntry.CONTENT_URI.toString(), scheduleBundle);
            } else {
                Log.e(LOG_TAG, "Could not insert schedule data because it was null.");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Failed to parse JSON data.");
            e.printStackTrace();
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
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
            syncImmediately(context);
        }
        return newAccount;
    }
}