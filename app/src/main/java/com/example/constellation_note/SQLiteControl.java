package com.example.constellation_note;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteControl
{
    private SQLiteHelper helper;
    private SQLiteDatabase sqlite;

    public SQLiteControl(SQLiteHelper helper)
    {
        this.helper = helper;
    }

    // 삽입
    public void insert(ContentValues contentValues)
    {
        sqlite = helper.getWritableDatabase();

        sqlite.insert(helper.getTable_name(), null, contentValues);
    }

}
