package kr.koreatech.cse.activitypractice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) // 이 이벤트를 가진 뷰는 다른 액티비티를 띄움
    {
        Intent intent = new Intent(this, MemoTextActivity.class); // MemoTextActivity인텐트를 생성
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu)   // 메뉴 생성
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);   // main_menu를 생성한다.
        return true;
    }

    public  boolean onOptionsItemSelected(MenuItem item)    // 메뉴 클릭 이벤트
    {
        switch (item.getItemId())   // 아이템 아이디에 따라 실행이 달라짐
        {
            case  R.id.menu1:       // menu1일 때
                Intent intent = new Intent(this, MemoWriteActivity.class); // MemoWriteActivity인텐트를 생성
                startActivity(intent);  // 액티비티 실행
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
