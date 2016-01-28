/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * The following code is a derivative work of code from the Android Open Source
 * Project and Udacity, which is licensed under the Apache License 2.0. This
 * project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 *
 * Copyright (c) 2016, Udacity
 * Copyright (c) 2014, The Android Open Source Project
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

package com.losaltoshacks.android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class Provider extends ContentProvider {
    private static final String LOG_TAG = Provider.class.getSimpleName();

    public static final String BULK_DELETE_AND_INSERT = "bulkDeleteAndInsert";
    public static final String CONTENT_VALUES_ARRAY = "contentValuesArray";
    public static final String ROWS_INSERTED = "rowsInserted";
    public static final String ROWS_DELETED = "rowsDeleted";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int UPDATES = 100;
    static final int SCHEDULE = 200;
    static final int SCHEDULE_WITH_START_TIME = 201;

    private static final String sScheduleWithStartTimeSelection =
            Contract.ScheduleEntry.TABLE_NAME +
                    "." + Contract.ScheduleEntry.COLUMN_TIME + " >= ? ";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_UPDATES, UPDATES);
        matcher.addURI(authority, Contract.PATH_SCHEDULE, SCHEDULE);
        matcher.addURI(authority, Contract.PATH_SCHEDULE + "/#", SCHEDULE_WITH_START_TIME);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case UPDATES:
                return Contract.UpdatesEntry.CONTENT_TYPE;
            case SCHEDULE:
                return Contract.ScheduleEntry.CONTENT_TYPE;
            case SCHEDULE_WITH_START_TIME:
                return Contract.ScheduleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case UPDATES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.UpdatesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SCHEDULE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SCHEDULE_WITH_START_TIME: {
                long time = Contract.ScheduleEntry.getTimeFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.ScheduleEntry.TABLE_NAME,
                        projection,
                        sScheduleWithStartTimeSelection,
                        new String[]{Long.toString(time)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case UPDATES: {
                long _id = db.insert(Contract.UpdatesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.UpdatesEntry.buildUpdatesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SCHEDULE: {
                long _id = db.insert(Contract.ScheduleEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.ScheduleEntry.buildScheduleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "Inserted: " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete: " + uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case UPDATES:
                rowsDeleted = db.delete(
                        Contract.UpdatesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCHEDULE:
                rowsDeleted = db.delete(
                        Contract.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(LOG_TAG, "Deleted " + rowsDeleted + " items.");
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case UPDATES:
                rowsUpdated = db.update(Contract.UpdatesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SCHEDULE:
                rowsUpdated = db.update(Contract.ScheduleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public Bundle call(String method, String arg, Bundle extras) {
        if (method.equals(BULK_DELETE_AND_INSERT)) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final Uri uri = Uri.parse(arg);
            final int match = sUriMatcher.match(uri);
            final ContentValues[] contentValues =
                    (ContentValues[]) extras.getParcelableArray(CONTENT_VALUES_ARRAY);
            int rowsInserted = 0,
                    rowsDeleted;
            switch (match) {
                case UPDATES:
                    db.beginTransaction();
                    try {
                        rowsDeleted = db.delete(Contract.UpdatesEntry.TABLE_NAME, null, null);
                        for (ContentValues value : contentValues) {
                            long _id = db.insert(Contract.UpdatesEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                rowsInserted++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    Log.d(LOG_TAG, String.format(
                            "Deleted %d updates and inserted %d", rowsDeleted, rowsInserted));
                    break;
                case SCHEDULE:
                    db.beginTransaction();
                    try {
                        rowsDeleted = db.delete(Contract.ScheduleEntry.TABLE_NAME, null, null);
                        for (ContentValues value : contentValues) {
                            long _id = db.insert(Contract.ScheduleEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                rowsInserted++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    Log.d(LOG_TAG, String.format(
                            "Deleted %d schedule items and inserted %d", rowsDeleted, rowsInserted));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid table name arg: " + arg);
            }
            Bundle rowInfo = new Bundle();
            rowInfo.putInt(ROWS_DELETED, rowsDeleted);
            rowInfo.putInt(ROWS_INSERTED, rowsInserted);
            return rowInfo;
        }
        return null;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case UPDATES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.UpdatesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(LOG_TAG, "Bulk inserted " + returnCount + " updates");
                return returnCount;
            case SCHEDULE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.ScheduleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(LOG_TAG, "Bulk inserted " + returnCount + " schedule items");
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}