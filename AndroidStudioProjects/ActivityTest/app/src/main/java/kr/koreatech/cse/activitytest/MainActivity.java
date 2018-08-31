package kr.koreatech.cse.activitytest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    static final int GET_STRING = 1;
    static final int GET_NUMBER = 2;
    TextView text1, text2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button)findViewById(R.id.button);
        text1 = (TextView)findViewById(R.id.text);
        text2 = (TextView)findViewById(R.id.text2);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent in = new Intent(MainActivity.this, SubActivity.class);
                startActivityForResult(in, GET_STRING);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == GET_STRING)
        {
            if(resultCode == RESULT_OK)
            {
                text1.setText(data.getStringExtra("INPUT_TEXT"));
                text2.setText(data.getStringExtra("INPUT_TEXT2"));
            }
            else if(resultCode == RESULT_CANCELED)
            {
                //
            }
        }
        else if(requestCode == GET_NUMBER)
        {
            //
        }
    }
}