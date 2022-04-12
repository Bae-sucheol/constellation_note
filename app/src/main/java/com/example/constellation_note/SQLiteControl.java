package com.example.constellation_note;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteControl implements Runnable
{
    private static final int TASK_NONE = 0;
    private static final int TASK_INSERT = 1;
    private static final int TASK_DELETE = 2;
    private static final int TASK_UPDATE = 3;
    private static final int TASK_SELECT = 4;

    private SQLiteHelper helper;
    private SQLiteDatabase sqlite;

    private String Table_note = "note";
    private String Table_constellation = "constellation";

    private int task_id = TASK_NONE;
    private String table;
    private String id;
    private ContentValues contentValues;

    public SQLiteControl(SQLiteHelper helper)
    {
        this.helper = helper;
    }

    @Override
    public void run()
    {

        try
        {

            switch(task_id)
            {
                case TASK_NONE :

                    // 설정된 작업이 없다는 경고 메시지를 출력해야함
                    // 지금은 임시로 print 함수로 대충 출력
                    // 어차피 해당 경고가 뜰 일은 없음.
                    // 테스트용으로 보면 됨.
                    System.out.println("현재 지정된 작업이 없습니다.");

                    break;

                case TASK_INSERT :

                    sqlite.insert(table, null, contentValues);

                    break;

                case TASK_DELETE :

                    sqlite.delete(table, "id=?", new String[] {id});

                    break;

                case TASK_UPDATE :

                    sqlite.update(table, contentValues, "id=?", new String[] {id});

                    break;

                case TASK_SELECT :

                    // 미정

                    break;

                default:

                    break;
            }

            db_close();

        }
        catch(Exception e)
        {

        }

    }

    // 삽입
    public void insert(String table ,ContentValues contentValues)
    {
        task_id = TASK_INSERT;
        this.table = table;
        this.contentValues = contentValues;

        sqlite = helper.getWritableDatabase();

        //sqlite.insert(table, null, contentValues);
    }

    // 삭제
    public void delete(String table, String id)
    {
        task_id = TASK_DELETE;
        this.table = table;
        this.id = id;

        sqlite = helper.getWritableDatabase();

        //sqlite.delete(table, "id=?", new String[] {id});
    }

    // 갱신
    public void update(String table, String id, String title)
    {
        task_id = TASK_UPDATE;
        this.table = table;
        this.id = id;

        sqlite = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);

        this.contentValues = values;

       // sqlite.update(table, values, "id=?", new String[] {id});
    }

    /* 일단 select 는 핸들러와 루퍼 개념을 조금 더 숙지 한 이후에 적용해볼 것.
    public List<String[]> select(String table, String columns[], String selection, String selectionArgs[])
    {
        task_id = TASK_SELECT;

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
     */

    // 커넥션 종료
    public void db_close()
    {
        task_id = TASK_NONE;
        table = null;
        id = null;
        contentValues= null;

        sqlite.close();
        //helper.close();
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
