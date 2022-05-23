package com.example.constellation_note;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Create_note extends AppCompatActivity
{

    private Toolbar toolbar;
    private SQLiteControl sqLiteControl;

    private Star_data star_data;
    private int constellation_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        toolbar = findViewById(R.id.create_note_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 인텐트
        Intent intent = getIntent();
        constellation_id = intent.getIntExtra("constellation_id", 0);

        // sqLiteControl 정의
        Handler handler = MainHandler.getMainHandler(this);

        sqLiteControl = new SQLiteControl(SQLiteHelper.getSqLiteHelper(this), handler);

        star_data = new Star_data();

        getStarData();
    }

    // 먼저 데이터를 불러와야 한다.
    private void getStarData()
    {

        String selection = "constellation_id = ?";
        String selectionArgs[] = new String[1];

        selectionArgs[0] = Integer.toString(constellation_id);

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_SELECT, sqLiteControl.getTable_note(), new String[] {"*"}, selection, selectionArgs, MainActivity.GET_STARS_LIST));
        MainActivity.submitRunnable(sqLiteControl);

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

        switch(item.getItemId())
        {
            case R.id.home :
                // finish 이전에 저장 작업을 먼저 해주어야 한다.

                finish();
                return true;
            case R.id.action_delete :
                // finish 이전에 삭제 작업을 해주어야 한다.
                //finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        // 여기에 저장 작업을 해주어야 한다.

        super.onBackPressed();
    }
}