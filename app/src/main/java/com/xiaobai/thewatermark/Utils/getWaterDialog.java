package com.xiaobai.thewatermark.Utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class getWaterDialog{

    private Bitmap bitmap;


    public getWaterDialog(final Context context, Bitmap bitmap1, final ImageView mImageView, final String getImageFormat) {

        final String TAG = "TAG" + context;
        this.bitmap = bitmap1;
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.get_water_dialog, null);  //自定义Diglog

        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        final AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.get_water_dialog);
        final EditText et_water_password = (EditText) dialogView.findViewById(R.id.et_water_password);
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        final String loginUserName = sp.getString("loginUserName", "未登录");


        /*提取水印按钮*/
        Button water_bt_ok = (Button) dialogView.findViewById(R.id.water_bt_ok);
        water_bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在提取水印...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                /*发送图片至后端*/

                bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
                String s_water_password = et_water_password.getText().toString().trim();    //获取水印密码

                /*获取imageView图片的格式*/
                String[] theImageFormat = getImageFormat.split("image/");
                String format = theImageFormat[1];
                Log.i("TEST_getImageFormat",format);

                String[] allowedContentTypes = new String[] { "image/png", "image/jpeg" };
                String photo = "data:image/"+format+";base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(bitmap), Base64.DEFAULT));
                String uri = "http://idea.welikedian.com/copyright/image/getDctWater";
                RequestParams params = new RequestParams();
                params.put("waterPassword",s_water_password);
                params.put("username",loginUserName);
                params.put("base64data",photo);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setResponseTimeout(30000);
                client.post(uri, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                        /*显示图片*/
                        final AlertDialog dlg = customizeDialog.create();
                        dlg.setContentView(R.layout.watchstartphoto);
                        /*显示水印隐藏框*/
                        final ImageView show_water_photo = (ImageView) dialogView.findViewById(R.id.water_photo);
                        dialogView.findViewById(R.id.water_photo).setVisibility(View.VISIBLE);

                        Button download_water = (Button) dialogView.findViewById(R.id.download_water);

                        Log.i("TEST_statusCode", String.valueOf(statusCode));

                        String value = new String(responseBody);        //用于判断是否成功提取水印

                        try {

                            JSONObject jsonObject = new JSONObject(value);
                            boolean response = jsonObject.getBoolean("success");
                            if (!response) {
                                String error = jsonObject.getString("error");
                                Log.i(TAG, "error:"+error);
                                Log.i(TAG,error.substring(0,4));
                                if (error.substring(0,4).equals("data")){
                                    String[] the_watermake = error.split(";base64,");     //对接收的图片数据切割
                                    String water_photo = the_watermake[1];           //获取图片部分
                                    final String photoFormat = the_watermake[0];

                                    Log.i("TEST_photoFormat", photoFormat);
                                    Log.i("TEST3", water_photo + "-----------------------------------------");             //切割base64
                                    Bitmap bitmapUri = BitmapToBytesUtil.base64ToBitmap(water_photo);

                                    Log.i(TAG,bitmapUri.toString());
                                    /*隐藏之前的控件*/
                                    dialogView.findViewById(R.id.tv_waterPasswordTip).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.et_water_password).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.water_bt_ok).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.water_bt_cancel).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.water_photo).setVisibility(View.VISIBLE);
                                    show_water_photo.setImageBitmap(bitmapUri);

                                    /*显示保存图片到本地按钮*/
                                    download_water.setVisibility(View.VISIBLE);
                                    download_water.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Bitmap bitmap2 = ((BitmapDrawable) ((ImageView) show_water_photo).getDrawable()).getBitmap();
                                            new saveImage(bitmap2,context, photoFormat);         //把bitmap2对应的图片保存图片至本地
                                            Log.i(TAG,photoFormat);
                                            Log.i(TAG,bitmap2.toString());
                                            Log.i(TAG,context.toString());
                                            Toast.makeText(context, "保存图片成功", Toast.LENGTH_SHORT).show();
                                            dlg.dismiss();
                                        }
                                    });

                                }else {
                                    Toast.makeText(context,error,Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            } else {
                                String error = jsonObject.getString("error");
                                JSONObject water = jsonObject.getJSONObject("data");
                                String s_water_make = water.getString("message");
                                Log.e("TAG",s_water_make);
                                String[] the_watermake = s_water_make.split(";base64,");     //对接收的图片数据切割
                                    String water_photo = the_watermake[1];           //获取图片部分
                                    final String photoFormat = the_watermake[0];

                                    Log.i("TEST_photoFormat", photoFormat);
                                    Log.i("TEST3", water_photo + "-----------------------------------------");             //切割base64
                                    Bitmap bm_waterphoto = BitmapToBytesUtil.base64ToBitmap(water_photo);           //转换成图片

                                    /*隐藏之前的控件*/
                                    dialogView.findViewById(R.id.tv_waterPasswordTip).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.et_water_password).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.water_bt_ok).setVisibility(View.GONE);
                                    dialogView.findViewById(R.id.water_bt_cancel).setVisibility(View.GONE);
                                    show_water_photo.setImageBitmap(bm_waterphoto);
                                    /*显示保存图片到本地按钮*/
                                    download_water.setVisibility(View.VISIBLE);
                                    download_water.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ProgressDialog progressDialog = new ProgressDialog(context);
                                            progressDialog.setMessage("正在保存...");
                                            progressDialog.setCancelable(true);
                                            progressDialog.show();
                                            Bitmap bitmap2 = ((BitmapDrawable) ((ImageView) show_water_photo).getDrawable()).getBitmap();    //获取图片中的uri保存至bitmap2
                                            new saveImage(bitmap2, context, photoFormat);         //把bitmap2对应的图片保存图片至本地
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "保存图片成功", Toast.LENGTH_SHORT).show();
                                            dlg.dismiss();
                                        }
                                    });

                                    Toast.makeText(context, "提取水印成功", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();

                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();

                        Log.i("TAG",error.toString());
                        Log.i("TAG", String.valueOf(statusCode));
                    }
                });

            }

        });

        /*取消提取水印按钮*/
        Button water_bt_cancel = (Button) dialogView.findViewById(R.id.water_bt_cancel);
        water_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        dlg.show();
    }

}