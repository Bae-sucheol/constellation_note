package com.example.constellation_note;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Create_note extends AppCompatActivity
{

    private Toolbar toolbar;
    private SQLiteControl sqLiteControl;

    private EditText edit_title;
    private EditText edit_content;

    private int id;
    private int constellation_id;
    private String title;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        toolbar = findViewById(R.id.create_note_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_title = findViewById(R.id.edit_create_note_toolbar_title);
        edit_content = findViewById(R.id.edit_create_note_content);

        // 인텐트
        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);
        constellation_id = intent.getIntExtra("constellation_id", 0);
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");

        edit_title.setText(title);
        edit_content.setText(content);

        // sqLiteControl 정의
        Handler handler = MainHandler.getMainHandler(this);

        sqLiteControl = new SQLiteControl(SQLiteHelper.getSqLiteHelper(this), handler);

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        String selection = "id = ? and constellation_id = ?";
        String arg1 = Integer.toString(id);
        String arg2 = Integer.toString(constellation_id);

        switch(item.getItemId())
        {
            case android.R.id.home :
                // finish 이전에 저장 작업을 먼저 해주어야 한다.

                ContentValues contentValues = new ContentValues();
                contentValues.put("title", edit_title.getText().toString());
                contentValues.put("content", edit_content.getText().toString());

                sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), contentValues, selection, new String[] {arg1, arg2}));
                MainActivity.submitRunnable(sqLiteControl);

                finish();
                break;

            case R.id.action_delete :

                sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_DELETE, sqLiteControl.getTable_note(), selection, new String[] {arg1, arg2}));
                MainActivity.submitRunnable(sqLiteControl);

                finish();
                //return true;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        // 여기에 저장 작업을 해주어야 한다.

        String selection = "id = ? and constellation_id = ?";
        String arg1 = Integer.toString(id);
        String arg2 = Integer.toString(constellation_id);

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", edit_title.getText().toString());
        contentValues.put("content", edit_content.getText().toString());

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), contentValues, selection, new String[] {arg1, arg2}));
        MainActivity.submitRunnable(sqLiteControl);

        super.onBackPressed();
    }
}