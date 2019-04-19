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
import com.xiaobai.thewatermark.WeChat.Image;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;

import static android.content.Context.MODE_PRIVATE;

public class ExtractingWatermarkDialog {

    private Bitmap bitmap;


    public ExtractingWatermarkDialog(final Context context, Bitmap bitmap1, final ImageView mImageView, final String getImageFormat) {

        final String TAG = "TAG" + context;
        this.bitmap = bitmap1;
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.get_water_dialog, null);  //自定义Diglog

        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        final AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.get_water_dialog);
        final EditText et_water_password = (EditText) dialogView.findViewById(R.id.et_water_password);  //水印密码输入框
        final EditText et_photoUri = dialogView.findViewById(R.id.et_photoUri);           //图片标识输入框
        final ImageView show_water_photo = (ImageView) dialogView.findViewById(R.id.water_photo);   //初始化显示水印图片控件
        final ImageView showUriPhoto = dialogView.findViewById(R.id.iv_waterUri);     //初始化显示图片URI控件
        final Button download_water = (Button) dialogView.findViewById(R.id.download_water);      //初始化保存水印图片按钮
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

                String photo = "data:image/"+format+";base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(bitmap), Base64.DEFAULT));
                String uri = "http://idea.welikedian.com/copyright/image/getDctWater";
                final RequestParams params = new RequestParams();
                params.put("waterPassword",s_water_password);
                params.put("username",loginUserName);
                params.put("base64data",photo);
                AsyncHttpClient client = new AsyncHttpClient();
                client.setResponseTimeout(30000);
                client.setConnectTimeout(30000);
                client.post(uri, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {




                        /*显示图片*/
                        final AlertDialog dlg = customizeDialog.create();
                        dlg.setContentView(R.layout.watchstartphoto);
                        /*显示水印隐藏框*/



                        Log.i("TEST_statusCode", String.valueOf(statusCode));

                        String value = new String(responseBody);        //用于判断是否成功提取水印

                        try {

                            JSONObject jsonObject = new JSONObject(value);
                            boolean response = jsonObject.getBoolean("success");
                            if (!response) {
                                String error = jsonObject.getString("error");

                                /*判断存在标识*/
                                if (error.substring(0,4).equals("data")){
                                    String[] the_watermake = error.split(";base64,");     //对接收的图片数据切割
                                    String water_photo = the_watermake[1];           //获取图片部分
                                    final String photoFormat = the_watermake[0];

                                    Bitmap bitmapUri = BitmapToBytesUtil.base64ToBitmap(water_photo);

                                    Log.i(TAG,bitmapUri.toString());
                                    /*隐藏之前的控件*/
                                    dialogView.findViewById(R.id.ll_getWaterMessage).setVisibility(View.GONE);      //隐藏旧窗口
                                    dialogView.findViewById(R.id.ll_uriWindow).setVisibility(View.VISIBLE);;        //显示标识图片窗口
                                    showUriPhoto.setImageBitmap(bitmapUri);     //显示水印URI

                                    dialogView.findViewById(R.id.bt_retry_ok).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            String uriGetDctWaterByID = "http://idea.welikedian.com/copyright/image/getDctWaterById";
                                            RequestParams uriParams = new RequestParams();
                                            uriParams.put("imageId",et_photoUri.getText().toString().trim());
                                            uriParams.put("waterPassword",et_water_password.getText().toString().trim());
                                            uriParams.put("username",loginUserName);

                                            Log.i(TAG+"params",uriParams.toString());
                                            AsyncHttpClient uriClient = new AsyncHttpClient();
                                            uriClient.setResponseTimeout(30000);
                                            uriClient.setConnectTimeout(30000);
                                            uriClient.post(uriGetDctWaterByID, uriParams, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                                                    String StringResponseBody = new String(responseBody);
                                                    try {
                                                        JSONObject JSResponseBody = new JSONObject(String.valueOf(StringResponseBody));

                                                        boolean success = JSResponseBody.getBoolean("success");
                                                        if (success){

                                                            JSONObject dataJsonObject = JSResponseBody.getJSONObject("data");
                                                            String message = dataJsonObject.getString("message");
                                                            String[] listMessage = message.split(";base64,");
                                                            String format = listMessage[0];
                                                            String waterPhoto = listMessage[1];

                                                            Bitmap waterBitmap = BitmapToBytesUtil.base64ToBitmap(waterPhoto);

                                                            dialogView.findViewById(R.id.ll_uriWindow).setVisibility(View.GONE);
                                                            dialogView.findViewById(R.id.ll_holdShowMessage).setVisibility(View.VISIBLE);
                                                            show_water_photo.setImageBitmap(waterBitmap);

                                                            /*显示保存图片到本地按钮*/
                                                            download_water.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    Bitmap bitmap2 = ((BitmapDrawable) ((ImageView) show_water_photo).getDrawable()).getBitmap();
                                                                    new saveImage(bitmap2,context, photoFormat);         //把bitmap2对应的图片保存图片至本地
                                                                    Toast.makeText(context, "保存图片成功", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    Log.i(TAG+"status",statusCode+"");
                                                    String value = new String(responseBody);        //用于判断是否成功提取水印
                                                    LogUtil.e(TAG+"responseBody",value);
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                    Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });

                                    dialogView.findViewById(R.id.bt_retry_cancel).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
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

                                    Bitmap bm_waterphoto = BitmapToBytesUtil.base64ToBitmap(water_photo);           //转换成图片

                                    /*隐藏之前的控件*/

                                    dialogView.findViewById(R.id.ll_getWaterMessage).setVisibility(View.GONE);      //隐藏旧窗口
                                    dialogView.findViewById(R.id.ll_holdShowMessage).setVisibility(View.VISIBLE);  //显示图片和下载按钮
                                    show_water_photo.setImageBitmap(bm_waterphoto);
                                    /*显示保存图片到本地按钮*/
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