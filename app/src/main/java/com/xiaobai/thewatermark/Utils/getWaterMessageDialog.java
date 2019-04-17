package com.xiaobai.thewatermark.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaobai.thewatermark.Activit.MainActivity;
import com.xiaobai.thewatermark.R;
import com.xiaobai.thewatermark.WeChat.CreatTemporaryPhoto;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.extras.Base64;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.xiaobai.thewatermark.WeChat.PhotoPreviewActivity.EXTRA_RESULT;
import static org.java_websocket.WebSocket.READYSTATE.NOT_YET_CONNECTED;


public class getWaterMessageDialog{


    private int count ;
    private MainActivity.GridAdapter gridAdapter1 ;    //GridView适配器
    /*获取插入水印信息界面 */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public   getWaterMessageDialog(final Activity context, final ImageView imageView, final boolean isSingePhoto, final ArrayList<String> photoArrayList1, final String imageFormat, final GridView gridView) {
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.mdialog, null);  //自定义Diglog
        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        final AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.mdialog);

        if(photoArrayList1!=null){
            count = photoArrayList1.size();

        }


        final ArrayList<HashMap<String,Object>> maps = new ArrayList<>();         //数据源
        final ArrayList<String> RTPhoto = new ArrayList<>();


        final TextView tv_showGPS_x = (TextView) dialogView.findViewById(R.id.tv_showGPS_x);      //获取经度
        final TextView tv_showGps_y = (TextView) dialogView.findViewById(R.id.tv_showGPS_y);      //获取纬度
        final TextView tv_address = (TextView) dialogView.findViewById(R.id.tv_address);            //获取地址信息
        final TextView tv_showIMEI = (TextView) dialogView.findViewById(R.id.tv_showIMEI);        //获取IMEI
        final TextView tv_showIMSI = (TextView) dialogView.findViewById(R.id.tv_showIMSI);        //获取IMSI
        final EditText et_show_my_message = (EditText) dialogView.findViewById(R.id.et_my_message); //自定义信息

        final String s_testGPS = tv_showGPS_x.getText().toString();     //用于检测是否获取了GPS信息
        final String s_testIMEI = tv_showIMEI.getText().toString();           //用于检测是否获取了IMEI信息
        final String s_my_message = et_show_my_message.getText().toString();    //获取自定义信息

        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        assert telManager != null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        final String imei = telManager.getImei();             //获取IMEI信息
        final String imsi = telManager.getSubscriberId();     //获取IMSI信息

        final StringBuilder builder = new StringBuilder();    //保存出错的图片
        builder.append("\n");




        Draft_6455 draft_6455 = new Draft_6455();
        URI uri = URI.create("ws://47.106.158.244/copyright/batchAddDctWater");

        final String[] photoPaths;    //存放图片集合

        final StringBuilder finalBuilder = null;
        final WebSocketClient webSocketClient = new WebSocketClient(uri, draft_6455) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "run() returned: " + "连接到服务器");
            }

            @Override
            public void onMessage(String message) {
                try {
                    JSONObject jsonObject = new JSONObject(message);

                    boolean success = jsonObject.getBoolean("success");
                    if (success){
                        String  photoMessage = String.valueOf(jsonObject.get("message"));
                        String[] SLPhotoMessage = photoMessage.split("base64,");
                        String base64Photo = SLPhotoMessage[1];
                        Bitmap new_photo = BitmapToBytesUtil.base64ToBitmap(base64Photo);           //转换成图片
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("ItemImage", new_photo);			//传入bitmap资源
                        Log.i("map",map.toString());
                        maps.add(map);
                        LogUtil.e("maps",maps.toString());
                        CreatTemporaryPhoto CreatTemporaryPhoto = new CreatTemporaryPhoto();
                        String photoPath;       //获取临时图片位置
                        photoPath = CreatTemporaryPhoto.creatTemporaryPhoto(new_photo,context,"image/jpeg");
                        RTPhoto.add(photoPath);
                    }else {

                        RTPhoto.add(MainActivity.imagePaths.get(RTPhoto.size()));
                        Log.i("error","第"+RTPhoto.size()+"张图片不能重复嵌入水印");
                        builder.append("第"+RTPhoto.size()+"张图片不能重复嵌入水印\n");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("onMessage ", "onMessage " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

                Log.d(TAG, "onClose() returned: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "onError() returned: " + ex);

            }
        };
        if (webSocketClient.getReadyState()==NOT_YET_CONNECTED){
            webSocketClient.connect();
        }



        Button bt_getGPS = (Button) dialogView.findViewById(R.id.bt_getGPS);        //获取经纬度按钮
        bt_getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.Latitude.equals("") || MainActivity.Longitude.equals("")){
                    tv_showGPS_x.setText("无法获取经纬度，请重试");
                }else{
                    tv_showGPS_x.setText(MainActivity.Latitude);
                    tv_showGps_y.setText(MainActivity.Longitude);
                    tv_address.setText(MainActivity.Address);
                }

            }
        });

        /*获取IMEI信息按钮*/
        Button bt_getIMEI = (Button) dialogView.findViewById(R.id.bt_getIMEI);
        bt_getIMEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_showIMEI.setText(imei);
            }
        });

        final Button bt_getIMSI = (Button) dialogView.findViewById(R.id.bt_getIMSI);
        bt_getIMSI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_showIMSI.setText(imsi);
            }
        });

        /*确定嵌入水印按钮*/
        Button bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_showGPS_x.getText().toString().equals(s_testGPS)) {
                    Toast.makeText(context, "请先获得您的GPS信息", Toast.LENGTH_SHORT).show();
                } else if (tv_showIMEI.getText().toString().equals(s_testIMEI)) {
                    Toast.makeText(context, "请先获得您的IMEI信息", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences sp = context.getSharedPreferences("loginInfo", MODE_PRIVATE);
                    final String loginUserName = sp.getString("loginUserName", "未登录");
                    String InfoMessage = et_show_my_message.getText().toString();
                    if (InfoMessage.equals("")){
                        InfoMessage = "null";
                    }
                    final String data = MainActivity.Address+"\n"+"经度：" + tv_showGPS_x.getText().toString() + "\n纬度:" + tv_showGps_y.getText().toString() + "\nIMEI:" + imei + "\nIMSI:" + imsi + "\n自定义信息:" +InfoMessage ;
                    /*单图操作*/
                    if (isSingePhoto){
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("正在插入水印...");
                        progressDialog.setCancelable(true);
                        progressDialog.show();

                        /*发送图片至后端*/
                        Bitmap to_insert =((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        String orgin = "data:"+imageFormat+";base64," + String.valueOf(Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(to_insert), Base64.DEFAULT));
                        RequestParams params = new RequestParams();
                        String url = "http://47.106.158.244/copyright/image/addDctWater";
                        params.put("base64data", orgin);
                        params.put("username", loginUserName);
                        params.put("str", data);
                        LogUtil.e("TAG",params.toString());
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setResponseTimeout(30000);
                        client.setConnectTimeout(30000);
                        client.post(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                                /*获取返回信息*/
                                String jason = new String(responseBody);
                                try {
                                    JSONObject response = new JSONObject(jason);
                                    boolean value = response.getBoolean("success");       //返回结果
                                    String error =  response.optString("error");    //返回错误结果
                                    Log.i("value", String.valueOf(value));          //返回成功信息
                                    if (value) {
                                        Toast.makeText(context, "嵌入水印成功", Toast.LENGTH_LONG).show();
                                        JSONObject data = response.getJSONObject("data");
                                        String get_message = data.optString("message");
                                        String key = data.optString("keys");
                                        Log.i("key",key);
                                        String[] newPhoto = get_message.split("base64,");     //对接收的图片数据切割
                                        String n_photo = newPhoto[1];           //获取图片部分
                                        Bitmap new_photo = BitmapToBytesUtil.base64ToBitmap(n_photo);           //转换成图片
                                        imageView.setImageBitmap(new_photo);
                                        dlg.dismiss();
                                        progressDialog.dismiss();

                                        /*显示水印密码*/
                                        final View dialogShowkeysView = LayoutInflater.from(context).inflate(R.layout.show_water_password, null);  //自定义Diglog
                                        customizeDialog.setCancelable(true);
                                        customizeDialog.setView(dialogShowkeysView);
                                        final AlertDialog show_keys_dlg = customizeDialog.create();
                                        show_keys_dlg.setContentView(R.layout.show_water_password);
                                        final TextView show_password = dialogShowkeysView.findViewById(R.id.tv_show_password);
                                        show_password.setText(key);

                                        dialogShowkeysView.findViewById(R.id.bt_copy).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                /*一键复制*/
                                                // 从API11开始android推荐使用android.content.ClipboardManager
                                                // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                // 将文本内容放到系统剪贴板里。
                                                cm.setText(show_password.getText());
                                                Toast.makeText(context, "复制密码成功", Toast.LENGTH_LONG).show();
                                                show_keys_dlg.dismiss();

                                            }
                                        });
                                        dialogShowkeysView.findViewById(R.id.bt_password_ok).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                show_keys_dlg.dismiss();
                                            }
                                        });
                                        show_keys_dlg.show();
                                    } else{
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
                                        Log.i("response", "------------------------>" + error);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "服务器请求失败", Toast.LENGTH_LONG).show();
                                Log.i("test------------------", String.valueOf(statusCode));
                            }
                        });



                    }else {


                        final ProgressDialog dialog = new ProgressDialog(context);
                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
                        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                        dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
/*                        dialog.setTitle("提示");*/
                        dialog.setMax(count);
                        dialog.setMessage("正在传输图片");
                        dialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                maps.clear();
                                /*多图操作*/
                                String webSocketSendMessage = "默认数据";

                                Log.i("TAG",String.valueOf(webSocketClient.getReadyState()));

                                for (int i = 0;i<photoArrayList1.size();i++){
                                    webSocketSendMessage = photoArrayList1.get(i);

                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("username",loginUserName);
                                        jsonObject.put("str",data);
                                        jsonObject.put("base64data",webSocketSendMessage);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    webSocketClient.send(jsonObject.toString());
                                }


                                for (;RTPhoto.size()!=count;){

                                    dialog.setProgress(RTPhoto.size());
                                    //正在上传第RTPhoto.size张图片
                                }

                                MainActivity.imagePaths = RTPhoto;

                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_RESULT, RTPhoto);
                                context.setResult(RESULT_OK, intent);


                                Log.i("imagePaths", String.valueOf(MainActivity.imagePaths));
                                dlg.dismiss();

                                LogUtil.e("TAG",maps.toString());
                                LogUtil.e("TAG","显示图片到gridview");

                                dialog.dismiss();

                                Looper.prepare();
                                Toast.makeText(context,"嵌入水印完成",Toast.LENGTH_LONG).show();

                                /*显示已经嵌入水印的图片*/
                                final View dialogShowPhotoErrorView = LayoutInflater.from(context).inflate(R.layout.photo_is_insert_water, null);  //自定义Diglog
                                customizeDialog.setCancelable(true);
                                customizeDialog.setView(dialogShowPhotoErrorView);
                                final AlertDialog show_error_dlg = customizeDialog.create();
                                show_error_dlg.setContentView(R.layout.photo_is_insert_water);
                                final TextView tv_show_photo_error = dialogShowPhotoErrorView.findViewById(R.id.tv_show_photo_error);
                                tv_show_photo_error.setText(builder);



                                Log.i("finalBuilder",builder.toString());

                                dialogShowPhotoErrorView.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        show_error_dlg.dismiss();

                                    }
                                });


                                show_error_dlg.show();
                                if (builder.toString().equals("\n"))show_error_dlg.dismiss();

                                Looper.loop();
                            }

                        }).start();


                    }


                }
            }


        });





        /*取消嵌入水印按钮*/
        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        dlg.show();
    }






}

