package com.example.song.songui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView m_ListView;
    private ThumbnailAdapter m_Adapter;
    private ArrayList<Thumbnail> m_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setCustomView(R.layout.actionbar_main);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowTitleEnabled(false);

        m_List = new ArrayList<>();
        m_List.add(new Thumbnail("알리오올리오", "우리들의 첫 오일 파스타", "alio_0", "alio"));
        m_List.add(new Thumbnail("스크램블 에그", "간편하고 쉬운 계란 요리", "scramble_0", "scramble"));
        m_List.add(new Thumbnail("제육볶음", "누구나 좋아하는", "jeyuk_0", "jeyuk"));
        m_List.add(new Thumbnail("탕수육", "중국집하면 떠오르는", "tang_0", "tang"));
        m_List.add(new Thumbnail("마약토스트", "맛있게 먹으면 0칼로리", "drug_0", "drug"));
        m_Adapter = new ThumbnailAdapter(this, R.layout.row, m_List);
        m_ListView = (ListView)findViewById(R.id.main_list);
        m_ListView.setAdapter(m_Adapter);
        m_ListView.setOnItemClickListener(onClickListItem);

    }

    public class Thumbnail {
        private String title_main;
        private String title_sub;
        private String source;
        private String name;

        public Thumbnail(String title_main, String title_sub, String source, String name) {
            this.title_main = title_main;
            this.title_sub = title_sub;
            this.source = source;
            this.name = name;
        }

        public String getTitle_main() {
            return title_main;
        }

        public String getTitle_sub() {
            return title_sub;
        }

        public String getSource() {
            return source;
        }

        public String getName() {
            return name;
        }
    }

    public class ThumbnailAdapter extends ArrayAdapter<Thumbnail> {
        private ArrayList<Thumbnail> items;

        public ThumbnailAdapter(Context context, int textViewResourceId, ArrayList<Thumbnail> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Thumbnail t = items.get(position);
            if (t != null) {
                TextView st = (TextView) v.findViewById(R.id.subtext);
                TextView mt = (TextView) v.findViewById(R.id.maintext);
                ImageView thumbimage = (ImageView) v.findViewById(R.id.thumbimage);
                FrameLayout lay = (FrameLayout) v.findViewById(R.id.layout);
                if (st != null) {
                    st.setText(t.getTitle_sub());
                }
                if (mt != null) {
                    mt.setText(t.getTitle_main());
                }
                if (lay != null) {
                    String resName = "drawable/" + t.getSource();
                    int lid = MainActivity.this.getResources().getIdentifier(resName, "drawable", MainActivity.this.getPackageName());
                    thumbimage.setImageResource(lid);
                }
            }
            return v;
        } // getView()
    } // class ThumbnailAdapter
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(MainActivity.this, ShowActivicy.class);
            intent.putExtra("KEY_NAME", m_List.get(i).getName());
            startActivity(intent);
        }
    };
}
