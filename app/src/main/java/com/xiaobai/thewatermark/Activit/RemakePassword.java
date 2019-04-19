package com.xiaobai.thewatermark.Activit;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaobai.thewatermark.R;
import com.xiaobai.thewatermark.Utils.MD5Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RemakePassword extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remake_password);
        init();
    }

    private void init(){

        /*标题*/
        TextView tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("修改密码");

        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemakePassword.this.finish();
            }
        });


        /*获取用户名*/
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        final String loginUserName = sp.getString("loginUserName", "未登录");
        TextView tv_loginUserName = findViewById(R.id.loginUserName);
        tv_loginUserName.setText(loginUserName);


        /*获取控件信息*/
        final EditText et_password = findViewById(R.id.et_user_name);
        final EditText et_newPsw = findViewById(R.id.et_psw);
        final EditText et_newPsw_again = findViewById(R.id.et_psw_again);



        /*修改密码按钮*/
        final Button btn_remake_password = findViewById(R.id.btn_remake_password);
        btn_remake_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String s_newPsw = et_newPsw.getText().toString().trim();
                String s_password = et_password.getText().toString().trim();
                String s_newPsw_again = et_newPsw_again.getText().toString().trim();

                String md5Password= MD5Utils.md5(s_password);
                String md5NewPsw = MD5Utils.md5(s_newPsw);

                if (!s_newPsw.equals(s_newPsw_again)){
                    Toast.makeText(RemakePassword.this,"输入的两次密码不一致",Toast.LENGTH_SHORT).show();
                }else {

                /*  发送请求到服务器*/
                    String url = "http://47.106.158.244/copyright/user/updatePwd";
                    RequestParams params = new RequestParams();
                    params.put("username",loginUserName);
                    params.put("oldPassword",md5Password);
                    params.put("newPassword",md5NewPsw);
                    AsyncHttpClient client= new AsyncHttpClient();
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            if (statusCode==200){
                                String jason = new String(responseBody);
                                try {
                                    JSONObject response = new JSONObject(jason);
                                    boolean value = response.getBoolean("success");
                                    if (value){
                                        Toast.makeText(RemakePassword.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                                        RemakePassword.this.finish();
                                    }else {
                                        String error = response.getString("error");
                                        Toast.makeText(RemakePassword.this,""+error,Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else
                                Toast.makeText(RemakePassword.this,"请求服务器失败",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            Log.i("TAG_repasswordStatus", String.valueOf(statusCode));
                            Toast.makeText(RemakePassword.this,"连接服务器失败",Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            }
        });



    }
}
