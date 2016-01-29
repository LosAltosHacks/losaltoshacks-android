/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.losaltoshacks.android.R;
import com.losaltoshacks.android.ScheduleFragment;
import com.twotoasters.sectioncursoradapter.adapter.SectionCursorAdapter;
import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

public class ScheduleAdapter extends SectionCursorAdapter<String, ScheduleAdapter.SectionViewHolder, ScheduleAdapter.ItemViewHolder> {
    private static final String LOG_TAG = ScheduleAdapter.class.getSimpleName();

    public ScheduleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags, R.layout.list_section_schedule, R.layout.list_item_schedule);
    }

    @Override
    protected ItemViewHolder createItemViewHolder(Cursor cursor, View itemView) {
        return new ItemViewHolder(itemView);
    }

    @Override
    protected void bindItemViewHolder(ItemViewHolder itemViewHolder, Cursor cursor, ViewGroup parent) {
        itemViewHolder.mEvent.setText(cursor.getString(ScheduleFragment.COL_SCHEDULE_EVENT));
        itemViewHolder.mLocation.setText(cursor.getString(ScheduleFragment.COL_SCHEDULE_LOCATION));
        itemViewHolder.mTime.setText(
                Utility.formatTimestamp(cursor.getInt(ScheduleFragment.COL_SCHEDULE_TIME),
                        "h:mm a"));
    }

    @Override
    protected String getSectionFromCursor(Cursor cursor) {
        return Utility.formatTimestamp(cursor.getLong(ScheduleFragment.COL_SCHEDULE_TIME),
                "EEEE, LLLL d");
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View sectionView, String section) {
        return new SectionViewHolder(sectionView);
    }

    @Override
    protected void bindSectionViewHolder(int position, SectionViewHolder sectionViewHolder,
                                         ViewGroup parent, String section) {
        sectionViewHolder.mTitle.setText(section);
    }

    public static class ItemViewHolder extends ViewHolder {
        public TextView mEvent;
        public TextView mLocation;
        public TextView mTime;
        public TextView mTag;
        public ItemViewHolder(View view) {
            super(view);
            mEvent = findWidgetById(R.id.schedule_event);
            mLocation = findWidgetById(R.id.schedule_location);
            mTime = findWidgetById(R.id.schedule_time);
        }
    }

    public static class SectionViewHolder extends ViewHolder {
        public TextView mTitle;
        public SectionViewHolder(View view) {
            super(view);
            mTitle = findWidgetById(R.id.schedule_section);
            System.out.println(mTitle);
        }
    }
}
