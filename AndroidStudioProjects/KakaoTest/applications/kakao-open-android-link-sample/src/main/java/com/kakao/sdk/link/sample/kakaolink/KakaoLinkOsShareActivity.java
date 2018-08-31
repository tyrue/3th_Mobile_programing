package com.kakao.sdk.link.sample.kakaolink;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kakao.sdk.link.sample.R;
import java.util.ArrayList;

public class KakaoLinkOsShareActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_link_os_share);

        ListView listView = (ListView) findViewById(R.id.os_share_list_view);
        String[] shareMethods = new String[]{
                "Share a text",
                "Share an image",
                "Share multiple images",
        };

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shareMethods));
        listView.setOnItemClickListener(this);
    }

    private void sendText() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Share text from kakao link sample. https://dev.kakao.com");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.kakao.talk");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_intent_chooser)));
    }

    private void sendImage() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("image/png");
        Uri uri = Uri.parse("android.resource://com.kakao.sdk.link.sample/" + R.drawable.kakaolink_sample_icon);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setPackage("com.kakao.talk");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_intent_chooser)));
    }

    private void sendMultipleImages() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("image/png");
        ArrayList<Uri> files = new ArrayList<Uri>();
        Uri uri1 = Uri.parse("android.resource://com.kakao.sdk.link.sample/" + R.drawable.kakaolink_sample_icon);
        Uri uri2 = Uri.parse("android.resource://com.kakao.sdk.link.sample/" + R.drawable.kakaosdk_splash);
        files.add(uri1);
        files.add(uri2);
        sendIntent.setPackage("com.kakao.talk");
        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        startActivity(Intent.createChooser(sendIntent, getString(R.string.text_share_intent_chooser)));
    }

//    private void sendFile() {
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setType("application/pdf");
//        Uri uri = Uri.parse("content://com.kakao.sdk.link.sample/sample.pdf");
//        Logger.e(uri.toString());
//        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(sendIntent, "Which app to share?"));
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                sendText();
                break;
            case 1:
                sendImage();
                break;
            case 2:
                sendMultipleImages();
                break;
//            case 3:
//                sendFile();
//                break;
            default:
                break;
        }
    }
}
