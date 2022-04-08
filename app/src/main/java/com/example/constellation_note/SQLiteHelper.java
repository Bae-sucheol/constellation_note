package com.example.constellation_note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper
{

    private String Table_note = "note";
    private String Table_constellation = "constellation";

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // constellation 테이블
        String id = "id INTEGER PRIMARY KEY AUTOINCREMENT, ";
        String title = "title TEXT NOT NULL";


        String query = "CREATE TABLE constellation(" +
                id +
                title +
                ");";

        sqLiteDatabase.execSQL(query);

        // note 테이블
        String content = "content TEXT NOT NULL, ";
        String timestamp = "timestamp TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), ";
        String parent_id = "parent_id INTEGER, ";
        String x = "x FLOAT, ";
        String y = "y FLOAT, ";
        String constellation_id = "constellation_id INTEGER, FOREIGN_KEY(constellation_id) REFERENCES constellation(id) ON DELETE CASCADE";

        query = "CREATE TABLE note(" +
                id +
                title +
                content +
                timestamp +
                parent_id +
                x +
                y +
                constellation_id +
                ");";

        //sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        String query = "DROP TABLE constellation";
        sqLiteDatabase.execSQL(query);

        query = "DROP TABLE note";
        sqLiteDatabase.execSQL(query);

        onCreate(sqLiteDatabase);
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
