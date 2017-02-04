/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * The following code is a derivative work of code from Suleiman Ali Shakir,
 * which is licensed under the MIT License. This project is licensed under the
 * ISC license. See the LICENSE file or http://opensource.org/licenses/ISC.
 *
 * Copyright (c) 2015 Suleiman Ali Shakir
 */

package com.losaltoshacks.android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.losaltoshacks.android.data.Contract;
import com.losaltoshacks.android.data.DbHelper;
import com.losaltoshacks.android.sync.SyncAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        checkDb();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(new DashboardFragment(), "Dash board");
        viewPagerAdapter.addFragment(new ScheduleFragment(), "Schedule");
        viewPagerAdapter.addFragment(new UpdatesFragment(), "Updates");
        viewPagerAdapter.addFragment(new MapFragment(), "Map");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkDb() {
        final SQLiteDatabase db = (new DbHelper(this)).getReadableDatabase();

        Cursor updatesCursor = db.rawQuery("SELECT * FROM " + Contract.UpdatesEntry.TABLE_NAME, null),
                scheduleCursor = db.rawQuery("SELECT * FROM " + Contract.ScheduleEntry.TABLE_NAME, null);

        if (!updatesCursor.moveToFirst() || !scheduleCursor.moveToFirst()) {
            SyncAdapter.syncImmediately(this);
        }
        updatesCursor.close();
        scheduleCursor.close();
    }
}
