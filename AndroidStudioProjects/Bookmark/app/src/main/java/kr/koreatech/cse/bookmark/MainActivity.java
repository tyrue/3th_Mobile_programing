package kr.koreatech.cse.bookmark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// 즐겨찾기 리스트 액티비티. OnItemLongClickListener, ActionMode.Callback 인터페이스 구현함
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, ActionMode.Callback
{
    private static final String FILE_NAME = "myFile.txt"; // 저장할 파일 이름

    private int po = 0;                             // 컨텍스트 액션 메뉴를 호출한 리스트 아이템의 위치
    ActionMode mActionMode;                         // 컨텍스트 액션 모드
    private ListView m_ListView;                   // 리스트 뷰 객체
    private ArrayAdapter<String> m_Adapter;        // 어댑터
    ArrayList<String> values = new ArrayList<>();   // 이름을 저장할 리스트
    ArrayList<String> uri = new ArrayList<>();       // uri을 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadItemsFromFile();    // 파일에서 읽어들어 values, uri리스트에 저장
        // Android에서 제공하는 String 문자열 하나를 출력하는 layout으로 어댑터 생성
        m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        m_ListView = (ListView) findViewById(R.id.list);       // Xml에서 추가한 ListView의 객체
        m_ListView.setAdapter(m_Adapter);                     // ListView에 어댑터 연결
        m_ListView.setOnItemClickListener(onClickListItem);  // ListView 아이템 터치 시 이벤트를 처리할 리스너 설정
        m_ListView.setOnItemLongClickListener(this);           // ListView 아이템 롱 클릭 시 이벤트를 처리할 리스너 설정
    }

    @Override
    protected void onResume() // onResume재정의, 파일을 불러온다.
    {
        super.onResume();
        values.clear();        // 이름 리스트 초기화
        uri.clear();           // uri 리스트 초기화
        loadItemsFromFile();    // 파일에서 읽어들어 values, uri리스트에 저장
        m_Adapter.notifyDataSetChanged(); // 리스트 갱신
    }

    public boolean onCreateOptionsMenu(Menu menu) // 메뉴 삽입
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);        // 이름이 menu인 메뉴를 넣는다.
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) // 메뉴 클릭 이벤트처리 메소드
    {
        switch (item.getItemId())
        {
            case R.id.add: // 메뉴가 add일 때
                Intent in = new Intent(this, AddActivity.class);
                startActivity(in); // AddActivity로 전환한다.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 리스트 아이템 터치 이벤트 리스너 구현, 해당 url에 대한 홈페이지 띄우기
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // 해당 아이템에 대한 uri 주소를 인텐트에 저장한다. 브라우저이므로 http://로 시작
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + uri.get(position)));
            if(intent != null) // 값이 있다면
            {
                if(intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent); // 브라우저 실행
            }
        }
    };

    // startActionMode() 메소드가 호출될 때 호출되는 콜백 메소드, 컨텍스트 메뉴를 생성
    public  boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context, menu);
        return true;
    }
    // onCreateActionMode()가 호출된 후에 호출
    // 액션 메뉴를 refresh하는 목적으로 여러 번 호출될 수 있다.
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        return false; // 아무 것도 하지 않을 때 false반환, 액션 메뉴가 업데이트 되면 true 반환
    }

    // 사용자가 액션 메뉴항목을 클릭했을 때 호출
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.del: // delete버튼 클릭한 경우 해당 리스트 아이템을 삭제
                int count = m_Adapter.getCount(); // 리스트 아이템의 개수
                int index = po; // 리스트뷰에서 선택된 아이템 인덱스
                if (count > 0 ) // 아이템이 있다면 동작
                {
                    if (index > -1 && index < count) // 인덱스가 리스트 안에 있다면
                    {
                        values.remove(index);  // 이름 삭제
                        uri.remove(index);     // uri 삭제
                        m_Adapter.notifyDataSetChanged(); // 리스트뷰 갱신
                        saveItemsToFile();      // 리스트뷰 아이템들을 파일에 저장.
                        // 삭제완료 메시지 출력
                        Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                    }
                }
                mode.finish(); // 컨택스트 액션 모드 종료
                return true;
            default:
                return false;
        }
    }

    // 사용자가 컨택스트 액션모드를 빠져나갈 때 호출
    public void onDestroyActionMode(ActionMode mode)
    {
        po = 0;
        mActionMode = null;
    }

    // 리스트뷰 아이템 롱 클릭 이벤트 리스너 구현
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(mActionMode != null)
            return false;
        po = position; // 이벤트를 실행한 리스트의 인덱스를 저장한다.

        mActionMode = this.startSupportActionMode(this); // 컨텍스트 액션모드 시작
        view.setSelected(true);
        return  true;
    }

    // 개발자를 위한 레시피 블로그 참조함. http://recipes4dev.tistory.com/113
    private void saveItemsToFile() // 파일에 저장할 때는 이름, uri다 저장함
    {
        File file = new File(getFilesDir(), FILE_NAME); // 파일 객체 생성
        FileWriter fw = null;          // 파일 쓰기 객체
        BufferedWriter bufwr = null;   // 쓰기 버퍼 객체
        try
        {
            // 파일 열기
            fw = new FileWriter(file);
            bufwr = new BufferedWriter(fw);

            for(int i = 0; i < m_Adapter.getCount(); i++ ) // 리스트 아이템 수 만큼 반복
            {
                String str = values.get(i) + "," + uri.get(i); // 이름과 uri을 합친다.
                bufwr.write(str); // 파일에 쓴다.
                bufwr.newLine() ; // 다음 줄로 넘어간다.
            }
            bufwr.flush(); // 버퍼 초기화

            // 파일 닫기
            if (bufwr != null) bufwr.close();
            if (fw != null)    fw.close();
        }
        catch (Exception e) { e.printStackTrace() ; } // 오류 캐치
    }

    // 개발자를 위한 레시피 블로그 참조함. http://recipes4dev.tistory.com/113
    private void loadItemsFromFile() // 파일로부터 리스트뷰에 저장할 때는 이름은 리스트, uri는 따로 저장함
    {
        File file = new File(getFilesDir(), FILE_NAME); // 파일 객체 생성
        FileReader fr = null;        // 파일 읽기 객체
        BufferedReader bufrd =null;  // 읽기 버퍼 객체
        String str;                   // 파일로 부터 읽을 문자열
        if (file.exists())          // 파일이 존재할 때
        {
            try
            {
                // 파일 열기
                fr = new FileReader(file) ;
                bufrd = new BufferedReader(fr) ;
                while ((str = bufrd.readLine()) != null) // 파일의 끝 까지 한 줄씩 읽어 간다.
                {
                    String s[] = str.split(","); // 이름과 uri을 분리
                    values.add(s[0]);   // 이름 저장
                    uri.add(s[1]);      // uri 저장
                }
                // 파일 닫기
                bufrd.close() ;
                fr.close() ;
            }
            catch (Exception e) { e.printStackTrace() ; }
        }
    }
}
