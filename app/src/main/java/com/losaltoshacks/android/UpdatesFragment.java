/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.losaltoshacks.android.data.JSONFileLoader;
import com.losaltoshacks.android.data.UpdatesAdapter;

import org.json.JSONArray;

public class UpdatesFragment extends Fragment implements LoaderManager.LoaderCallbacks<JSONArray[]> {
    private UpdatesAdapter mUpdatesAdapter;

    public UpdatesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        ListView listView = (ListView) view.findViewById(R.id.updates_listview);
        mUpdatesAdapter = new UpdatesAdapter(getContext(), null);
        listView.setAdapter(mUpdatesAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    public Loader<JSONArray[]> onCreateLoader(int i, Bundle bundle) {
        return new JSONFileLoader(getContext());
    }

    public void onLoadFinished(Loader<JSONArray[]> loader, JSONArray[] data) {
        mUpdatesAdapter.swapData(data[0]);
    }

    public void onLoaderReset(Loader<JSONArray[]> loader) {
        mUpdatesAdapter.swapData(null);
    }

}
