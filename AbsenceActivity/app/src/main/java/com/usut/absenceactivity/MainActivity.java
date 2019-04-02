package com.usut.absenceactivity;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

    }

    public void btnCall(View view) {
        Intent intent=new Intent();
        getIntent().setAction(Intent.ACTION_DIAL);

        Uri uri=Uri.parse("tel:01012345678");
        intent.setData(uri);
        startActivity(intent);



    }

    public void btnMail(View view) {

        Intent intent=new Intent();

        intent.setAction(Intent.ACTION_SENDTO);

        Uri uri=Uri.parse("smsto:01012345678");
        intent.setData(uri);

        startActivity(intent);

        //문자내용도 미리 작성되도록 되려면...추가 데이터 필요
        intent.putExtra("sms_body","Hello Nice to meet you" );
    }
    public void btnAbsence(View view) {
        //ChattingActivity실행
        Intent intent=new Intent(this,ChattingActivity.class);
        startActivity(intent);

    }

    public void btnMap(View view) {
        Intent intent=new Intent(this,MapActivity.class);
        startActivity(intent);




    }


    public void clickBtn(View view) {
    }


    public void clickImage(View view) {
    }
}
