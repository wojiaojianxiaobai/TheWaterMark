package com.xiaobai.thewatermark.Activit;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaobai.thewatermark.R;
import com.xiaobai.thewatermark.Utils.BitmapCache;
import com.xiaobai.thewatermark.Utils.photoListCell;
import com.xiaobai.thewatermark.Utils.saveImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AlbumActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lv;
    private List<photoListCell> show_photo_list = new ArrayList<photoListCell>();
    public ArrayList<Map<String, Object>> list= new ArrayList<>();     //存放数组列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);


        /*获取用户名*/
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        final String loginUserName = sp.getString("loginUserName", "未登录");

        //从main_title_bar.xml页面布局中获得对应的UI控件
        TextView tv_main_title=(TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(loginUserName+"的相册");
        TextView tv_back=(TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumActivity.this.finish();
            }
        });
        RelativeLayout rl_title_bar=(RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.TRANSPARENT);

        String url = "http://47.106.158.244/copyright/image/imageList/"+loginUserName;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                list.clear();       //清空list
                String s_album = new String(responseBody);
                try {

                    JSONObject j_album = new JSONObject(s_album);
                    Log.i("TESTJ_album",j_album+"");
                    JSONArray array = j_album.getJSONArray("list");
                    Log.i("TEST_list",array+"");
                    for (int i=0;i<array.length();i++){

                        Map<String,Object> map = new HashMap<String, Object>();
                        JSONObject jsonObject = (JSONObject) array.get(i);
                        Log.i("TEST_jasonObject", String.valueOf(jsonObject));

                        String imgId = jsonObject.getString("imgId");
                        Log.i("TEST_imgID",imgId+"");
                        String waterAddr = jsonObject.getString("waterAddr");
                        String arnoldAddr= jsonObject.getString("arnoldAddr");
                        String waterPassword =jsonObject.getString("waterPassword");
                        String arnoldPassword = jsonObject.getString("arnoldPassword");
                        String detail = jsonObject.getString("detail");
                        String isDelete = jsonObject.getString("isDelete");     //判断删除
                        String suffix = jsonObject.getString("suffix");         //图片格式

                        map.put("imgId",imgId);
                        Log.i("TEST_ID:",imgId);
                        map.put("waterAddr",waterAddr);
                        map.put("arnoldAddr",arnoldAddr);
                        map.put("waterPassword",waterPassword);
                        map.put("arnoldPassword",arnoldPassword);
                        map.put("detail",detail);
                        map.put("suffix",suffix);
                        map.put("isDelete",isDelete);
                        list.add(map);
                        Log.i("TEST_map",map.toString());
                        Log.i("TEST_list:",list.toString());


                    }


                    lv=findViewById(R.id.lv);
                    /*        lv.setAdapter(new photoListViewAdpter(this,map));*/
                    Mybaseadapter list_item = new Mybaseadapter(AlbumActivity.this);
                    lv.setAdapter(list_item);
/*                    lv.setOnItemClickListener(this);*/

                    list_item.notifyDataSetInvalidated();
                    lv.invalidate();
                    invalidateOptionsMenu();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(getApplicationContext(),"this is item",Toast.LENGTH_SHORT).show();
            photoListCell oneOfPhoto = (photoListCell) lv.getItemAtPosition(i);         //get the list
            String name = oneOfPhoto.arnoldAddr;                                 //get the message in this list
            Toast.makeText(AlbumActivity.this,""+name,Toast.LENGTH_SHORT).show();

    }





    //listview的适配器
    public class Mybaseadapter extends BaseAdapter {
        private Context context;
        private RequestQueue queue;
        private ImageLoader myImageloader;
        private ImageLoader arnold_imageloader;



        public Mybaseadapter(Context context) {
            this.context = context;
            queue = Volley.newRequestQueue(context);
            myImageloader = new ImageLoader(queue, new BitmapCache());
            arnold_imageloader = new ImageLoader(queue,new BitmapCache());
        }


        @Override
        public int getCount() {
            return list.size();

        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.album_item, null);
                viewHolder.waterAddr = (NetworkImageView) convertView.findViewById(R.id.iv_waterPhoto);     //水印图片地址
                viewHolder.insertPassword = (TextView) convertView.findViewById(R.id.tv_insertPassword);    //水印密码
                viewHolder.message = (TextView) convertView.findViewById(R.id.tv_water_message);
                viewHolder.download = (ImageView) convertView.findViewById(R.id.iv_albumDownLoad);      //下载按钮
                viewHolder.delete = (ImageView) convertView.findViewById(R.id.iv_albumDelete);          //删除按钮
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String S_waterAddr = list.get(position).get("waterAddr").toString();    //水印图片URL
            viewHolder.waterAddr.setImageResource(R.drawable.loading);
            viewHolder.waterAddr.setTag(S_waterAddr);




            Log.i("TEST_S_waterAddr",S_waterAddr);
            if (S_waterAddr.equals("null")||S_waterAddr.equals("")){
                viewHolder.waterAddr.setImageResource(R.drawable.when_photo_is_null);
            }else viewHolder.waterAddr.setImageUrl(S_waterAddr,myImageloader);

            viewHolder.insertPassword.setText("水印密码："+list.get(position).get("waterPassword".toString()));
            viewHolder.message.setText(list.get(position).get("detail").toString());

            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.waterAddr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Drawable drawable = finalViewHolder.waterAddr.getDrawable();
                    Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
                    Toast.makeText(getApplicationContext(),"显示详细图片",Toast.LENGTH_SHORT).show();


                }
            });
            viewHolder.insertPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"this is insertPassword",Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"this is message",Toast.LENGTH_SHORT).show();
                }
            });


            viewHolder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    HashMap hashMap = (HashMap) lv.getItemAtPosition(position);
                    Log.i("TAG_hashMap",hashMap.toString());

                    Drawable drawable = finalViewHolder.waterAddr.getDrawable();
                    Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();


                    new saveImage(bitmap, getApplicationContext(), "image/"+hashMap.get("suffix"));

                    /*一键复制*/
                    // 从API11开始android推荐使用android.content.ClipboardManager
                    // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText((String)hashMap.get("waterPassword"));
                    Toast.makeText(getApplicationContext(), "成功下载并复制密码", Toast.LENGTH_LONG).show();

                    Log.i("TAG","image/"+hashMap.get("suffix"));

                    Log.i("TAG_bitmap",bitmap.toString());//get the message in this list

                }
            });

            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    new AlertDialog.Builder(AlbumActivity.this).setTitle("确认删除图片吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“确认”后的操作

                                    String uri = "http://47.106.158.244/copyright/image/deleteImage";

                                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                                    RequestParams params = new RequestParams();

                                    HashMap hashMap = (HashMap) lv.getItemAtPosition(position);
                                    String imgId = (String) hashMap.get("imgId");
                                    params.add("imgId",imgId);
                                    asyncHttpClient.post(uri, params, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            Log.i("TAG","删除图片成功");
                                            list.remove(position);
                                            lv.postInvalidate();
                                            notifyDataSetChanged();

                                            Toast.makeText(AlbumActivity.this,"删除图片成功",Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“返回”后的操作,这里不设置没有任何操作


                                }
                            }).show();


                }
            });


            return convertView;
        }

    }

    final static class ViewHolder {
        NetworkImageView waterAddr;
        TextView message;
        TextView insertPassword;
        ImageView download;
        ImageView delete;
    }

}
