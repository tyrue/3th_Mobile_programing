package com.kakao.sdk.link.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.sdk.link.sample.kakaolink.KakaoLinkV2MainActivity;
import com.kakao.sdk.link.sample.kakaolink.KakaoLinkImageUploadActivity;
import com.kakao.sdk.link.sample.kakaolink.KakaoLinkOsShareActivity;
import com.kakao.sdk.link.sample.storylink.KakaoStoryLinkMainActivity;

public class KakaoServiceListActivity extends Activity implements OnClickListener, AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_service_list);

        GridView servicesView = (GridView) findViewById(R.id.services_grid_view);


        final LinkService[] services = getLinkServices();
        servicesView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return services.length;
            }

            @Override
            public Object getItem(int position) {
                return services[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("InflateParams")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(KakaoServiceListActivity.this);
                    convertView = inflater.inflate(R.layout.item_link_service, null);
                }

                ImageView imageView = (ImageView) convertView.findViewById(R.id.service_icon);
                TextView textView = (TextView) convertView.findViewById(R.id.service_name);

                imageView.setImageDrawable(getResources().getDrawable(services[position].drawableId));
                textView.setText(getString(services[position].titleId));
                return convertView;
            }
        });

        servicesView.setOnItemClickListener(this);
    }

    private LinkService[] getLinkServices() {
        return new LinkService[] {
                new LinkService(R.drawable.icon_main_01, R.string.title_activity_kakao_link40_main, KakaoLinkV2MainActivity.class),
                new LinkService(R.drawable.icon_main_01, R.string.title_activity_kakao_link_upload, KakaoLinkImageUploadActivity.class),
                new LinkService(R.drawable.icon_main_02, R.string.title_activity_kakao_story_link_main, KakaoStoryLinkMainActivity.class),
                new LinkService(R.drawable.icon_main_01, R.string.title_activity_content_share, KakaoLinkOsShareActivity.class),
        };
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, getLinkServices()[position].getActivityClass()));
    }

    private static class LinkService {
        final int drawableId;
        final int titleId;
        final Class<?> aClass;

        LinkService(int drawableId, int titleId, Class<?> aClass) {
            this.drawableId = drawableId;
            this.titleId = titleId;
            this.aClass = aClass;
        }

        Class<?> getActivityClass() {
            return aClass;
        }
    }
}
