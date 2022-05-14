package com.example.good_bad_game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button signUp = findViewById(R.id.btn_sign_up);
        EditText email = findViewById(R.id.email);
        EditText pw = findViewById(R.id.password);
        Button btn = findViewById(R.id.btn_sign_in);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        Button home = findViewById(R.id.btn_sign_in);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        });

        //----------------------------------------------22.05.10 (Django <-> Android 로그인)

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipt_email = email.getText().toString();
                String ipt_pw = passwordHash(pw.getText().toString());


                LoginService LoginService = retrofit.create(LoginService.class);

                Call<List<Login>> call = LoginService.getPosts();

                call.enqueue(new Callback<List<Login>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<Login>> call, Response<List<Login>> response) {
                        if (!response.isSuccessful())
                        {
                            Log.d("onResponse 발동","Connection은 성공하였으나 code 에러 발생");
                            AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                            ad.setTitle("에러");
                            ad.setMessage("Code:" + response.code());
                            ad.show();
                            return;

                        }

                        List<Login> Login_infos = response.body();

                        for ( Login login_info : Login_infos)
                        {
                            Log.d("onResponse 발동","내부 데이터를 하나씩 가져와서 Login 정보와 비교 시작");
                            Log.d("이름 : ", ipt_email);
                            Log.d("데베이름 : ", login_info.get_mail());
                            Log.d("패스워드 : ", ipt_pw);
                            Log.d("데베패스워드 : ", login_info.get_password());
                            if(ipt_email.equals(login_info.get_mail()) && ipt_pw.equals(login_info.get_password())) {
                                Log.d("성공!","이름, 폰번호 일치");
                                AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                                ad.setTitle("성공");
                                ad.setMessage("ID : " + login_info.get_mail() + "    password : " + login_info.get_password());
                                ad.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), Home.class);
                                        startActivity(intent);
                                    }
                                }, 3000);

                                break;
                            }
                        }

                        // home 화면 이동 작성해야함

                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<Login>> call, Throwable t) {
                        Log.d("onFailure 발동","Connection Error");
                        AlertDialog.Builder ad = new AlertDialog.Builder(SignIn.this);
                        ad.setTitle("에러");
                        ad.setMessage(t.getMessage());
                        ad.show();

                    }
                });

            }
        });

    }

    public static String passwordHash(String password){
        return sha1("kD0a1"+md5("xA4"+password)+"f4A");
    }

    // SHA ( Secure Hash Algorithm )
    public static String sha1(String clearString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(clearString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }
        catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    // MD5 ( Message Digest Algorithm : 무결성 검사에 사용하는 128비트 해쉬 함수 )
    // IETF의 RFC 1321로 지정되어 있으나 다수의 중요 결함이 발견되어 현재는 해쉬 용도로 SHA(해쉬함수 집합)와 같이 사용.
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}