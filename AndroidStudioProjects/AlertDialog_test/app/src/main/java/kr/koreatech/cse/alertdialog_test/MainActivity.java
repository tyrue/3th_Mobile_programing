package kr.koreatech.cse.alertdialog_test;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    private static final int DIALOG_YES_NO_MESSAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener()
        {
            public  void onClick(View v)
            {
                showDialog(DIALOG_YES_NO_MESSAGE);
            }
        });
    }

    protected Dialog onCreateDialog(int id )
    {
        switch (id)
        {
            case DIALOG_YES_NO_MESSAGE:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("종료 확인 대화 상자")
                        .setMessage("애플리케이션을 종료하시겠습니까?")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener()
                        {
                            public  void onClick(DialogInterface dialog, int which)
                            {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                return  alert;
        }
        return null;
    }
}
