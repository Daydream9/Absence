package com.usut.absenceactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity {

    EditText etName;
    CircleImageView ivProfile;

    Uri imgUri;//갤러리앱에서 선택한 이미지의 uri


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        etName= findViewById(R.id.et_name);
        ivProfile= findViewById(R.id.iv_profile);

        loadData();
        if(G.nickName!=null){
            Intent intent= new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void clickImage(View view) {
        //갤러리 앱 실행 및 선택결과 받기
        Intent intent= new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch ( requestCode ){
            case 10:
                if(resultCode==RESULT_OK){
                    imgUri= data.getData();
                    //Glide.with(this).load(imgUri).into(ivProfile);
                    Picasso.get().load(imgUri).into(ivProfile);
                }
                break;
        }
    }

    public void clickBtn(View view) {
        //닉네임 얻어오기
        G.nickName= etName.getText().toString();

        //프로필이미지을 firebase저장소에 업로드하기
        saveData();
    }

    void saveData(){
        //프로필이미지를 firebase저장소에 업로드하기
        if(imgUri==null) return;

        //firebaseStorage 관리 객체 얻어오기
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        //이미지파일 노드명이 중복되지 않도록..
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName= sdf.format( new Date())+".png";

        //'root'노드 아래에 'profileImages'라는 폴더 안에..저장
        //노드참조객체 얻어오기
        final StorageReference imgRef= firebaseStorage.getReference("profileImages/"+fileName);

        //이미지 업로드
        UploadTask uploadTask= imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //이미지 업로드에 성공했으므로...
                //업로드된 이미지의 다운로드 주소(URL)을 얻어오기
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //firebase저장소에 있는 이미지의 다운로드 주소를 문자열로..
                        G.profileUrl= uri.toString();
                        Toast.makeText(ChattingActivity.this, "프로필 저장완료", Toast.LENGTH_SHORT).show();

                        //firebase DB에 저장하기!!
                        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                        //'profiles'라는 자식노드 참조
                        DatabaseReference profilesRef= firebaseDatabase.getReference("profiles");

                        //닉네임을 키[식별자]로 하고 프로필이미지의 url을 값을 저장
                        profilesRef.child(G.nickName).setValue(G.profileUrl);


                        //phone에 영구적으로 닉네임과 프로필이미지경로를 저장
                        SharedPreferences pref= getSharedPreferences("account", MODE_PRIVATE);
                        SharedPreferences.Editor editor= pref.edit();

                        editor.putString("nickName", G.nickName);
                        editor.putString("profileUrl", G.profileUrl);

                        editor.commit();

                        //저장까지 모두 완료되었다면...채팅화면 액티비티로 이동
                        Intent intent= new Intent(ChattingActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });



            }
        });
    }


    void loadData(){
        SharedPreferences pref= getSharedPreferences("account", MODE_PRIVATE);
        G.nickName= pref.getString("nickName", null);
        G.profileUrl= pref.getString("profileUrl", null);
    }


}
