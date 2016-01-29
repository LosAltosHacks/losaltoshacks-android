/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MapFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private SubsamplingScaleImageView mMapFloor1;
    private SubsamplingScaleImageView mMapFloor2;

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        SegmentedGroup segmentedGroup = (SegmentedGroup) view.findViewById(R.id.segmentMapControl);
        segmentedGroup.setOnCheckedChangeListener(this);

        mMapFloor1 = (SubsamplingScaleImageView) view.findViewById(R.id.map_floor1);
        mMapFloor1.setImage(ImageSource.resource(R.drawable.floor1));

        mMapFloor2 = (SubsamplingScaleImageView) view.findViewById(R.id.map_floor2);
        mMapFloor2.setImage(ImageSource.resource(R.drawable.floor2));

        return view;
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.buttonFloor1:
                mMapFloor1.setVisibility(View.VISIBLE);
                mMapFloor2.setVisibility(View.GONE);
                break;
            case R.id.buttonFloor2:
                mMapFloor1.setVisibility(View.GONE);
                mMapFloor2.setVisibility(View.VISIBLE);
                break;
        }
    }
}
