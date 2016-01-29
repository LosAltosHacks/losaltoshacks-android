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

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.losaltoshacks.android.data.Contract;
import com.losaltoshacks.android.data.DbHelper;
import com.losaltoshacks.android.data.Utility;
import com.losaltoshacks.android.sync.SyncAdapter;
import com.onesignal.OneSignal;

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

        checkIfPlayServicesAvailable();
        OneSignal.startInit(this).init();

        checkDb();

        checkForNotification();
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
        checkIfPlayServicesAvailable();
        checkForNotification();
    }

    private void checkForNotification() {
        if (Utility.openUpdates) {
            Utility.openUpdates = false;
            Log.d(LOG_TAG, "Opening updates fragment because notification was opened.");
            mViewPager.setCurrentItem(2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(LOG_TAG, "Pushed settings");
            SyncAdapter.syncImmediately(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkIfPlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int status = apiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(status)) {
                Dialog errorDialog = apiAvailability.getErrorDialog(this, status, 1);
                errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        MainActivity.this.finish();
                    }
                });
                errorDialog.show();
            } else {
                Toast.makeText(this, "Your device does not have Google Play Services and is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
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
