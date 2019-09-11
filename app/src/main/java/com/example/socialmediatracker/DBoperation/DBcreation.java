package com.example.socialmediatracker.DBoperation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.socialmediatracker.Activities.Alert;
import com.example.socialmediatracker.helper.AppMailIndicator;

import java.util.ArrayList;

import static com.example.socialmediatracker.DBoperation.CreateDatabase.MAIL_INDICATOR_COUNT;

public class DBcreation {
    private SQLiteDatabase sqLiteDatabase;
    private CreateDatabase createDatabase;
    private Context context;

    public DBcreation(Context context) {
        this.context = context;
        createDatabase = new CreateDatabase(context);
    }

    private void open() {
        sqLiteDatabase = createDatabase.getWritableDatabase();
    }

    private void close() {
        sqLiteDatabase.close();
    }


    public boolean AddAppInfo(DatabaseModel databaseModel) {
        this.open();
        sqLiteDatabase = createDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.PACKAGE_NAME, databaseModel.getPackageName());
        values.put(CreateDatabase.TIME, databaseModel.getTime());

        long id = sqLiteDatabase.insert(CreateDatabase.TABLE_NAME, null, values);
        this.close();

        return id > 0;
    }

    public boolean UpdateAppBasicInfo(DatabaseModel databaseModel) {
        this.open();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.PACKAGE_NAME, databaseModel.getPackageName());
        values.put(CreateDatabase.TIME, databaseModel.getTime());

        long id = sqLiteDatabase.update(CreateDatabase.TABLE_NAME, values,
                CreateDatabase.PACKAGE_NAME + " = ?", new String[]{String.valueOf(databaseModel.getPackageName())});
        this.close();

        return id > 0;
    }

    public ArrayList<DatabaseModel> getAllData() {
        ArrayList<DatabaseModel> databaseModels = new ArrayList<>();
        this.open();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + CreateDatabase.TABLE_NAME, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            for (int i = 1; i <= cursor.getCount(); i++) {
                String packageName = cursor.getString(cursor.getColumnIndex(CreateDatabase.PACKAGE_NAME));
                String time = cursor.getString(cursor.getColumnIndex(CreateDatabase.TIME));
                cursor.moveToNext();
                databaseModels.add(new DatabaseModel(packageName, Long.parseLong(time)));
            }

        }

        cursor.close();
        this.close();
        return databaseModels;
    }

    public DatabaseModel getDataByPackage(String packageName) {
        this.open();
        String[] columns = {
                CreateDatabase.PACKAGE_NAME,
                CreateDatabase.TIME
        };

        String selection = CreateDatabase.PACKAGE_NAME + " = ?";
        String[] selectionArgs = {packageName};

        Cursor cursor = sqLiteDatabase.query(CreateDatabase.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        DatabaseModel databaseModel = new DatabaseModel(packageName, 2*3600*1000);
        if (cursor.getCount() > 0) {
            String packageN = cursor.getString(cursor.getColumnIndex(CreateDatabase.PACKAGE_NAME));
            String time = cursor.getString(cursor.getColumnIndex(CreateDatabase.TIME));
            databaseModel = new DatabaseModel(packageN, Long.parseLong(time));
        }

        cursor.close();
        this.close();
        return databaseModel;
    }

    public boolean AddMailIndicator(AppMailIndicator appMailIndicator){
        this.open();
        sqLiteDatabase = createDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.MAIL_PACKAGE_NAME, appMailIndicator.getPackageName());
        values.put(MAIL_INDICATOR_COUNT, String.valueOf(appMailIndicator.getCount()));

        long id = sqLiteDatabase.insert(CreateDatabase.MAIL_INDICATOR, null, values);
        this.close();

        return id > 0;
    }

    public boolean UpdateMailIndicator(AppMailIndicator appMailIndicator) {
        this.open();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.MAIL_PACKAGE_NAME, appMailIndicator.getPackageName());
        values.put(MAIL_INDICATOR_COUNT, appMailIndicator.getCount());

        long id = sqLiteDatabase.update(CreateDatabase.MAIL_INDICATOR, values,
                CreateDatabase.MAIL_PACKAGE_NAME + " = ?", new String[]{String.valueOf(appMailIndicator.getPackageName())});
        this.close();

        return id > 0;
    }

    public AppMailIndicator getMailIndicatorByPackage(String packageName){
        this.open();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * from "+CreateDatabase.MAIL_INDICATOR+" WHERE "+CreateDatabase.MAIL_PACKAGE_NAME+" = ?", new String[] {packageName});

        cursor.moveToFirst();
        AppMailIndicator appMailIndicator = new AppMailIndicator(packageName, 1);
        if (cursor.getCount() > 0) {
            String count = cursor.getString(cursor.getColumnIndex(MAIL_INDICATOR_COUNT));
            appMailIndicator.setPackageName(packageName);
            appMailIndicator.setCount(Integer.parseInt(count));
        }
        cursor.close();
        this.close();
        return appMailIndicator;
    }

    public boolean checkMailIndicatorByPackage(String packageName){
        this.open();
        String[] columns ={
                CreateDatabase.MAIL_PACKAGE_NAME,
                MAIL_INDICATOR_COUNT
        };
        String selection = CreateDatabase.MAIL_PACKAGE_NAME + " = ?";
        String[] selectionArgs = {packageName};

        Cursor cursor = sqLiteDatabase.query(CreateDatabase.MAIL_INDICATOR, columns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        boolean b = cursor.getCount() > 0;
        this.close();
        cursor.close();
        return b;

    }
}
