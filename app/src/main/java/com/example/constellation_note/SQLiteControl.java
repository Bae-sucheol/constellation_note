package com.example.constellation_note;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;;

public class SQLiteControl implements Runnable
{
    public static final int TASK_NONE = 0;
    public static final int TASK_INSERT = 1;
    public static final int TASK_DELETE = 2;
    public static final int TASK_UPDATE = 3;
    public static final int TASK_SELECT = 4;
    public static final int TASK_ALTER = 5;

    private int select_id = 0;
    private boolean update_multiple = false;

    private SQLiteHelper helper;
    private SQLiteDatabase sqlite;

    private String Table_note = "note";
    private String Table_constellation = "constellation";

    private int task_id = TASK_NONE;
    private String table;
    private String id;
    private ContentValues contentValues;

    private String columns[];
    private String selection;
    private String selectionArgs[];

    private Handler handler;

    private static ArrayList<SQL_data> queue = new ArrayList<>();

    public SQLiteControl(SQLiteHelper helper, Handler handler)
    {
        this.helper = helper;
        this.handler = handler;
    }

    @Override
    public void run()
    {

        try
        {
            System.out.println("sql 작업이 실행되고 있습니다.");
            apply_sqldata();

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

                    int result = sqlite.delete(table, selection, selectionArgs);

                    System.out.println("삭제 결과 : " + result);
                    
                    break;

                case TASK_UPDATE :

                    if(update_multiple)
                    {

                        String target;

                        if(table.equals(getTable_constellation()))
                        {
                            target = "id";
                        }
                        else
                        {
                            target = "constellation_id";
                        }

                        sqlite.execSQL(String.format("UPDATE %s SET %s = %s - 1 WHERE %s;",
                                table, target, target , selection));

                    }
                    else
                    {
                        sqlite.update(table, contentValues, selection, selectionArgs);
                    }

                    break;

                case TASK_SELECT :

                    Message message;
                    Bundle bundle = new Bundle();
                    Cursor cursor = sqlite.query(table, columns, selection, selectionArgs, null, null, null);

                    if(select_id == MainActivity.GET_LAST_CONSTELLATION_ID)
                    {

                        if(cursor.moveToLast())
                        {
                            bundle.putBoolean("isEmpty", false);
                            bundle.putInt("id", cursor.getInt(0));
                        }
                        else
                        {
                            bundle.putBoolean("isEmpty", true);
                            bundle.putInt("id", 0);
                        }

                    }
                    else if(select_id == MainActivity.GET_STARS_LIST)
                    {

                        ArrayList<Star_data> returnValues = new ArrayList<>();

                        while(cursor.moveToNext())
                        {

                            Star_data data = new Star_data();
                            data.set_id(cursor.getInt(0));
                            data.setTitle(cursor.getString(1));
                            data.setContent(cursor.getString(2));
                            data.setParent_index(cursor.getInt(4));
                            data.setRelative_x(cursor.getFloat(5));
                            data.setRelative_y(cursor.getFloat(6));
                            data.setConstellation_id(cursor.getInt(7));

                            returnValues.add(data);
                        }
                        bundle.putParcelableArrayList("stars", returnValues);
                    }
                    else
                    {

                        ArrayList<Constellation_data> returnValues = new ArrayList<>();

                        while(cursor.moveToNext())
                        {

                            if(table.equals(getTable_constellation()))
                            {

                                Constellation_data data = new Constellation_data();
                                data.setId(cursor.getInt(0));
                                data.setTitle(cursor.getString(1));

                                returnValues.add(data);
                            }
                            else if(table.equals(getTable_note()))
                            {
                                Star_data data = new Star_data();

                                data.setId(cursor.getInt(0));
                                data.setTitle(cursor.getString(1));

                                returnValues.add(data);

                            }

                        }

                        bundle.putParcelableArrayList("constellations", returnValues);

                    }

                    message = handler.obtainMessage();
                    message.what = select_id;
                    message.setData(bundle);

                    handler.sendMessage(message);

                    cursor.close();

                    break;

                case TASK_ALTER :

                    sqlite.execSQL(String.format("UPDATE SQLITE_SEQUENCE SET SEQ = %s WHERE NAME = %s;",
                           id, getTable_constellation()));

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

    public static void put_sqldata(SQL_data sql_data)
    {
        queue.add(sql_data);
    }

    private void apply_sqldata()
    {

        if(queue.size() == 0)
        {
            return;
        }

        SQL_data sql_data = queue.remove(0);
        //queue.remove(0);

        this.task_id = sql_data.getTask_id();

        this.select_id = sql_data.getSelect_id();
        this.update_multiple = sql_data.getUpdate_multiple();

        this.table = sql_data.getTable();
        this.id = sql_data.get_id();
        this.contentValues = sql_data.getContentValues();

        this.columns = sql_data.getColumns();
        this.selection = sql_data.getSelection();
        this.selectionArgs = sql_data.getSelectionArgs();

        if(task_id == TASK_SELECT)
        {
            sqlite = helper.getReadableDatabase();
        }
        else
        {
            sqlite = helper.getWritableDatabase();
        }

    }

    // 커넥션 종료
    public void db_close()
    {
        task_id = TASK_NONE;
        select_id = 0;
        update_multiple = false;
        table = null;
        id = null;
        contentValues= null;
        columns = null;
        selection = null;
        selectionArgs = null;

        //sqlite.close();
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


