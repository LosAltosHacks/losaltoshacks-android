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
import com.losaltoshacks.android.onesignal.NotificationHandler;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        checkIfPlayServicesAvailable();
        OneSignal.startInit(this).setNotificationOpenedHandler(new NotificationHandler()).init();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DashboardFragment(), "Dashboard");
        viewPagerAdapter.addFragment(new ScheduleFragment(), "Schedule");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfPlayServicesAvailable();
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
}
