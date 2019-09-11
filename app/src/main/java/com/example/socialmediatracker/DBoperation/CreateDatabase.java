package com.example.socialmediatracker.DBoperation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CreateDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_info_db";
    private static final int DB_VERSION = 1;
    static final String TABLE_NAME = "table_name";
    static final String PACKAGE_NAME = "package_name";
    static final String TIME = "time";

    private static final String CREATE_TABLE = "create table "+TABLE_NAME+" ("+
            PACKAGE_NAME+ " text, "+
            TIME+ " BIGINT default 7200000);";

    static final String MAIL_INDICATOR = "mail_indicator";
    static final String MAIL_PACKAGE_NAME = "mail_package_name";
    static final String MAIL_INDICATOR_COUNT = "mail_count";

    private static final String CREATE_MAIL_INDICATOR_TABLE = "create table "+MAIL_INDICATOR+" ("+
            MAIL_PACKAGE_NAME+ " text, "+
            MAIL_INDICATOR_COUNT+ " INT default 1);";


    CreateDatabase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_MAIL_INDICATOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int onlVersion, int newVersion) {

    }
}
