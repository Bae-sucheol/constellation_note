package com.example.constellation_note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper
{

    private String Table_name = "note";

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String id = "id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        String title = "title TEXT NOT NULL, ";
        String content = "content TEXT NOT NULL, ";
        String timestamp = "timestamp TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))";

        String query = "CREATE TABLE note(" +
                id +
                title +
                content +
                timestamp +
                ");";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        String query = "DROP TABLE note";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public String getTable_name()
    {
        return this.Table_name;
    }

}
