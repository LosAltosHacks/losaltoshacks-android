/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DashboardFragment extends Fragment {
    private DashboardCountdown mDashboardCountdown;
    private TextView mCountdownText;
    private TextView mCountdownTime;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mCountdownText = (TextView) rootView.findViewById(R.id.countdown_text);
        mCountdownTime = (TextView) rootView.findViewById(R.id.countdown_time);

        createCountdown();

        return rootView;
    }

    private void createCountdown() {
        if (mDashboardCountdown != null) {
            mDashboardCountdown.cancel();
        }

        long millisUntilFinished;
        boolean started;

        Calendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2017, Calendar.FEBRUARY, 4, 12, 0),
                finish = new GregorianCalendar(2017, Calendar.FEBRUARY, 5, 6, 0);

        // If the hackathon hasn't started yet we display a countdown to it
        if (now.compareTo(start) == -1) {
            millisUntilFinished = start.getTimeInMillis() - now.getTimeInMillis();
            started = false;
        } else {
            millisUntilFinished = finish.getTimeInMillis() - now.getTimeInMillis();
            started = true;
        }

        mDashboardCountdown = new DashboardCountdown(
                millisUntilFinished,
                started,
                mCountdownText,
                mCountdownTime
        );

        mDashboardCountdown.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDashboardCountdown.cancel();
    }

    private class DashboardCountdown extends CountDownTimer {
        public final String LOG_TAG = DashboardCountdown.class.getSimpleName();
        TextView countdownTime;
        TextView countdownText;
        boolean started;

        public DashboardCountdown(long millisUntilFinished, boolean started,
                                  TextView countdownText, TextView countdownTime) {

            super(millisUntilFinished, 1000);

            this.countdownText = countdownText;
            this.countdownTime = countdownTime;
            this.started = started;

            if (started) {
                countdownText.setText(getString(R.string.dashboard_during_hackathon));
            } else {
                countdownText.setText(getString(R.string.dashboard_before_hackathon));
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long secondsLeft = millisUntilFinished / 1000,
                    hours =  secondsLeft / 3600,
                    minutes = (secondsLeft % 3600) / 60,
                    seconds = secondsLeft % 60;

            if (hours > 0) {
                countdownTime.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
            } else {
                countdownTime.setText(String.format("%02d:%02d", minutes, seconds));
            }
        }

        @Override
        public void onFinish() {
            if (!started) {
                createCountdown();
            } else {
                countdownTime.setText(getString(R.string.timer_finished));
                countdownText.setText(getString(R.string.dashboard_after_hackathon));
            }
        }
    }
}
