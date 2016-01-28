/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DashboardFragment extends Fragment {
    private DashboardCountdown mDashboardCountdown;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        long millisUntilFinished, duration;

        Calendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2016, Calendar.JANUARY, 30, 13, 0),
                finish = new GregorianCalendar(2016, Calendar.JANUARY, 31, 10, 0);

        // If the hackathon hasn't started yet we display a countdown to it
        if (now.compareTo(start) == -1) {
            millisUntilFinished = start.getTimeInMillis() - now.getTimeInMillis();
            duration = 0;
        } else {
            millisUntilFinished = finish.getTimeInMillis() - now.getTimeInMillis();
            duration = finish.getTimeInMillis() - start.getTimeInMillis();
        }

        mDashboardCountdown = new DashboardCountdown(
                millisUntilFinished,
                duration,
                (TextView) rootView.findViewById(R.id.countdown_textview),
                (ProgressBar) rootView.findViewById(R.id.progress_bar)
        );

        mDashboardCountdown.start();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDashboardCountdown.cancel();
    }

    private class DashboardCountdown extends CountDownTimer {
        public final String LOG_TAG = DashboardCountdown.class.getSimpleName();
        TextView countdownView;
        ProgressBar progressBar;
        long duration;

        public DashboardCountdown(long millisUntilFinished, long duration,
                                  TextView countdownView, ProgressBar progressBar) {

            super(millisUntilFinished, 1000);

            this.countdownView = countdownView;
            this.progressBar = progressBar;
            this.duration = duration;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long secondsLeft = millisUntilFinished / 1000,
                    hours =  secondsLeft / 3600,
                    minutes = (secondsLeft % 3600) / 60,
                    seconds = secondsLeft % 60;

            if (hours > 0) {
                countdownView.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
            } else {
                countdownView.setText(String.format("%02d:%02d", minutes, seconds));
            }

            // If duration is 0, set the progress bar to 0
            if (duration != 0) {
                double percentComplete = 1 - millisUntilFinished / (double) duration;
                progressBar.setProgress((int) Math.floor(percentComplete * progressBar.getMax()));
            } else {
                progressBar.setProgress(0);
            }
        }

        @Override
        public void onFinish() {
            countdownView.setText(getString(R.string.timer_finished));
            progressBar.setProgress(100);
        }
    }
}
