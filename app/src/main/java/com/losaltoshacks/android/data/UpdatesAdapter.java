/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.losaltoshacks.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdatesAdapter extends BaseAdapter {
    private static final String LOG_TAG = UpdatesAdapter.class.getSimpleName();
    private JSONArray updates;
    private LayoutInflater layoutInflater;

    public UpdatesAdapter(Context context, JSONArray updates) {
        layoutInflater = LayoutInflater.from(context);
        this.updates = updates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject update = getItem(position);
        if (update == null ) {
            Log.e(LOG_TAG, "Could not get item");
            return null;
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_updates, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.mTitle.setText(update.getString("title"));
            viewHolder.mDescription.setText(update.getString("description"));
            viewHolder.mDate.setText(update.getString("date"));
            viewHolder.mTag.setText(update.getString("tag"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Could not parse update");
            e.printStackTrace();
        }
        return convertView;
    }

    public void swapData(JSONArray data) {
        Log.d(LOG_TAG, "Swapping data");
        updates = data;
        if (data != null) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }
    }

    @Override
    public int getCount() {
        if (updates != null) {
            return updates.length();
        } else {
            return 0;
        }
    }

    @Override
    public JSONObject getItem(int position) {
        if (updates != null) {
            try {
                return updates.getJSONObject(position);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Failed to get update object");
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mDescription;
        public TextView mDate;
        public TextView mTag;
        public ViewHolder(View view) {
            mTitle = (TextView) view.findViewById(R.id.updates_title);
            mDescription = (TextView) view.findViewById(R.id.updates_description);
            mDate = (TextView) view.findViewById(R.id.updates_date);
            mTag = (TextView) view.findViewById(R.id.updates_tag);
        }
    }
}
