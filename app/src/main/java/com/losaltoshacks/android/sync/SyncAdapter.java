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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.losaltoshacks.android.R;
import com.losaltoshacks.android.data.Contract.ScheduleEntry;
import com.losaltoshacks.android.data.Contract.UpdatesEntry;
import com.losaltoshacks.android.data.FirebaseArray;
import com.losaltoshacks.android.data.FirebaseModels.ScheduleItem;
import com.losaltoshacks.android.data.FirebaseModels.UpdateItem;
import com.losaltoshacks.android.data.Provider;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    ContentResolver mContentResolver;

    private FirebaseArray mScheduleSnapshots;
    private FirebaseArray mUpdatesSnapshots;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
        initializeArrays();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
        initializeArrays();
    }

    private void initializeArrays() {
        Query mScheduleQuery = FirebaseDatabase.getInstance().getReference("/schedule");
        mScheduleSnapshots = new FirebaseArray(mScheduleQuery);
        mScheduleSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                syncImmediately(getContext());
            }

            @Override
            public void onDataChanged() {
                syncImmediately(getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, databaseError.toException());
            }
        });

        Query mUpdatesQuery = FirebaseDatabase.getInstance().getReference("/updates");
        mUpdatesSnapshots = new FirebaseArray(mUpdatesQuery);
        mUpdatesSnapshots.setOnChangedListener(new FirebaseArray.OnChangedListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                syncImmediately(getContext());
            }

            @Override
            public void onDataChanged() {
                syncImmediately(getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(LOG_TAG, databaseError.toException());
            }
        });
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");
        Context context = getContext();

        insertFirebaseData(mScheduleSnapshots, mUpdatesSnapshots, context);

        Log.d(LOG_TAG, "Sync finished.");
    }

    private static void insertFirebaseData(FirebaseArray scheduleSnapshots, FirebaseArray updatesSnapshots, Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues[] updatesArray = new ContentValues[updatesSnapshots.getCount()];
        for (int i = 0; i < updatesSnapshots.getCount(); i++) {
            UpdateItem update = updatesSnapshots.getItem(i).getValue(UpdateItem.class);

            ContentValues updateValues = new ContentValues();

            updateValues.put(UpdatesEntry.COLUMN_TITLE, update.getTitle());
            updateValues.put(UpdatesEntry.COLUMN_DESCRIPTION, update.getDescription());
            updateValues.put(UpdatesEntry.COLUMN_TIME, update.getTime());
            updateValues.put(UpdatesEntry.COLUMN_TAG, update.getTag());

            updatesArray[i] = updateValues;
        }

        Bundle updatesBundle = new Bundle();
        updatesBundle.putParcelableArray(Provider.CONTENT_VALUES_ARRAY, updatesArray);
        contentResolver.call(UpdatesEntry.CONTENT_URI,
                Provider.BULK_DELETE_AND_INSERT, UpdatesEntry.CONTENT_URI.toString(), updatesBundle);


        ContentValues[] scheduleArray = new ContentValues[scheduleSnapshots.getCount()];
        for (int i = 0; i < scheduleSnapshots.getCount(); i++) {
            ScheduleItem event = scheduleSnapshots.getItem(i).getValue(ScheduleItem.class);

            ContentValues scheduleValues = new ContentValues();

            scheduleValues.put(ScheduleEntry.COLUMN_EVENT, event.getEvent());
            scheduleValues.put(ScheduleEntry.COLUMN_TIME, event.getTime());
            scheduleValues.put(ScheduleEntry.COLUMN_LOCATION, event.getLocation());
            scheduleValues.put(ScheduleEntry.COLUMN_TAG, event.getTag());

            scheduleArray[i] = scheduleValues;
        }

        Bundle scheduleBundle = new Bundle();
        scheduleBundle.putParcelableArray(Provider.CONTENT_VALUES_ARRAY, scheduleArray);
        contentResolver.call(ScheduleEntry.CONTENT_URI,
                Provider.BULK_DELETE_AND_INSERT, ScheduleEntry.CONTENT_URI.toString(), scheduleBundle);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(SyncAdapter.getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        Log.d(LOG_TAG, "getSyncAccount");
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