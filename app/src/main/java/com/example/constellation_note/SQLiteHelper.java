package com.example.constellation_note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper
{
    // 싱글톤 패턴으로 만들어야 하기 때문에.. static으로 클래스 고유의(1개의, 유니크한) 객체를 만들어 놓는다.
    private static SQLiteHelper sqLiteHelper = null;

    private SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }
    
    public static SQLiteHelper getSqLiteHelper(Context context)
    {

        // 객체가 존재하지 않으면 만들어야한다.
        if(sqLiteHelper == null)
        {
            sqLiteHelper = new SQLiteHelper(context, "constellation_note.db", null, 59);
        }

        return sqLiteHelper;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        //db.execSQL("PRAGMA foreign_keys = ON;");
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {



        // constellation 테이블
        String id = "id INTEGER PRIMARY KEY, ";
        String title = "title TEXT NOT NULL";


        String query = "CREATE TABLE constellation(" +
                id +
                title +
                ");";

        sqLiteDatabase.execSQL(query);

        // note 테이블
        id = "id INTEGER, ";
        title = "title TEXT NOT NULL, ";
        String content = "content TEXT NOT NULL, ";
        String timestamp = "timestamp TEXT NOT NULL DEFAULT (datetime('now', 'localtime')), ";
        String parent_id = "parent_id INTEGER, ";
        String x = "x FLOAT NOT NULL, ";
        String y = "y FLOAT NOT NULL, ";
        String color = "color INTEGER NOT NULL, ";
        String constellation_id = "constellation_id INTEGER, ";
        String drawing = "drawing BLOB, ";
        String constraint = "CONSTRAINT constellation_id_fk FOREIGN KEY(constellation_id) REFERENCES constellation(id) ON UPDATE CASCADE ON DELETE CASCADE ";
        String constraint2 = "PRIMARY KEY(id, constellation_id)";

        query = "CREATE TABLE note(" +
                id +
                title +
                content +
                timestamp +
                parent_id +
                x +
                y +
                color +
                constellation_id +
                drawing +
                constraint +
                constraint2 +
                ");";

        sqLiteDatabase.execSQL(query);
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
}
