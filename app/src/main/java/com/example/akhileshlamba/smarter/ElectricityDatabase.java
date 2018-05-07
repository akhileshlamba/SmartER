package com.example.akhileshlamba.smarter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.akhileshlamba.smarter.entities.ElectricityUsage;

import java.text.SimpleDateFormat;

/**
 * Created by Akhilesh Lamba on 29/3/18.
 */

public class ElectricityDatabase {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Electricity_usage.db";
    private final Context context;
    private static final String TEXT_TYPE = " TEXT"; private static final String COMMA_SEP = ",";
    private MySQLiteOpenHelper myDBHelper; private SQLiteDatabase db;

    private String[] columns = {
            ElectricityContract.FeedEntry.COLUMN_NAME_ID,
            ElectricityContract.FeedEntry.COLUMN_NAME_RESID,
            ElectricityContract.FeedEntry.COLUMN_NAME_AC,
            ElectricityContract.FeedEntry.COLUMN_NAME_WM,
            ElectricityContract.FeedEntry.COLUMN_NAME_FR,
            ElectricityContract.FeedEntry.COLUMN_NAME_TEMP,
            ElectricityContract.FeedEntry.COLUMN_NAME_DATE,
            ElectricityContract.FeedEntry.COLUMN_NAME_HOUR};


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ElectricityContract.FeedEntry.TABLE_NAME + " (" +
                    ElectricityContract.FeedEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_RESID + " INTEGER," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_AC + " DOUBLE," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_WM + " DOUBLE," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_FR + " DOUBLE," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_TEMP + " DOUBLE," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_DATE + " TEXT," +
                    ElectricityContract.FeedEntry.COLUMN_NAME_HOUR + " INTEGER)";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ElectricityContract.FeedEntry.TABLE_NAME;

    public ElectricityDatabase(Context context){
        this.context = context;
        myDBHelper = new MySQLiteOpenHelper(context);
    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    public ElectricityDatabase open() throws SQLException {
        db = myDBHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        myDBHelper.close();
    }

    public long insertUser(ElectricityUsage electricityUsage) {
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_RESID, electricityUsage.getResid());
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_AC, electricityUsage.getAirconditionerUsage());
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_WM, electricityUsage.getWashingmachineUsage());
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_FR, electricityUsage.getFridgeUsage());
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_TEMP, electricityUsage.getTemperature());
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_DATE, sdf.format(electricityUsage.getCurrentdate()));
        values.put(ElectricityContract.FeedEntry.COLUMN_NAME_HOUR, electricityUsage.getDayHour());

        return db.insert(ElectricityContract.FeedEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllUsages(int resid) {
        String selection = ElectricityContract.FeedEntry.COLUMN_NAME_RESID + " = ?";
        String[] selectionArgs = { String.valueOf(resid) };
        return db.query(ElectricityContract.FeedEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getHourUsages(int hour) {
        String selection = ElectricityContract.FeedEntry.COLUMN_NAME_HOUR + " = ?";
        String[] selectionArgs = { String.valueOf(hour) };

        return db.query(ElectricityContract.FeedEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    public int deleteAllRecords(int resid){
        String selection = ElectricityContract.FeedEntry.COLUMN_NAME_RESID + " = ?";
        String[] selectionArgs = { String.valueOf(resid) };
        return db.delete(ElectricityContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
    }

}
