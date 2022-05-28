package com.example.constellation_note;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;

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

    private Intent recognizer_intent;
    private SpeechRecognizer speechRecognizer;
    private final int PERMISSION = 1;
    private boolean recording = false;
    private RecognitionListener recognitionListener;

 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        permission_check();

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

        // recognizer_intent 객체
        recognizer_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recognizer_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        // recognitionListener 클래스 따로 만들어서 연결해주기 귀찮아 그냥 클래스 내에서 처리하려고 한다.
        // 오버라이드 할 내용이 많아 메소드로 따로 처리하려고 한다.
        setRecognitionListener();

    }

    private void setRecognitionListener()
    {
        recognitionListener = new RecognitionListener()
        {

            @Override
            public void onReadyForSpeech(Bundle bundle) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float v) { }

            @Override
            public void onBufferReceived(byte[] bytes) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onPartialResults(Bundle bundle) { }

            @Override
            public void onEvent(int i, Bundle bundle) { }

            // 이부분만 쓸 것.
            // 기본적인 것만 구현하려고 하므로 에러처리와 결과만 받으면 된다.
            @Override
            public void onError(int error)
            {
                String error_message = null;

                switch(error)
                {
                    case SpeechRecognizer.ERROR_AUDIO :
                        error_message = "오디오 에러";
                        break;

                    case SpeechRecognizer.ERROR_CLIENT :
                        // stopListening 시 발생한다고 함.
                        // 즉 고의적인 에러 호출이므로 따로 처리 안해도 됨.
                        // 혹은 고의적으로 녹음을 멈추고 다시 시작하고 싶으면
                        // record_voice() 함수를 실행하면 된다.
                        break;

                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS :
                        error_message = "퍼미션 에러";
                        break;

                    case SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED :
                        error_message = "언어 미지원";
                        break;

                    case SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE :
                        error_message = "언어 사용불가";
                        break;

                    case SpeechRecognizer.ERROR_NETWORK:
                        error_message = "네트워크 에러";
                        break;

                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT :
                        error_message = "네트워크 타임아웃";
                        break;

                    case SpeechRecognizer.ERROR_NO_MATCH :
                        record_voice();
                        break;

                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY :
                        error_message = "Recognizer busy";
                        break;

                    case SpeechRecognizer.ERROR_SERVER :
                        error_message = "서버 에러";
                        break;

                    case SpeechRecognizer.ERROR_SERVER_DISCONNECTED :
                        error_message = "서버 연결 끊김";
                        break;

                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT :
                        error_message = "시간 초과";
                        break;

                    case SpeechRecognizer.ERROR_TOO_MANY_REQUESTS :
                        error_message = "너무 많은 요청";
                        break;

                    default :
                        error_message = "알 수 없는 오류";
                        break;

                }

                Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();

            }

            // 결과
            @Override
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String pre_text = edit_content.getText().toString();

                StringBuffer post_text = new StringBuffer(pre_text);
                Iterator<String> iterator = matches.iterator();

                post_text.append(" ");

                while(iterator.hasNext())
                {
                    post_text.append(iterator.next());
                }

                edit_content.setText(post_text.toString());
                recording = false;
            }


        };
    }

    private void permission_check()
    {

        int permission_internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permission_record =  ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        // 인터넷 퍼미션 혹은 녹음 퍼미션이 거부 상태일 경우
        if(permission_internet == PackageManager.PERMISSION_DENIED || permission_record == PackageManager.PERMISSION_DENIED)
        {
            // 퍼미션 요청
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }


    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void record_voice()
    {
        recording = true;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(recognitionListener);
        speechRecognizer.startListening(recognizer_intent);
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

            case R.id.action_stt :

                // stt 관련 이벤트 작성

                if(!recording)
                {
                    record_voice();
                }

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