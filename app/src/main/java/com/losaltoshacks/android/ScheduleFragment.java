/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.losaltoshacks.android.data.Contract;
import com.losaltoshacks.android.data.ScheduleAdapter;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ScheduleFragment.class.getSimpleName();
    private ScheduleAdapter mScheduleAdapter;
    private static final int SCHEDULE_LOADER = 0;

    public static final String[] SCHEDULE_COLUMNS = {
            Contract.ScheduleEntry._ID,
            Contract.ScheduleEntry.COLUMN_EVENT,
            Contract.ScheduleEntry.COLUMN_LOCATION,
            Contract.ScheduleEntry.COLUMN_TIME,
            Contract.ScheduleEntry.COLUMN_TAG
    };

    public static final int COL_SCHEDULE_ID = 0;
    public static final int COL_SCHEDULE_EVENT = 1;
    public static final int COL_SCHEDULE_LOCATION = 2;
    public static final int COL_SCHEDULE_TIME = 3;
    public static final int COL_SCHEDULE_TAG = 4;

    public ScheduleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ListView listView = (ListView) view.findViewById(R.id.schedule_listview);
        mScheduleAdapter = new ScheduleAdapter(getActivity(), null, 0);
        listView.setAdapter(mScheduleAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SCHEDULE_LOADER, null, this);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                Contract.ScheduleEntry.CONTENT_URI,
                SCHEDULE_COLUMNS,
                null,
                null,
                Contract.ScheduleEntry.COLUMN_TIME + " ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        Log.d(LOG_TAG, "Loaded " + cursor.getCount() + " rows");
        mScheduleAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        mScheduleAdapter.swapCursor(null);
    }

}
