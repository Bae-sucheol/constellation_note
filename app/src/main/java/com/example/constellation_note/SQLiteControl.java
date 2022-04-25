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
    private static final int TASK_NONE = 0;
    private static final int TASK_INSERT = 1;
    private static final int TASK_DELETE = 2;
    private static final int TASK_UPDATE = 3;
    private static final int TASK_SELECT = 4;
    private static final int TASK_ALTER = 5;

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

                    sqlite.delete(table, selection, selectionArgs);
                    
                    break;

                case TASK_UPDATE :

                    if(update_multiple)
                    {
                        sqlite.execSQL(String.format("UPDATE %s SET %s = id - 1 WHERE %s;",
                                getTable_constellation(), "id" , selection));
                    }
                    else
                    {
                        sqlite.update(table, contentValues, "id = ?", new String[]{id});
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

                    sqlite.execSQL(String.format("ALTER TABLE %s AUTO_INCREMENT = %s;",
                            getTable_constellation(), id));

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
    public void delete(String table, String selection, String[] selectionArgs)
    {
        task_id = TASK_DELETE;
        this.table = table;
        //this.id = id;
        this.selection = selection;
        this.selectionArgs = selectionArgs;

        sqlite = helper.getWritableDatabase();

        //sqlite.delete(table, "id=?", new String[] {id});
    }

    // 갱신
    public void update(String table, ContentValues contentValues, String id)
    {
        task_id = TASK_UPDATE;
        this.table = table;
        this.id = id;
        this.contentValues = contentValues;

        sqlite = helper.getWritableDatabase();

       // sqlite.update(table, values, "id=?", new String[] {id});
    }

    public void update(String table, String selection)
    {
        task_id = TASK_UPDATE;
        update_multiple = true;
        this.table = table;
        this.selection = selection;

        sqlite = helper.getWritableDatabase();
    }

    public void select(String table, String columns[], String selection, String selectionArgs[], int flag)
    {
        task_id = TASK_SELECT;
        select_id = flag;

        sqlite = helper.getReadableDatabase();
        // String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy

        this.table = table;
        this.columns = columns;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public void set_autoincrement(String id)
    {
        task_id = TASK_ALTER;
        this.id = id;
        sqlite = helper.getWritableDatabase();
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
