package com.example.akhileshlamba.smarter;

import android.provider.BaseColumns;

/**
 * Created by akhileshlamba on 29/3/18.
 */

public class ElectricityContract {

    private ElectricityContract(){}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "ELECTRICITY_USAGE";
        public static final String COLUMN_NAME_ID = "USAGE_ID";
        public static final String COLUMN_NAME_RESID = "RESID";
        public static final String COLUMN_NAME_AC = "AC_USAGE";
        public static final String COLUMN_NAME_WM = "WASHINGMACHINE_USAGE";
        public static final String COLUMN_NAME_FR = "FRIDGE_USAGE";
        public static final String COLUMN_NAME_TEMP = "TEMPERATURE";
        public static final String COLUMN_NAME_DATE = "CURRENTDATE";
        public static final String COLUMN_NAME_HOUR = "HOUR";
    }
}

