package kr.koreatech.cse.test;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button addBtn;
    Button removeBtn;
    TextView countText;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
        {
            Bundle bundle = savedInstanceState.getParcelable("Bundle1");
            if(bundle != null)
            {
                // Bundle 객체에 데이터를 저장할 때 사용했던 key 값을 가지고 데이터를 얻는다
                String text = bundle.getString("Backup_string");
                // 해당 데이터를 EditText에 설정한다
                countText.setText(text);
            }
        }
        countText = (TextView)findViewById(R.id.count);
        addBtn = (Button)findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                countText.setText("현재 개수 = " + count);
            }
        });

        removeBtn = (Button)findViewById(R.id.remove);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count--;
                countText.setText("현재 개수 = " + count);
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.i("Mobile Programming", "onSaveInstanceState()");
        // EditText 내용을 onSaveInstanceState의 매개 변수인 Bundle 클래스 객체, outState에 저장
        // 먼저 EditText에 입력된 텍스트를 가지고 와서 String 객체로 변환한다
        Bundle bundle = new Bundle();
        String backupString = countText.getText().toString();
        bundle.putString("Backup_string", backupString);
        // Bundle 객체도 Intent와 비슷하게, key-value 쌍으로 데이터를 저장한다
        outState.putParcelable("Bundle1", bundle);
    }

    // 백업한 데이터를 전달받아 복원하는 함수
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("Mobile Programming", "onRestoreInstanceState()");
        // 만약 매개 변수인 Bundle 객체가 null이 아니면,
        // 해당 액티비티에서 백업한 데이터가 존재한다는 것을 의미
        // Bundle 객체에 백업된 데이터를 가지고 와서 EditText 내용을 복원한다
        if(savedInstanceState != null)
        {
            // Bundle 객체에 데이터를 저장할 때 사용했던 key 값을 가지고 데이터를 얻는다
            String text = savedInstanceState.getString("Backup_string");
            // 해당 데이터를 EditText에 설정한다
            countText.setText(text);
        }
    }
}