/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.content.Context;
import android.util.Log;

import com.losaltoshacks.android.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.locks.ReentrantLock;

public class FileHelper {
    private static final String LOG_TAG = FileHelper.class.getSimpleName();
    private Context context;
    private static ReentrantLock updatesLock = new ReentrantLock();

    public FileHelper(Context context) {
        this.context = context;
    }

    public JSONArray readUpdates() {
        return readJSONFile(updatesLock, context.getString(R.string.updates_filename));
    }

    public void writeUpdates(JSONArray updates) {
        writeJSONFile(updates, updatesLock, context.getString(R.string.updates_filename));
    }

    private JSONArray readJSONFile(ReentrantLock lock, String filename) {
        lock.lock();

        JSONArray fileData = null;
        try {
            String jsonData = Utility.readInputStream(context.openFileInput(filename));
            fileData = new JSONArray(jsonData);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + filename);
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Invalid JSON in file: " + filename);
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return fileData;
    }

    private void writeJSONFile(JSONArray jsonData, ReentrantLock lock, String filename) {
        lock.lock();

        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(
                    filename, Context.MODE_PRIVATE);

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(jsonData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Failed to close buffered writer.");
                e.printStackTrace();
            }
            lock.unlock();
        }
    }
}
