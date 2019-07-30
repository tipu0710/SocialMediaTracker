package com.example.socialmediatracker.DBoperation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigInteger;
import java.util.ArrayList;

public class DBcreation {
    private SQLiteDatabase sqLiteDatabase;
    private CreateDatabase createDatabase;
    private Context context;

    public DBcreation(Context context) {
        this.context = context;
        createDatabase = new CreateDatabase(context);
    }

    private void open(){
        sqLiteDatabase = createDatabase.getWritableDatabase();
    }
    private void close(){
        sqLiteDatabase.close();
    }


    public boolean AddAppInfo(DatabaseModel databaseModel){
        this.open();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.PACKAGE_NAME, databaseModel.getPackageName());
        values.put(CreateDatabase.TIME, databaseModel.getTime());

        long id = sqLiteDatabase.insert(CreateDatabase.TABLE_NAME, null, values);
        this.close();

        return id > 0;
    }

    public boolean UpdateAppBasicInfo(DatabaseModel databaseModel){
        this.open();
        ContentValues values = new ContentValues();

        values.put(CreateDatabase.PACKAGE_NAME, databaseModel.getPackageName());
        values.put(CreateDatabase.TIME, databaseModel.getTime());

        long id = sqLiteDatabase.update(CreateDatabase.TABLE_NAME, values,
                CreateDatabase.PACKAGE_NAME+" = ?", new String[]{String.valueOf(databaseModel.getPackageName())});
        this.close();

        return id > 0;
    }

    public ArrayList<DatabaseModel> getAllData(){
        ArrayList<DatabaseModel> databaseModels = new ArrayList<>();
        this.open();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+CreateDatabase.TABLE_NAME, null);
        cursor.moveToFirst();

        if (cursor.getCount()>0){
            for (int i =1; i<=cursor.getCount(); i++){
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


    public DatabaseModel getDataByPackage(String packageName){
        this.open();
        String[] columns = {
                CreateDatabase.PACKAGE_NAME,
                CreateDatabase.TIME
        };

        String selection = CreateDatabase.PACKAGE_NAME + " = ?";
        String[] selectionArgs = {packageName};

        Cursor cursor = sqLiteDatabase.query(CreateDatabase.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        DatabaseModel databaseModel = new DatabaseModel();
        if (cursor.getCount()>0){
            String packageN = cursor.getString(cursor.getColumnIndex(CreateDatabase.PACKAGE_NAME));
            String time = cursor.getString(cursor.getColumnIndex(CreateDatabase.TIME));
            databaseModel = new DatabaseModel(packageN, Long.parseLong(time));
        }

        cursor.close();
        this.close();
        return databaseModel;
    }
}
