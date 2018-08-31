package com.example.song.songui;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Song on 2017-12-19.
 */

public class ListViewAdapter extends ArrayAdapter<ListView_item>
{
    private ArrayList<ListView_item> listView_items = new ArrayList<ListView_item>();

    public ListViewAdapter(Context context, int resource, ArrayList<ListView_item> objects)
    {
        super(context, resource, objects);
        this.listView_items = objects;
    }

    @Override
    public int getCount() {
        return listView_items.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();
        ListView_item temp;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1);
        RadioButton radio = (RadioButton) convertView.findViewById(R.id.radio);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListView_item listViewItem = listView_items.get(position);

        temp = listView_items.get(position);

        if (temp != null) {
            //List Item control
            if(temp.getCheck() == true){
                convertView.setBackgroundColor(Color.argb(100,0, 0, 0));
            }
            else
            {
                convertView.setBackgroundColor(Color.alpha(0));
            }
        }
        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.getTitleStr());
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public ListView_item getItem(int position) {
        return listView_items.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String title)
    {
        ListView_item item = new ListView_item(title);
        listView_items.add(item);
    }
}
