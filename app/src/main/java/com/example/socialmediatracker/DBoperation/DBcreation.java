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
                CreateDatabase.PACKAGE_NAME+" = "+
                        databaseModel.getPackageName(), null);
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
                BigInteger time = BigInteger.valueOf(cursor.getInt(cursor.getColumnIndex(CreateDatabase.TIME)));
                cursor.moveToNext();
                databaseModels.add(new DatabaseModel(packageName, time.longValue()));
            }

        }

        cursor.close();
        this.close();
        return databaseModels;
    }
}
