/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.losaltoshacks.android.R;
import com.losaltoshacks.android.UpdatesFragment;

public class UpdatesAdapter extends CursorAdapter {
    private static final String LOG_TAG = UpdatesAdapter.class.getSimpleName();

    public UpdatesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_updates, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mTitle.setText(cursor.getString(UpdatesFragment.COL_UPDATE_TITLE));
        viewHolder.mDescription.setText(cursor.getString(UpdatesFragment.COL_UPDATE_DESCRIPTION));
        viewHolder.mTime.setText(Integer.toString(cursor.getInt(UpdatesFragment.COL_UPDATE_TIME)));
        viewHolder.mTag.setText(cursor.getString(UpdatesFragment.COL_UPDATE_TAG));
    }

    private static class ViewHolder {
        public TextView mTitle;
        public TextView mDescription;
        public TextView mTime;
        public TextView mTag;
        public ViewHolder(View view) {
            mTitle = (TextView) view.findViewById(R.id.updates_title);
            mDescription = (TextView) view.findViewById(R.id.updates_description);
            mTime = (TextView) view.findViewById(R.id.updates_time);
            mTag = (TextView) view.findViewById(R.id.updates_tag);
        }
    }
}
