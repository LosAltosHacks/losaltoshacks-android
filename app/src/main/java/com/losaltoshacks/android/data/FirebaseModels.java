/*
 * Copyright (C) 2016, Los Altos Hacks Team
 *
 * This project is licensed under the ISC license. See the LICENSE file or
 * http://opensource.org/licenses/ISC for a copy.
 */

package com.losaltoshacks.android.data;

public class FirebaseModels {
    public static final class ScheduleItem {
        private String mEvent;
        private String mLocation;
        private int mTime;
        private String mTag;

        public ScheduleItem() {
        }

        public ScheduleItem(String mEvent, String mLocation, int mTime, String mTag) {
            this.mEvent = mEvent;
            this.mLocation = mLocation;
            this.mTime = mTime;
            this.mTag = mTag;
        }

        public String getEvent() {
            return mEvent;
        }

        public String getLocation() {
            return mLocation;
        }

        public int getTime() {
            return mTime;
        }

        public String getTag() {
            return mTag;
        }


        public void setEvent(String mEvent) {
            this.mEvent = mEvent;
        }

        public void setLocation(String mLocation) {
            this.mLocation = mLocation;
        }

        public void setTime(int mTime) {
            this.mTime = mTime;
        }

        public void setTag(String mTag) {
            this.mTag = mTag;
        }
    }

    public static final class UpdateItem {
        private String mTitle;
        private String mDescription;
        private int mTime;
        private String mTag;

        public UpdateItem() {
        }

        public UpdateItem(String mTitle, String mDescription, int mTime, String mTag) {
            this.mTitle = mTitle;
            this.mDescription = mDescription;
            this.mTime = mTime;
            this.mTag = mTag;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDescription() {
            return mDescription;
        }

        public int getTime() {
            return mTime;
        }

        public String getTag() {
            return mTag;
        }

        public void setTitle(String mTitle) {
            this.mTitle = mTitle;
        }

        public void setDescription(String mDescription) {
            this.mDescription = mDescription;
        }

        public void setTime(int mTime) {
            this.mTime = mTime;
        }

        public void setTag(String mTag) {
            this.mTag = mTag;
        }

    }
}
