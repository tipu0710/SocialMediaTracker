package com.example.socialmediatracker.DBoperation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "app_info_db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "table_name";
    public static final String PACKAGE_NAME = "package_name";
    public static final String TIME = "time";

    public static final String CREATE_TABLE = "create table "+TABLE_NAME+" ("+
            PACKAGE_NAME+ " text, "+
            TIME+ " BIGINT default 7200000);";

    public CreateDatabase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int onlVersion, int newVersion) {

    }
}
