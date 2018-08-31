package kr.koreatech.cse.activitytest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SubActivity extends AppCompatActivity
{
    EditText edit1, edit2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        edit1 = (EditText)findViewById(R.id.edit);
        edit2 = (EditText)findViewById(R.id.edit2);

        Button okBtn = (Button)findViewById(R.id.button_ok);
        okBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("INPUT_TEXT", edit1.getText().toString());
                intent.putExtra("INPUT_TEXT2",edit2.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.button_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}