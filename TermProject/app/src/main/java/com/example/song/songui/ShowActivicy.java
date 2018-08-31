package com.example.song.songui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.song.songui.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Song on 2017-12-18.
 */

public class ShowActivicy extends AppCompatActivity {

    ImageView img_show, prev_show, next_show, food_img;
    TextView title_show, text_show, page_show, test;
    String TAG = "mytest";
    int textID, imageID;
    String myname, pkgName;
    String[] cook, order;
    int now, timeCount;
    DrawerLayout dlay;
    ListView d_ListView;
    ArrayList<ListView_item> d_List;
    ListViewAdapter d_Adapter;

    private static final String CLIENT_ID = "66avQ0aidLCJbQbwGSmy";
    public final int MY_PERMISSIONS_RECORD = 1;

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;

    private String mResult; // 음성인식 문자열


    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak
                test.setText("Ready");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                Log.i("MesTag", "partial : "+mResult);
            break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = results.get(0);
                //mResult = strBuf.toString();
                Log.i("MesTag", "final : "+mResult);
                test.setText(mResult);

                if(mResult.contains("이전")) {
                    now = now - 2;
                    if(now >= 0) {
                        imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                        img_show.setImageResource(imageID);
                        title_show.setText(cook[now]);
                        text_show.setText(cook[now+1]);
                        page_show.setText(now/2 + " / " + (cook.length/2-1));
                        dlay.closeDrawer(Gravity.LEFT);
                    } else {
                        now = now + 2;
                        Toast.makeText(getApplicationContext(), "첫 페이지입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else if (mResult.contains("다음")) {
                    now = now + 2;
                    if(now <= cook.length-1) {
                        imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                        img_show.setImageResource(imageID);
                        title_show.setText(cook[now]);
                        text_show.setText(cook[now + 1]);
                        page_show.setText(now/2 + " / " + (cook.length/2-1));
                        dlay.closeDrawer(Gravity.LEFT);
                    } else {
                        now = now - 2;
                        Toast.makeText(getApplicationContext(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                } else if(mResult.contains("순서") || mResult.contains("목록")) {
                    dlay.openDrawer(Gravity.LEFT);
                } else if(mResult.contains("취소")) {
                    dlay.closeDrawer(Gravity.LEFT);
                } else {
                    for(int i = 0; i < order.length; i++) {
                        if((mResult.contains(i + "번")) || (mResult.contains(i + "단계"))) {
                            now = i * 2;
                            imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                            img_show.setImageResource(imageID);
                            title_show.setText(cook[now]);
                            text_show.setText(cook[now + 1]);
                            page_show.setText(now/2 + " / " + (cook.length/2-1));
                            dlay.closeDrawer(Gravity.LEFT);
                        }
                    }
                }
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                test.setText(mResult);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;
        }
        d_List.get(now/2).setCheck(true);
        for(int a = 0; a < d_List.size(); a++)
        {
            if(now/2 != a)
                d_List.get(a).setCheck(false);
        }
        d_Adapter.notifyDataSetChanged();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<ShowActivicy> mActivity;

        RecognitionHandler(ShowActivicy activity) {
            mActivity = new WeakReference<ShowActivicy>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowActivicy activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        Intent intent = getIntent();
        myname = intent.getStringExtra("KEY_NAME");
        Log.i(TAG, myname);

        pkgName = getPackageName();

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();

        img_show = (ImageView)findViewById(R.id.img_show);
        title_show = (TextView)findViewById(R.id.title_show);
        text_show = (TextView)findViewById(R.id.text_show);
        prev_show = (ImageView)findViewById(R.id.prev_show);
        next_show = (ImageView)findViewById(R.id.next_show);
        page_show = (TextView)findViewById(R.id.page_show);
        test = (TextView)findViewById(R.id.test);
        dlay = (DrawerLayout)findViewById(R.id.drawer);
        food_img = (ImageView)findViewById(R.id.food_img);
        imageID = getResources().getIdentifier(myname + "_0", "drawable", pkgName);
        textID = getResources().getIdentifier(myname, "raw", pkgName);

        img_show.setImageResource(imageID);
        food_img.setImageResource(imageID);

        // audio permission request
        if(ContextCompat.checkSelfPermission(ShowActivicy.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(ShowActivicy.this,
                    Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(ShowActivicy.this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_RECORD);
                Log.i(TAG, "Permission request");
            } else {
                ActivityCompat.requestPermissions(ShowActivicy.this,
                        new String[] {Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD);
            }
        } else { }

        // file open
        try {
            InputStream is = getResources().openRawResource(textID);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);

            Log.i("Input data : ", new String(buffer));
            String tmp = new String(buffer);
            cook = tmp.split(":::");
            /* arrayList에 목록을 저장하는 함수
            for(int i = 0; i < cook.length; i++) {
                list.add(cook[now]);
                now++;
            }*/
            is.close();
        } catch(IOException e) {
            Log.i("TAG","Exception");
            e.printStackTrace();
        }
        order = new String[cook.length/2];
        for(int i = 0; i <= cook.length - 1; i = i+2) {
            order[i/2] = cook[i];
        }
        d_ListView = (ListView)findViewById(R.id.drawer_menulist);
        d_List = new ArrayList<>();
        d_Adapter = new ListViewAdapter(this, R.layout.listview_item, d_List);
        d_ListView.setAdapter(d_Adapter);
        for(int i = 0; i < order.length; i++) {
            d_Adapter.addItem(order[i]);
        }
        d_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                now = i * 2;
                imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                img_show.setImageResource(imageID);
                title_show.setText(cook[now]);
                text_show.setText(cook[now + 1]);
                page_show.setText(now/2 + " / " + (cook.length/2-1));
                dlay.closeDrawer(Gravity.LEFT);
                Toast.makeText(getApplicationContext(), i + "번째 순서입니다.", Toast.LENGTH_SHORT).show();

                d_List.get(now/2).setCheck(true);
                for(int a = 0; a < d_List.size(); a++)
                {
                    if(now/2 != a)
                        d_List.get(a).setCheck(false);
                }
                d_Adapter.notifyDataSetChanged();
            }
        });

        if(d_List.get(0) != null) {
            d_List.get(0).setCheck(true);
        }
        
        title_show.setText(cook[0]);
        text_show.setText(cook[1]);
        page_show.setText("0 / " + (cook.length/2-1));

        d_List.get(now/2).setCheck(true);
        for(int i = 0; i < d_List.size(); i++)
        {
            if(now/2 != i)
                d_List.get(i).setCheck(false);
        }
        d_Adapter.notifyDataSetChanged();
        // Speeach Recognize using Timer
        TimerTask tTask = new TimerTask() {
            @Override
            public void run() {
                timeCount++;
                if(timeCount == 5) {
                    if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        // Start button is pushed when SpeechRecognizer's state is inactive.
                        // Run SpeechRecongizer by calling recognize().
                        mResult = "";
                        naverRecognizer.recognize();
                        test.post(new Runnable() {
                            @Override
                            public void run() {
                                test.setText("Connecing...");
                            }
                        });
                        timeCount = 0;
                    } else {
                        Log.d(TAG, "timer stop and wait Final Result");
                        timeCount = 0;
                        //naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(tTask, 0, 1000);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }
    @Override
    protected void onResume() {
        super.onResume();

        mResult = "";
    }
    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.prev_show:
                now = now - 2;
                if(now >= 0) {
                    imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                    img_show.setImageResource(imageID);
                    title_show.setText(cook[now]);
                    text_show.setText(cook[now+1]);
                    page_show.setText(now/2 + " / " + (cook.length/2-1));
                } else {
                    now = now + 2;
                    Toast.makeText(getApplicationContext(), "첫 페이지입니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.next_show:
                now = now + 2;
                if(now <= cook.length-1) {
                    imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
                    img_show.setImageResource(imageID);
                    title_show.setText(cook[now]);
                    text_show.setText(cook[now + 1]);
                    page_show.setText(now/2 + " / " + (cook.length/2-1));
                } else {
                    now = now - 2;
                    Toast.makeText(getApplicationContext(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        d_List.get(now/2).setCheck(true);
        for(int i = 0; i < d_List.size(); i++)
        {
            if(now/2 != i) {
                d_List.get(i).setCheck(false);
            }
        }
        d_Adapter.notifyDataSetChanged();
    }
    /*
    ListView.OnItemClickListener onDrawerClicked = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            now = i * 2;
            imageID = getResources().getIdentifier(myname + "_" +(now/2), "drawable", pkgName);
            img_show.setImageResource(imageID);
            title_show.setText(cook[now]);
            text_show.setText(cook[now + 1]);
            page_show.setText(now/2 + " / " + (cook.length/2-1));
            dlay.closeDrawer(Gravity.LEFT);
        }
    };*/
} // ShowActivity
