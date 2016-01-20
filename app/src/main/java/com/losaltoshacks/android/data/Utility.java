/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String readInputStream(InputStream is) {
        BufferedReader bufferedReader = null;
        String data = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            data = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to close buffered reader.");
                e.printStackTrace();
            }
        }
        return data;
    }

    public static JSONArray sortJSONArray(JSONArray jsonArray, Comparator<JSONObject> comparator) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        JSONArray sortedArray = null;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(jsonArray.getJSONObject(i));
            }

            Collections.sort(jsonObjects, comparator);

            sortedArray = new JSONArray();
            for (int i = 0; i < jsonObjects.size(); i++) {
                sortedArray.put(jsonObjects.get(i));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error while sorting JSONArray");
            e.printStackTrace();
        }
        return sortedArray;
    }
}
