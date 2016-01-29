/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String formatTimestamp(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(time * 1000));
    }
}
