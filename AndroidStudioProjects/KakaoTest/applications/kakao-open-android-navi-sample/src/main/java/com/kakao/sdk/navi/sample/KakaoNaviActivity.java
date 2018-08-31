package com.kakao.sdk.navi.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaonavi.Destination;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import java.util.LinkedList;
import java.util.List;

public class KakaoNaviActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_navi);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_main));
        }
        ListView naviMethodListView = (ListView) findViewById(R.id.navi_method_list_view);
        Button naviButton = (Button) findViewById(R.id.kakao_navi_button);

        naviMethodListView.setAdapter(new NaviMethodAdapter(this));
        naviMethodListView.setOnItemSelectedListener(this);
        naviMethodListView.setOnItemClickListener(this);
        naviButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kakao_navi_button:
                onNaviButtonClicked();
                break;
            default:
                break;
        }
    }

    private void onNaviButtonClicked() {
        if (position == -1) {
            Toast.makeText(getApplicationContext(), "실행하고 싶은 목적지 공유 / 길 안내를 선택하세요.", Toast.LENGTH_LONG).show();
            return;
        }
        Location kakao = Location.newBuilder("카카오 판교 오피스", 321256, 533732).build();
        KakaoNaviParams params;
        switch (position) {
            case 1:
                params = KakaoNaviParams.newBuilder(kakao).build();
                KakaoNaviService.shareDestination(this, params);
                break;
            case 2:
                kakao = Destination.newBuilder("카카오 판교 오피스", 127.10821222694533, 37.40205604363057).build();
                params = KakaoNaviParams.newBuilder(kakao).setNaviOptions(NaviOptions.newBuilder().setCoordType(CoordType.WGS84).build()).build();
                KakaoNaviService.shareDestination(this, params);
                break;
            case 4:
                kakao = Location.newBuilder("카카오 판교 오피스", 321256, 533732).build();
                params = KakaoNaviParams.newBuilder(kakao).setNaviOptions(new NaviOptions.Builder().build()).build();
                KakaoNaviService.navigate(this, params);
                break;
            case 5:
                kakao = Destination.newBuilder("카카오 판교 오피스", 127.10821222694533, 37.40205604363057).build();
                Location stop = Location.newBuilder("서서울호수공원", 126.8322289016308, 37.528495607451205).build();
                List<Location> stops = new LinkedList<Location>();
                stops.add(stop);
                params = KakaoNaviParams.newBuilder(kakao).setNaviOptions(NaviOptions.newBuilder().setCoordType(CoordType.WGS84).build()).setViaList(stops).build();
                KakaoNaviService.navigate(this, params);
                break;
            case 6:
                params = KakaoNaviParams.newBuilder(kakao).setNaviOptions(NaviOptions.newBuilder().setRouteInfo(true).build()).build();
                KakaoNaviService.navigate(this, params);
                break;
            default:
                break;
        }
    }

    class NaviMethodAdapter extends BaseAdapter {
        private final Context context;
        private final String[] shareMethods = {
                "카카오 판교 오피스",
                "카카오 판교 오피스 WGS84",
        };
        private final String[] naviMethods = {
                "카카오 판교 오피스",
                "카카오 판교 오피스 WGS84 경유지 추가",
                "카카오 판교 오피스 전체 경로 보기"
        };

        public NaviMethodAdapter(final Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return shareMethods.length + naviMethods.length + 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            TextView methodTextView = null;

            if (position == 0 || position == 3) {
                convertView = getLayoutInflater().inflate(R.layout.navi_method_header_layout, parent, false);
                textView = (TextView) convertView.findViewById(R.id.navi_method_header_text);
            } else {
                convertView = getLayoutInflater().inflate(R.layout.navi_method_layout, parent, false);
                methodTextView = (TextView) convertView.findViewById(R.id.navi_method_text);
            }

            if (position == 0) {
                textView.setText("목적지 공유");
            } else if (position == 3){
                textView.setText("목적지 길안내");
            } else if (position <= shareMethods.length){
                methodTextView.setText(shareMethods[position - 1]);
            } else {
                methodTextView.setText(naviMethods[position - shareMethods.length - 2]);
            }

            return convertView;
        }
    }
}
