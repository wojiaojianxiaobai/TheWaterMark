package com.xiaobai.thewatermark.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaobai.thewatermark.Activit.MainActivity;
import com.xiaobai.thewatermark.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.extras.Base64;

public class recovery {
    public recovery(final ImageView mImageView, final Context context, final String loginUserName){

        if (mImageView != null) {

            final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(context);
            final View dialogView = LayoutInflater.from(context).inflate(R.layout.inverse_arnold, null);  //自定义Diglog
            customizeDialog.setCancelable(true);
            customizeDialog.setView(dialogView);
            final AlertDialog invese_dlg = customizeDialog.create();
            invese_dlg.setContentView(R.layout.inverse_arnold);
            final EditText et_water_password = (EditText) dialogView.findViewById(R.id.et_inverse_password);



            /*还原图片确定按钮*/
            Button water_bt_ok = (Button) dialogView.findViewById(R.id.bt_inverse_ok);
            water_bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("正在还原图片...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    /*发送图片至后端*/


                    Bitmap get_photo = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                    String inverse_password = et_water_password.getText().toString();    //获取水印密码
                    Log.i("TEST1",get_photo.toString()+"");
                    String base64_photo = "data:image/jpeg;base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(get_photo), Base64.DEFAULT));
                    String url = "http://47.106.158.244/copyright/image/inverseArnold";
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("base64data",base64_photo);
                    Log.i("TEST_base64_photo:",base64_photo);
                    params.put("username",loginUserName);
                    Log.i("TEST_username:",loginUserName);
                    params.put("arnoldPassword",inverse_password);
                    Log.i("TEST_inverse_password:",inverse_password);
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                            String s_arnold = new String(responseBody);
                            try {
                                JSONObject j_arnold = new JSONObject(s_arnold);
                                JSONObject data = j_arnold.getJSONObject("data");
                                boolean success = j_arnold.getBoolean("success");
                                Log.i("TEST_success:", String.valueOf(success));
                                if (success){
                                    String get_message = data.optString("message");
                                    Log.i("TEST_get_message:",get_message);
                                    String[] newPhoto = get_message.split("base64,");     //对接收的图片数据切割
                                    String n_photo = newPhoto[1];           //获取图片部分
                                    Log.i("TEST_n_photo:",n_photo);
                                    Bitmap new_photo = BitmapToBytesUtil.base64ToBitmap(n_photo);           //转换成图片
                                    mImageView.setImageBitmap(new_photo);

                                    progressDialog.dismiss();
                                    invese_dlg.dismiss();
                                    Toast.makeText(context,"图片还原成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    String error = j_arnold.optString("error");
                                    Log.i("TEST_error:",error);
                                    Toast.makeText(context,""+error,Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("TEST_statuscode:", String.valueOf(statusCode));
                            Toast.makeText(context,"连接服务器失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            });

            /*取消提取水印按钮*/
            Button water_bt_cancel = (Button) dialogView.findViewById(R.id.bt_inverse_cancel);
            water_bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invese_dlg.dismiss();
                }
            });
            invese_dlg.show();
        }
        else Toast.makeText(context, "请先插入图片", Toast.LENGTH_SHORT).show();
    }
}
