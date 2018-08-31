package kr.koreatech.cse.testnavi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{

    ListView listview;
    ImageView food_img;
    ArrayList<ListView_item> value;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        value = new ArrayList<ListView_item>();
        final String[] items = {"WHITE", "RED", "GREEN", "BLUE", "BLACK", "gd"} ;

        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.listview_item, value) ;
        listview = (ListView) findViewById(R.id.drawer_menulist) ;
        listview.setAdapter(adapter);
        food_img = (ImageView) findViewById(R.id.food_img);

        food_img.setImageResource(R.drawable.knives);
        for(String s : items) {
            adapter.addItem(s);
        }

        listview.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView parent, View v, int position, long id)
            {


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;
                drawer.closeDrawer(Gravity.LEFT) ;
             }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
