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

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MapFragment extends Fragment {
    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private SubsamplingScaleImageView mMap;

    public MapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMap = (SubsamplingScaleImageView) view.findViewById(R.id.map);
        mMap.setImage(ImageSource.resource(R.drawable.map));

        return view;
    }
}
