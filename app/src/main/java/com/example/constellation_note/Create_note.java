package com.example.constellation_note;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.Attribution;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class Create_note extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener
{
    private Toolbar toolbar;
    private SQLiteControl sqLiteControl;

    private EditText edit_title;
    private EditText edit_content;

    private int id;
    private int constellation_id;
    private String title;
    private String content;
    private byte[] drawing;

    private Intent recognizer_intent;
    private SpeechRecognizer speechRecognizer;
    private final int PERMISSION = 1;
    private boolean recording = false;
    private RecognitionListener recognitionListener;

    private FrameLayout layout_content;
    private LinearLayout layout_draw_menu;
    private LinearLayout layout_color_menu;

    private ImageView imageView_color_picker;
    private ImageView imageView_eraser;
    private ImageView imageView_undo;
    private ImageView imageView_redo;

    private ImageView imageView_color_black;
    private ImageView imageView_color_gray;
    private ImageView imageView_color_red;
    private ImageView imageView_color_blue;

    private Draw_view draw_view;

    private TextView textView_pen_width;
    private SeekBar seekBar_pen_width;

    private int modify_count = 0;
    private Stack<Character> modify_stack = new Stack<>();
    private char last_char;
    private int start_text_length;
    private int current_text_length;
 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        permission_check();

        toolbar = findViewById(R.id.create_note_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = this.getWindow();

        window.setStatusBarColor(Color.WHITE);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        edit_title = findViewById(R.id.edit_create_note_toolbar_title);
        edit_content = findViewById(R.id.edit_create_note_content);

        // 인텐트
        Intent intent = getIntent();

        id = intent.getIntExtra("id", 0);
        constellation_id = intent.getIntExtra("constellation_id", 0);
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        drawing = intent.getByteArrayExtra("drawing");

        edit_title.setText(title);
        edit_content.setText(content);
        start_text_length = title.length() - 1;
        current_text_length = start_text_length;

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

        layout_content = findViewById(R.id.layout_content);
        layout_draw_menu = findViewById(R.id.layout_draw_menu);
        layout_color_menu = findViewById(R.id.layout_color_menu);

        imageView_color_picker = findViewById(R.id.imageView_color_picker);
        imageView_eraser = findViewById(R.id.imageView_eraser);
        imageView_undo = findViewById(R.id.imageView_undo);
        imageView_redo = findViewById(R.id.imageView_redo);

        imageView_color_black = findViewById(R.id.imageView_color_black);
        imageView_color_gray = findViewById(R.id.imageView_color_gray);
        imageView_color_red = findViewById(R.id.imageView_color_red);
        imageView_color_blue = findViewById(R.id.imageView_color_blue);

        textView_pen_width = findViewById(R.id.textView_pen_width);
        seekBar_pen_width = findViewById(R.id.seekBar_pen_width);

        imageView_color_picker.setOnClickListener(this);
        imageView_color_picker.setOnLongClickListener(this);
        imageView_eraser.setOnClickListener(this);
        imageView_undo.setOnClickListener(this);
        imageView_redo.setOnClickListener(this);

        imageView_color_black.setOnClickListener(this);
        imageView_color_gray.setOnClickListener(this);
        imageView_color_red.setOnClickListener(this);
        imageView_color_blue.setOnClickListener(this);

        seekBar_pen_width.setOnSeekBarChangeListener(this);

        // 그림용 view를 framelayout에 추가.
        draw_view = new Draw_view(this, drawing);
        draw_view.setBackgroundColor(Color.WHITE);
        draw_view.setEnabled(false);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        draw_view.setLayoutParams(layoutParams);

        layout_content.addView(draw_view, 0);

        // 따로 클래스를 지정하여 오버라이드 하기 번거로워 그냥 사용한다.

        edit_content.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int length, int delete, int insert)
            {

                if(delete == 1)
                {
                    current_text_length -= delete;
                    //System.out.println("char : " + charSequence.charAt(current_text_length));
                    if(modify_count > 0)
                    {
                        modify_count--;
                    }
                    else
                    {
                        modify_stack.push(charSequence.charAt(current_text_length));
                    }

                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int length, int delete, int insert)
            {
                if(insert == 1)
                {
                    current_text_length += insert;
                    //System.out.println("char : " + charSequence.charAt(current_text_length - 1));

                    if(!modify_stack.empty())
                    {
                        if(modify_stack.peek() == charSequence.charAt(current_text_length - 1))
                        {
                            modify_stack.pop();
                        }
                        else
                        {
                            modify_count++;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

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
                String error_message = "";

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

        switch(item.getItemId())
        {
            case android.R.id.home :
                // finish 이전에 저장 작업을 먼저 해주어야 한다.

                update_data();
                update_drawing();

                finish_activity();
                break;

            case R.id.action_stt :

                // stt 관련 이벤트 작성

                if(!recording)
                {
                    record_voice();
                }

                break;

            case R.id.action_delete :

                String selection = "id = ? and constellation_id = ?";
                String arg1 = Integer.toString(id);
                String arg2 = Integer.toString(constellation_id);

                sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_DELETE, sqLiteControl.getTable_note(), selection, new String[] {arg1, arg2}));
                MainActivity.submitRunnable(sqLiteControl);

                finish_activity();
                //return true;
                break;

            case R.id.action_draw :

                if(layout_draw_menu.getVisibility() == View.VISIBLE)
                {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24));
                    layout_draw_menu.setVisibility(View.GONE);
                    edit_content.setVisibility(View.VISIBLE);
                    draw_view.setEnabled(false);
                }
                else
                {
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_keyboard_24));
                    layout_draw_menu.setVisibility(View.VISIBLE);
                    edit_content.setVisibility(View.GONE);
                    draw_view.setEnabled(true);
                }


                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {

        update_data();
        update_drawing();
        finish_activity();

        super.onBackPressed();

    }

    private void update_data()
    {
        boolean textChanged = isTextChanged();
        boolean titleChanged = isTitleChanged();

        if(!textChanged && !titleChanged)
        {
            return;
        }

        ContentValues contentValues = new ContentValues();

        if(titleChanged)
        {
            contentValues.put("title", title);
        }

        if(textChanged)
        {
            contentValues.put("content", content);
        }

        title = edit_title.getText().toString();
        content = edit_content.getText().toString();

        String selection = "id = ? and constellation_id = ?";
        String arg1 = Integer.toString(id);
        String arg2 = Integer.toString(constellation_id);

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), contentValues, selection, new String[] {arg1, arg2}));
        MainActivity.submitRunnable(sqLiteControl);
    }

    private void finish_activity()
    {
        Intent intent = new Intent();
        intent.putExtra("requestCode", 1);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("drawing", drawing);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.imageView_color_picker :

                draw_view.setPenMode();

                break;
            case R.id.imageView_eraser :

                draw_view.setEraserMode();

                break;
            case R.id.imageView_undo :

                draw_view.undo();

                break;
            case R.id.imageView_redo :

                draw_view.redo();

                break;
            case R.id.imageView_color_black :

                draw_view.setColor_id(Color.BLACK);
                layout_color_menu.setVisibility(View.GONE);

                break;
            case R.id.imageView_color_gray :

                draw_view.setColor_id(Color.LTGRAY);
                layout_color_menu.setVisibility(View.GONE);

                break;
            case R.id.imageView_color_red :

                draw_view.setColor_id(Color.RED);
                layout_color_menu.setVisibility(View.GONE);

                break;
            case R.id.imageView_color_blue :

                draw_view.setColor_id(Color.BLUE);
                layout_color_menu.setVisibility(View.GONE);

                break;
            default :

                break;
        }
    }

    @Override
    public boolean onLongClick(View view)
    {

        // 어차피 롱클릭 할 요소는 1개이므로 따로 아이디를 거를 필요가 없다.
        layout_color_menu.setVisibility(View.VISIBLE);

        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {

       textView_pen_width.setText( Integer.toString(i + 4));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        draw_view.setWidth(seekBar.getProgress() + 4);
    }

    public void drawing()
    {
        if(layout_color_menu.getVisibility() == View.GONE)
        {
            return;
        }

        layout_color_menu.setVisibility(View.GONE);
    }

    public void update_drawing()
    {
        if(!draw_view.isDrawing())
        {
            return;
        }

        draw_view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(draw_view.getDrawingCache());
        draw_view.setDrawingCacheEnabled(false);

        drawing = bitmapToByteArray(bitmap);

        String selection = "id = ? and constellation_id = ?";
        String arg1 = Integer.toString(id);
        String arg2 = Integer.toString(constellation_id);

        ContentValues contentValues = new ContentValues();
        contentValues.put("drawing", drawing);

        sqLiteControl.put_sqldata(new SQL_data(sqLiteControl.TASK_UPDATE, sqLiteControl.getTable_note(), contentValues, selection, new String[] {arg1, arg2}));
        MainActivity.submitRunnable(sqLiteControl);
    }

    public byte[] bitmapToByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        return stream.toByteArray() ;
    }

    private boolean isTextChanged()
    {
        if(start_text_length != current_text_length)
        {
            return true;
        }
        if(!modify_stack.empty())
        {
            return true;
        }

        System.out.println("내용이 바뀌지 않았습니다.");
        return false;
    }

    private boolean isTitleChanged()
    {
        if(title.equals(edit_title.getText().toString()))
        {
            System.out.println("타이틀이 바뀌지 않았습니다.");
            return false;
        }

        return true;
    }
}