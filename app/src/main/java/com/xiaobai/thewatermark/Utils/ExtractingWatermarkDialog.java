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
import com.loopj.android.http.ResponseHandlerInterface;
import com.xiaobai.thewatermark.Activit.MainActivity;
import com.xiaobai.thewatermark.R;
import com.xiaobai.thewatermark.WeChat.Image;

import org.json.JSONArray;
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
        final EditText et_photoUri = dialogView.findViewById(R.id.et_waterUri);           //图片标识输入框
        et_photoUri.setTransformationMethod(new TransFromaToA(true));

        /*四张标识图片框*/
        final ImageView imageViewOne = dialogView.findViewById(R.id.iv_imageOne);
        final ImageView imageViewTwo = dialogView.findViewById(R.id.iv_imageTwo);
        final ImageView imageViewThree = dialogView.findViewById(R.id.iv_imageThree);
        final ImageView imageViewFour = dialogView.findViewById(R.id.iv_imageFour);

        final ImageView show_water_photo = (ImageView) dialogView.findViewById(R.id.water_photo);   //初始化显示水印图片控件
        final ImageView showUriPhoto = dialogView.findViewById(R.id.iv_waterUri);     //初始化显示图片URI控件
        final Button download_water = (Button) dialogView.findViewById(R.id.download_water);      //初始化保存水印图片按钮
        SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
        final String loginUserName = sp.getString("loginUserName", "未登录");




        AsyncHttpClient UriClient = new AsyncHttpClient();
        RequestParams photoParams = new RequestParams();
        bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        /*获取imageView图片的格式*/
        String[] theImageFormat = getImageFormat.split("image/");
        final String format = theImageFormat[1];
        String photo = "data:image/"+format+";base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(bitmap), Base64.DEFAULT));
        String getUri = "http://idea.welikedian.com/copyright/image/getDctWater";
        photoParams.put("base64data",photo);
        UriClient.post(getUri, photoParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                Log.i("TAG_response",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success){
                        JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                        JSONArray jsonArray = dataJsonObject.getJSONArray("image");
                        String photo1 = String.valueOf(jsonArray.get(0)).split("data:image/"+format+";base64,")[1];
                        String photo2 = String.valueOf(jsonArray.get(1)).split("data:image/"+format+";base64,")[1];
                        String photo3 = String.valueOf(jsonArray.get(2)).split("data:image/"+format+";base64,")[1];
                        String photo4 = String.valueOf(jsonArray.get(3)).split("data:image/"+format+";base64,")[1];
                        Bitmap bm_waterphoto1 = BitmapToBytesUtil.base64ToBitmap(photo1);
                        Bitmap bm_waterphoto2 = BitmapToBytesUtil.base64ToBitmap(photo2);
                        Bitmap bm_waterphoto3 = BitmapToBytesUtil.base64ToBitmap(photo3);
                        Bitmap bm_waterphoto4 = BitmapToBytesUtil.base64ToBitmap(photo4);
                        imageViewOne.setImageBitmap(bm_waterphoto1);
                        imageViewTwo.setImageBitmap(bm_waterphoto2);
                        imageViewThree.setImageBitmap(bm_waterphoto3);
                        imageViewFour.setImageBitmap(bm_waterphoto4);
                    }else Toast.makeText(context,jsonObject.getString("error"),Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("TAG_statusCode", String.valueOf(statusCode));
            }
        });





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

/*                bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();*/
                String s_water_password = et_water_password.getText().toString().trim();    //获取水印密码
                String s_waterUri = et_photoUri.getText().toString().trim();            //获取图片标识


                String uri = "http://idea.welikedian.com/copyright/image/getDctWaterById";
                final RequestParams params = new RequestParams();
                params.put("waterPassword",s_water_password);
                params.put("imageId",s_waterUri);
                Log.i("TAG_param",params.toString());
/*                params.put("base64data",photo);*/
                AsyncHttpClient client = new AsyncHttpClient();
                client.setResponseTimeout(30000);
                client.setConnectTimeout(30000);
                client.post(uri, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {


                        String response = new String(responseBody);
                        try {
                            JSONObject photoJS = new JSONObject(response);

                            boolean success = photoJS.getBoolean("success");
                            if (success){

                                JSONObject dataObject = photoJS.getJSONObject("data");
                                Bitmap waterMarkPhoto =
                                        BitmapToBytesUtil.base64ToBitmap( dataObject.getString("message").split(";base64,")[1]);
                                dialogView.findViewById(R.id.ll_getWaterMessage).setVisibility(View.GONE);      //隐藏旧窗口
                                dialogView.findViewById(R.id.ll_holdShowMessage).setVisibility(View.VISIBLE);   //显示新窗口
                                show_water_photo.setImageBitmap(waterMarkPhoto);
                                progressDialog.dismiss();


                                download_water.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Bitmap bitmap2 = ((BitmapDrawable) ((ImageView) show_water_photo).getDrawable()).getBitmap();    //获取图片中的uri保存至bitmap2
                                        new saveImage(bitmap2, context, getImageFormat);         //把bitmap2对应的图片保存图片至本地
                                        Toast.makeText(context, "保存图片成功", Toast.LENGTH_SHORT).show();
                                        dlg.dismiss();
                                    }
                                });

                            }else
                                Toast.makeText(context,photoJS.getString("error"),Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("TAG_response",response);
                        progressDialog.dismiss();


                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                        progressDialog.dismiss();
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