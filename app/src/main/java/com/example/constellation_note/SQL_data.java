package com.example.constellation_note;

import android.content.ContentValues;

public class SQL_data
{

    private int task_id;
    private int select_id;
    private boolean update_multiple = false;
    private String table;
    private String id;
    private ContentValues contentValues;
    private String columns[];
    private String selection;
    private String selectionArgs[];

    public SQL_data()
    {

    }

    public SQL_data(String id)
    {
        this.id = id;
    }

    public SQL_data(int task_id, String table, String[] columns, int select_id)
    {
        this.task_id = task_id;
        this.table = table;
        this.columns = columns;
        this.select_id = select_id;
    }

    public SQL_data(int task_id, String table, ContentValues contentValues)
    {
        this.task_id = task_id;
        this.table = table;
        this.contentValues = contentValues;
    }

    public SQL_data(int task_id, String table, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        this.task_id = task_id;
        this.table = table;
        this.contentValues = contentValues;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public SQL_data(int task_id, String table, String[] columns, String selection, String[] selectionArgs, int select_id)
    {
        this.task_id = task_id;
        this.table = table;
        this.columns = columns;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.select_id = select_id;
    }

    public SQL_data(int task_id, String table, String selection, String[] selectionArgs)
    {
        this.task_id = task_id;
        this.table = table;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public SQL_data(int task_id, String table, String selection, boolean update_multiple)
    {
        this.task_id = task_id;
        this.table = table;
        this.selection = selection;
        this.update_multiple = update_multiple;
    }


    // setter

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public void setSelect_id(int select_id) {
        this.select_id = select_id;
    }

    public void setUpdate_multiple(boolean update_multiple) {
        this.update_multiple = update_multiple;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void set_id(String id) {
        this.id = id;
    }

    public void setContentValues(ContentValues contentValues) {
        this.contentValues = contentValues;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        this.selectionArgs = selectionArgs;
    }

    // getter

    public int getTask_id() {
        return task_id;
    }

    public int getSelect_id() {
        return select_id;
    }

    public boolean getUpdate_multiple() {
        return update_multiple;
    }

    public String getTable() {
        return table;
    }

    public String get_id() {
        return id;
    }

    public ContentValues getContentValues() {
        return contentValues;
    }

    public String[] getColumns() {
        return columns;
    }

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

}
