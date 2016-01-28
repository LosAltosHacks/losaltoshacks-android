/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.losaltoshacks.android.data.Contract;
import com.losaltoshacks.android.data.Contract.UpdatesEntry;
import com.losaltoshacks.android.data.UpdatesAdapter;

public class UpdatesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = UpdatesFragment.class.getSimpleName();
    private static final int UPDATES_LOADER = 0;

    public static final String[] UPDATES_COLUMNS = {
            UpdatesEntry._ID,
            UpdatesEntry.COLUMN_TITLE,
            UpdatesEntry.COLUMN_DESCRIPTION,
            UpdatesEntry.COLUMN_TIME,
            UpdatesEntry.COLUMN_TAG
    };

    public static final int COL_UPDATE_ID = 0;
    public static final int COL_UPDATE_TITLE = 1;
    public static final int COL_UPDATE_DESCRIPTION = 2;
    public static final int COL_UPDATE_TIME = 3;
    public static final int COL_UPDATE_TAG = 4;

    public UpdatesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_updates, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new UpdatesAdapter(getActivity(), null, 0));
        getLoaderManager().initLoader(UPDATES_LOADER, null, this);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                Contract.UpdatesEntry.CONTENT_URI,
                UPDATES_COLUMNS,
                null,
                null,
                Contract.UpdatesEntry.COLUMN_TIME + " DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished");
        Log.d(LOG_TAG, "Loaded " + cursor.getCount() + " rows");
        ((CursorAdapter) getListAdapter()).swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        ((CursorAdapter) getListAdapter()).swapCursor(null);
    }

}
