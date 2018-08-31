package kr.koreatech.cse.playertest;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    // audio/video 파일 재생을 제어하는데 사용하는 클래스
    MediaPlayer player;
    Notification.Builder noti;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        player = MediaPlayer.create(this, R.raw.bangarang_feat); // create(Context context, int resid)
        player.setLooping(false); // 반복재생 여부 설정

        //***************************************
        // Service를 Foreground로 실행하기 위한 과정

        // 1. Notification 객체 생성
        // 1-1. Intent 객체 생성 - MainActivity 클래스를 실행하기 위한 Intent 객체
        Intent intent = new Intent(this, MainActivity.class);
        // 1-2. Intent 객체를 이용하여 PendingIntent 객체를 생성 - Activity를 실행하기 위한 PendingIntent
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 1-3. Notification 객체 생성
        noti = new Notification.Builder(this)
                .setContentTitle("Music service")
                .setContentText("Service is running... start an activity")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent);

        // 2. foregound service 설정 - startForeground() 메소드 호출, 위에서 생성한 nofication 객체 넘겨줌
        startForeground(123, noti.build());

        //****************************************
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // intent: startService() 호출 시 넘기는 intent 객체
        // flags: service start 요청에 대한 부가 정보. 0, START_FLAG_REDELIVERY, START_FLAG_RETRY
        // startId: start 요청을 나타내는 unique integer id
        Log.d(TAG, "onStartCommand()");
        Toast.makeText(this, "MusicService 시작", Toast.LENGTH_SHORT).show();

        player.start();
        //noti.setContentText("gd");
       // noti.notify();
        return START_STICKY;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        Toast.makeText(this, "MusicService 중지", Toast.LENGTH_SHORT).show();

        player.stop();
        player.release();
        player = null;
    }

    // 아래 onBind 메소드가 없으면 어떻게 될까?
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}