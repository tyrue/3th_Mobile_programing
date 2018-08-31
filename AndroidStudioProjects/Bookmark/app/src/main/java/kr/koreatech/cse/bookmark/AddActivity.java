package kr.koreatech.cse.bookmark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class AddActivity extends AppCompatActivity // 즐겨찾기 이름과 uri을 저장하는 액티비티
{
    private static final String FILE_NAME = "myFile.txt"; // 저장할 파일 이름
    EditText edit1, edit2;  // EditText뷰

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // xml로 부터 뷰를 생성한다.
        edit1 = (EditText)findViewById(R.id.editText);
        edit2 = (EditText)findViewById(R.id.editText2);
        Button Btn = (Button)findViewById(R.id.button);

        Btn.setOnClickListener(new View.OnClickListener() // 버튼 이벤트 리스너 구현
        {
            @Override
            public void onClick(View v) // 클릭 이벤트
            {
                // 개발자를 위한 레시피 블로그 참조함. http://recipes4dev.tistory.com/113
                File file = new File(getFilesDir(), FILE_NAME); // 파일 객체 생성
                FileWriter fw = null;        // 파일 쓰기 객체
                BufferedWriter bufwr = null; // 파일 쓰기 버퍼
                try
                {
                    // 파일 열기
                    fw = new FileWriter(file,true); // 파일을 이어쓰기 한다.
                    bufwr = new BufferedWriter(fw);

                    String name = edit1.getText().toString(); // 입력한 텍스트를 받아옴
                    String uri = edit2.getText().toString();  // 입력한 텍스트를 받아옴

                    // 만약 입력된 텍스트 없이 추가하기 버튼을 클릭한다면 기본 값을 넣어준다.
                    if(name.isEmpty() || name == null) name = "이름없음";
                    if(uri.isEmpty() || uri == null) uri = "www.google.com";

                    bufwr.write(name + "," + uri); // 파일에 문자열을 저장함
                    bufwr.newLine(); // 다음 줄로 넘어감
                    bufwr.flush();  // 버퍼 초기화

                    // 파일 닫기
                    if (bufwr != null)  bufwr.close();
                    if (fw != null)  fw.close();
                }
                catch (Exception e) {e.printStackTrace();} // 오류 캐치
                finish();   // 액티비티 종료
            }
        });
    }
}