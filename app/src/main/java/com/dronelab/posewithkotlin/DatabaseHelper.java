package com.dronelab.posewithkotlin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Logs.db";
    private static final String TABLE_NAME = "Logs_Table";
    //columns
    private static final String ACTIVITY = "ACTIVITY";
    private static final String TIME = "TIME";

    private static final String CREATE_TABLE = "CREATE_TABLE " + TABLE_NAME + " (" +
            ACTIVITY + " TEXT, " +
            TIME + " TEXT " + ")";

    public boolean insert(String activity){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACTIVITY, activity);

        long result = db.insert(TABLE_NAME,null,contentValues);
        return result != -1; //if data is -1 then insert was not successful

    }


    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase){
        sqliteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, int i, int il){
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqliteDatabase);

    }
}
