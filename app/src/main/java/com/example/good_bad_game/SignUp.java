package com.example.good_bad_game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.os.Handler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {

    private String illegal_pw_content = "";
    private Boolean legal_email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        illegal_pw_content = "";
        legal_email = false;
        Button signIn = findViewById(R.id.btn_menu_sign_in);
        Button signUp = findViewById(R.id.btn_sign_up);
        EditText email = findViewById(R.id.email);
        EditText pw = findViewById(R.id.password);
        EditText nickname = findViewById(R.id.nickname);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipt_email = email.getText().toString();
                String ipt_password = pw.getText().toString();
                String ipt_nickname = nickname.getText().toString();

                // 비밀번호 정책 검사 시행할 것. 대문자, 소문자, 숫자, 특수문자 조합하여 8자리 이상.
                illegal_pw_content = isValidPassword(ipt_password);
                legal_email = isValidEmail(ipt_email);

                if (illegal_pw_content != "All Pass"){
                    AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                    ad.setTitle("비밀번호 정책 위반!");
                    ad.setMessage("위반사유 : " + illegal_pw_content);
                    ad.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), SignUp.class);
                            startActivity(intent);
                        }
                    }, 3000);
                }

                if (legal_email != true){
                    AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                    ad.setTitle("이메일 형식 위반!");
                    ad.setMessage("이메일을 올바르게 입력해주시기 바랍니다.");
                    ad.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), SignUp.class);
                            startActivity(intent);
                        }
                    }, 3000);
                }

                if (illegal_pw_content == "All Pass" && legal_email){

                    Log.d("암호화 전 비밀번호 : ", ipt_password);
//                ipt_password = passwordHash(ipt_password);
                    Log.d("암호화 된 비밀번호 : ", ipt_password);

                    Log.d("password 인증", illegal_pw_content);
                    Log.d("mail 인증", legal_email.toString());

                    LoginService LoginService = retrofit.create(LoginService.class);

                    Post post = new Post(ipt_email, ipt_password, ipt_nickname, "null", "null", "null");

                    Call<Post> call = LoginService.createPost(post);

                    call.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {
                            if (!response.isSuccessful()){
                                AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                                ad.setTitle("에러 발생");
                                ad.setMessage("에러코드 : " + response.code());
                                ad.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                        startActivity(intent);
                                    }
                                }, 3000);

                                return;
                            }

                            Post postResponse = response.body();

                            String content = "";
                            content += "Code : " + response.code() + "\n";

                            AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                            ad.setTitle("회원가입 완료!");
                            ad.setMessage("회원가입 하신 계정으로 로그인해주시길 바랍니다.");
                            ad.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                    startActivity(intent);
                                }
                            }, 3000);

                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Log.d("onFailure 발동","Connection Error");
                            AlertDialog.Builder ad = new AlertDialog.Builder(SignUp.this);
                            ad.setTitle("에러2");
                            ad.setMessage(t.getMessage());
                            ad.show();
                        }
                    });

                }

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

    public static String isValidPassword(String pw){
        // 최소 8자, 최대 20자 상수 선언
        final int MIN = 8;
        final int MAX = 25;

        // 영어, 숫자, 특수문자 포함한 MIN to MAX 글자 정규식
        final String REGEX =
                "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[\\W]).{" + MIN + "," + MAX + "})$";
        // 3자리 연속 문자 정규식
        final String SAMEPT = "(\\w)\\1\\1";
        // 공백 문자 정규식
        final String BLANKPT = "(\\s)";

        // 정규식 검사객체
        Matcher matcher;

        // 공백 체크
        if (pw == null || "".equals(pw)) {
            return "패스워드는 반드시 입력해야합니다.";
        }

        // ASCII 문자 비교를 위한 UpperCase
        String tmpPw = pw.toUpperCase();
        // 문자열 길이
        int strLen = tmpPw.length();

        // 글자 길이 체크
        if (strLen > 15 || strLen < 8) {
            return "비밀번호는 8자 이상 15자 이하여야 합니다.(비밀번호 길이: " + strLen + ")";
        }

        // 공백 체크
        matcher = Pattern.compile(BLANKPT).matcher(tmpPw);
        if (matcher.find()) {
            return "비밀번호에 공백을 입력할 수 없습니다.";
        }

        // 비밀번호 정규식 체크
        matcher = Pattern.compile(REGEX).matcher(tmpPw);
        if (!matcher.find()) {
            return "소문자, 대문자, 숫자, 특수문자를 조합하여 비밀번호를 생성해야합니다.";
        }

        // 동일한 문자 3개 이상 체크
        matcher = Pattern.compile(SAMEPT).matcher(tmpPw);
        if (matcher.find()) {
            return "패스워드에 동일한 문자를 3개 이상 입력하였습니다.";
        }

        // ASCII Char를 담을 배열 선언
        int[] tmpArray = new int[strLen];

        // Make Array
        for (int i = 0; i < strLen; i++) {
            tmpArray[i] = tmpPw.charAt(i);
        }

        // Validation Array
        for (int i = 0; i < strLen - 2; i++) {
            // 첫 글자 A-Z / 0-9
            if ((tmpArray[i] > 47 && tmpArray[i + 2] < 58)
                    || (tmpArray[i] > 64 && tmpArray[i + 2] < 91)) {
                        // 배열의 연속된 수 검사
                        // 3번째 글자 - 2번째 글자 = 1, 3번째 글자 - 1번째 글자 = 2
                        if (Math.abs(tmpArray[i + 2] - tmpArray[i + 1]) == 1
                            && Math.abs(tmpArray[i + 2] - tmpArray[i]) == 2) {
                        char c1 = (char) tmpArray[i];
                        char c2 = (char) tmpArray[i + 1];
                        char c3 = (char) tmpArray[i + 2];
                        return "비밀번호에 연속된 패턴이 존재합니다. 해당 정보 : \"" + c1 + c2 + c3 + "\"";
                        }
            }
        }

        return "All Pass";

    }

    public static Boolean isValidEmail(String ipt_email){
        String regx = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(ipt_email);
        return matcher.matches();
    }

}