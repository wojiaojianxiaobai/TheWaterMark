package com.xiaobai.thewatermark.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaobai.thewatermark.Activit.MainActivity;
import com.xiaobai.thewatermark.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.extras.Base64;

/*置乱按钮*/
public class ArnoldDialog{
    public ArnoldDialog(final Context context, String loginUserName, final ImageView mImageView,Bitmap bitmap1){
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.show_water_password, null);  //自定义Diglog
        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        final AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.show_water_password);
        final TextView show_password = dialogView.findViewById(R.id.tv_show_password);

        if (mImageView != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在置乱图片...");
            progressDialog.setCancelable(true);
            progressDialog.show();

            Bitmap get_photo = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
            bitmap1 = get_photo;    //保留原图
            String base64_photo = "data:image/jpeg;base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(get_photo), Base64.DEFAULT));
            String url = "http://47.106.158.244/copyright/image/arnold";
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("base64data",base64_photo);
            params.put("username",loginUserName);
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                    String s_arnold = new String(responseBody);
                    try {
                        JSONObject j_arnold = new JSONObject(s_arnold);
                        JSONObject data = j_arnold.getJSONObject("data");
                        boolean success = j_arnold.getBoolean("success");
                        if (success){
                            String get_message = data.optString("message");
                            String key = data.optString("keys");
                            Log.i("TEST5",key+"--------------------------------------");
                            String[] newPhoto = get_message.split("base64,");     //对接收的图片数据切割
                            String n_photo = newPhoto[1];           //获取图片部分
                            Bitmap new_photo = BitmapToBytesUtil.base64ToBitmap(n_photo);           //转换成图片
                            mImageView.setImageBitmap(new_photo);

                            Toast.makeText(context,"图片置乱成功",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            show_password.setText(key);
                            dlg.show();

                            dialogView.findViewById(R.id.bt_password_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dlg.dismiss();
                                }
                            });




                        }else {
                            String error = j_arnold.optString("error");
                            Toast.makeText(context,""+error,Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                    Toast.makeText(context,"服务器请求失败",Toast.LENGTH_SHORT).show();
                }
            });

        } else Toast.makeText(context, "请先插入图片", Toast.LENGTH_SHORT).show();

    }

}
