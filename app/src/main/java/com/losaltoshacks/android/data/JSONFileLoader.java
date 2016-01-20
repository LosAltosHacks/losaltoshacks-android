/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * The following code is a derivative work of code from Alex Lockwood,
 * which is licensed under the MIT License. This project is licensed under the
 * ISC license. See the LICENSE file or http://opensource.org/licenses/ISC.
 *
 * Copyright (c) 2012 Alex Lockwood
 */
package com.losaltoshacks.android.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;

import java.util.Observable;
import java.util.Observer;

public class JSONFileLoader extends AsyncTaskLoader<JSONArray[]> {
    private static final String LOG_TAG = JSONFileLoader.class.getSimpleName();
    private JSONArray[] mData;
    private FileObserver mObserver;

    public JSONFileLoader(Context context) {
        super(context);
    }

    @Override
    public JSONArray[] loadInBackground() {
        return new JSONArray[] {FileHelper.getInstance().readUpdates(getContext())};
    }

    @Override
    public void deliverResult(JSONArray[] data) {
        if (isReset()) {
            return;
        }
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
        if (mObserver == null) {
            mObserver = new FileObserver();
        }
        if (takeContentChanged() || mData == null) {
            Log.d(LOG_TAG, "Reloading data");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mObserver != null) {
            mObserver.destroy();
        }
    }

    private class FileObserver implements Observer {

        public FileObserver() {
            FileHelper.getInstance().addObserver(this);
        }

        public void update(Observable observable, Object data) {
            onContentChanged();
        }

        public void destroy() {
            FileHelper.getInstance().deleteObserver(this);
        }
    }
}
