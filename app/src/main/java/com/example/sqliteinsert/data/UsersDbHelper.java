package com.example.sqliteinsert.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sqliteinsert.Message;

public class UsersDbHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "myDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_COMMANDS =
            "CREATE TABLE "+ UserContract.UserEntry.TABLE_NAME +
            "( " +
                UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserContract.UserEntry.COLUMN_NAME + " VARCHAR(225) UNIQUE NOT NULL CHECK (length(" + UserContract.UserEntry.COLUMN_NAME + ") > 0)," +
                UserContract.UserEntry.COLUMN_PASSWORD + " VARCHAR(225) NOT NULL CHECK (length(" + UserContract.UserEntry.COLUMN_PASSWORD + ") > 0)" +
            ");";

    private static final String DROP_TABLE_COMMANDS = "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    private Context context;

    public UsersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_COMMANDS);
        } catch (Exception e) {
            Message.message(context,""+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          try {
               db.execSQL(DROP_TABLE_COMMANDS);
               this.onCreate(db);
           }catch (Exception e) {
               Message.message(context,""+e);
           }
    }
}