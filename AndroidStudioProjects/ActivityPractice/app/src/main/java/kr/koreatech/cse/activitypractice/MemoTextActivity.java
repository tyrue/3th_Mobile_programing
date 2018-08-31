package kr.koreatech.cse.activitypractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MemoTextActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memotext);
    }

    public boolean onCreateOptionsMenu(Menu menu)   // 메뉴 생성
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.memotext_menu, menu); // memotext_menu메뉴를 생성한다.
        return true;
    }

    public  boolean onOptionsItemSelected(MenuItem item)    // 메뉴 클릭 이벤트
    {
        switch (item.getItemId())       // 아이템 아이디에 따라 실행이 달라짐
        {
            case  R.id.menu1:           // menu1일 때
                return true;
        }
        return false;
    }
}