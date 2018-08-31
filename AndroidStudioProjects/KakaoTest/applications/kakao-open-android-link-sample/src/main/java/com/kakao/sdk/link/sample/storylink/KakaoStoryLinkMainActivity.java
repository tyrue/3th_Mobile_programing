package com.kakao.sdk.link.sample.storylink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.kakao.sdk.link.sample.R;
import com.kakao.sdk.link.sample.common.widget.KakaoDialogSpinner;
import com.kakao.sdk.link.sample.common.widget.KakaoToast;
import com.kakao.util.helper.log.Logger;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author leoshin on 15. 10. 6.
 */
public class KakaoStoryLinkMainActivity extends Activity implements OnClickListener {
    private static final int PICK_FROM_GALLERY = 0;
    private static final int SEND_STORY_GALLERY_IMAGE = 1;

    private KakaoDialogSpinner linkServiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_storylink_main);

        linkServiceType = (KakaoDialogSpinner) findViewById(R.id.send_type);
        findViewById(R.id.send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String type = String.valueOf(linkServiceType.getSelectedItem());
        if (type.equals(getString(R.string.story_url_scrap_link))) {
            sendPostingLink();
        } else {
            startImageGallery(this, PICK_FROM_GALLERY);
        }
    }

    public void sendPostingLink() {
        Map<String, Object> urlInfoAndroid = new Hashtable<String, Object>(1);
        urlInfoAndroid.put("title", "(광해) 실제 역사적 진실은?");
        urlInfoAndroid.put("desc", "(광해 왕이 된 남자)의 역사성 부족을 논하다.");
        urlInfoAndroid.put("imageurl", new String[] {"http://m1.daumcdn.net/photo-media/201209/27/ohmynews/R_430x0_20120927141307222.jpg"});
        urlInfoAndroid.put("type", "article");

        // Recommended: Use application context for parameter.
        StoryLink storyLink = StoryLink.getLink(getApplicationContext());

        // check, intent is available.
        if (!storyLink.isAvailableIntent()) {
            KakaoToast.makeToast(this, "KakaoStory not installed.", Toast.LENGTH_SHORT);
            return;
        }

        storyLink.openKakaoLink(this,
                "http://m.media.daum.net/entertain/enews/view?newsid=20120927110708426",
                getPackageName(),
                "1.0",
                "미디어디음",
                "UTF-8",
                urlInfoAndroid);
    }

    private static void startImageGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            StoryLink storyLink = StoryLink.getLink(getApplicationContext());

            // check, intent is available.
            if (!storyLink.isAvailableIntent()) {
                KakaoToast.makeToast(this, "Not installed KakaoStory.", Toast.LENGTH_SHORT);
                return;
            }

            Uri uri = data.getData();
            Cursor c = getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                c.moveToNext();
                String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                c.close();
                storyLink.openStoryLinkImageApp(this, path);
            } else {
                Logger.e("Cursor for " + uri + " is null.");
            }

        }
    }
}
