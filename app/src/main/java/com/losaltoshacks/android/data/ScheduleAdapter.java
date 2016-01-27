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
import com.losaltoshacks.android.ScheduleFragment;

public class ScheduleAdapter extends CursorAdapter {
    private static final String LOG_TAG = ScheduleAdapter.class.getSimpleName();

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_schedule, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mEvent.setText(cursor.getString(ScheduleFragment.COL_SCHEDULE_EVENT));
        viewHolder.mLocation.setText(cursor.getString(ScheduleFragment.COL_SCHEDULE_LOCATION));
        viewHolder.mTime.setText(Integer.toString(cursor.getInt(ScheduleFragment.COL_SCHEDULE_TIME)));
        viewHolder.mTag.setText(cursor.getString(ScheduleFragment.COL_SCHEDULE_TAG));
    }

    private static class ViewHolder {
        public TextView mEvent;
        public TextView mLocation;
        public TextView mTime;
        public TextView mTag;
        public ViewHolder(View view) {
            mEvent = (TextView) view.findViewById(R.id.schedule_event);
            mLocation = (TextView) view.findViewById(R.id.schedule_location);
            mTime = (TextView) view.findViewById(R.id.schedule_time);
            mTag = (TextView) view.findViewById(R.id.schedule_tag);
        }
    }
}
