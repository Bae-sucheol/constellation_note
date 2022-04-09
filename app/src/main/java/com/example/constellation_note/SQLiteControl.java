package com.example.constellation_note;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteControl
{
    private SQLiteHelper helper;
    private SQLiteDatabase sqlite;

    private String Table_note = "note";
    private String Table_constellation = "constellation";

    public SQLiteControl(SQLiteHelper helper)
    {
        this.helper = helper;
    }

    // 삽입
    public void insert(String table ,ContentValues contentValues)
    {
        sqlite = helper.getWritableDatabase();

        sqlite.insert(table, null, contentValues);

        System.out.println("삽입되었습니다.");
        System.out.println(contentValues);
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


    public List<String[]> select(String table, String columns[], String selection, String selectionArgs[])
    {
        sqlite = helper.getReadableDatabase();
        // String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy
        Cursor cursor = sqlite.query(table, columns, selection, selectionArgs, null, null, null);

        List<String[]> returnValues = new ArrayList<>();

        String returnValue[] = new String[columns.length];

        while(cursor.moveToNext())
        {
            for(int i = 0; i < returnValue.length; i++)
            {
                int index = cursor.getColumnIndex(columns[i]);
                returnValue[i] = cursor.getString(index);
            }

            returnValues.add(returnValue);

        }

        cursor.close();

        return returnValues;
    }

    // 커넥션 종료
    public void db_close()
    {
        sqlite.close();
        helper.close();
    }

    public String getTable_note()
    {
        return this.Table_note;
    }

    public String getTable_constellation()
    {
        return this.Table_constellation;
    }
}
