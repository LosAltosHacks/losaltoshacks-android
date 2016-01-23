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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.losaltoshacks.android.data.Contract.ScheduleEntry;
import com.losaltoshacks.android.data.Contract.UpdatesEntry;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "losaltoshacks.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_UPDATES_TABLE = "CREATE TABLE " + UpdatesEntry.TABLE_NAME + " (" +
                UpdatesEntry._ID + " INTEGER PRIMARY KEY," +
                UpdatesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                UpdatesEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                UpdatesEntry.COLUMN_DATE + "INTEGER NOT NULL, " +
                UpdatesEntry.COLUMN_TAG + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_SCHEDULE_TABLE = "CREATE TABLE " + ScheduleEntry.TABLE_NAME + " (" +
                ScheduleEntry._ID + " INTEGER PRIMARY KEY," +
                ScheduleEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_START + " INTEGER NOT NULL, " +
                ScheduleEntry.COLUMN_END + " INTEGER NOT NULL," +
                ScheduleEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                ScheduleEntry.COLUMN_TAG + " TEXT NOT NULL, " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_UPDATES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SCHEDULE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UpdatesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ScheduleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
