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
    public void insert(String table ,ContentValues contentValues)
    {
        sqlite = helper.getWritableDatabase();

        sqlite.insert(table, null, contentValues);
    }

    // 삭제
    public void delete(String table, String id)
    {
        sqlite = helper.getWritableDatabase();

        sqlite.delete(table, "id=?", new String[] {id});
    }

    // 갱신
    public void update(String table, String id, String title)
    {
        sqlite = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);

        sqlite.update(table, values, "id=?", new String[] {id});
    }

    public void db_close()
    {
        sqlite.close();
        helper.close();
    }
}
