package com.xiaobai.thewatermark.Activit;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xiaobai.thewatermark.R;
import com.xiaobai.thewatermark.Utils.BitmapToBytesUtil;
import com.xiaobai.thewatermark.Utils.LeftHold;
import com.xiaobai.thewatermark.Utils.Location;
import com.xiaobai.thewatermark.Utils.getWaterDialog;
import com.xiaobai.thewatermark.Utils.getWaterMessageDialog;
import com.xiaobai.thewatermark.Utils.recovery;
import com.xiaobai.thewatermark.Utils.saveImage;
import com.xiaobai.thewatermark.WeChat.PhotoPickerActivity;
import com.xiaobai.thewatermark.WeChat.PhotoPreviewActivity;
import com.xiaobai.thewatermark.WeChat.SelectModel;
import com.xiaobai.thewatermark.WeChat.intent.PhotoPickerIntent;
import com.xiaobai.thewatermark.WeChat.intent.PhotoPreviewIntent;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class  MainActivity extends Activity {
    private static final int REQ_TAKE_PHOTO = 1;    //相机照相检测
    public static final int CHOOSE_PHOTO = 2;     //相册上传照片检测
    private static final int REQUEST_CAMERA_CODE = 10;          //相机处理结果
    private static final int REQUEST_PREVIEW_CODE = 20;         //相册处理结果

    public static ArrayList<String> imagePaths = new ArrayList<>();   //存放选中的相册

    private GridView gridView;      //图片窗口
    private MainActivity.GridAdapter gridAdapter;    //GridView适配器

    private static final String TAG =MainActivity.class.getSimpleName();

    /*工具栏列表*/
    private RelativeLayout insertWaterMark;     //工具栏   插入水印按钮
    private RelativeLayout rl_insertPhoto;      //工具栏   插入图片按钮
    private  LinearLayout rl_getStartPhoto;     //工具栏   查看原图
    private LinearLayout rl_saveStartPhoto;     //工具栏   保存图片按钮
    private RelativeLayout rl_getWaterMark;     //工具栏   提取水印按钮


    private ImageView showStartPhoto;           //显示原图
    private Bitmap bitmap1;                     //保留原图uri
    private Bitmap bitmap2;                     //操作过后的图片uri

    private ImageView mImageView;               //显示图片窗口
    private ImageView nullImageTip;              //空白图片提示



    private ImageView my_icon;      //用户头像

    /*记录类*/
    private String imageFormat;     //记录存储图片格式（后面要改成数组）
    private TextView myInfoName;    //记录用户名


    /*检测类*/
    boolean thePhotoIsInsert=false;   //用于检测是否插入图片
    boolean IsSingePhoto = true;       //检测是单图片还是多图片


    private LeftHold mLeftMenu; //左侧隐藏菜单栏

    private LinearLayout remakePassword; //修改密码
    private LinearLayout swicthUser;    //切换用户


    /*高德定位*/
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static String Latitude;      //经度
    public static String Longitude;     //纬度
    public static String Address;       //地址


    /*废弃对象*/
    private RelativeLayout rl_recovery;         //工具栏   还原图片按钮
    private RelativeLayout rl_covery;           //工具栏   置乱按钮        ------测试用-----


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        mImageView = findViewById(R.id.imageView);          //单图片显示


        /*显示加载的本地图片*/
        gridView = (GridView) findViewById(R.id.gridView);

        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


/*                String imgs = (String) parent.getItemAtPosition(position);*/
/*                if ("paizhao".equals(imgs) ){                                          //添加图片按钮
                    PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                    intent.setSelectModel(SelectModel.MULTI);
                    intent.setShowCarema(true);        // 需要在图库显示拍照按钮时候启用
                    intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                    intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                    startActivityForResult(intent, REQUEST_CAMERA_CODE);
                }else {*/
                    //点击查看图片
                    Toast.makeText(MainActivity.this, "1" + position, Toast.LENGTH_SHORT).show();
                    PhotoPreviewIntent photoPreviewIntent = new PhotoPreviewIntent(MainActivity.this);
                    photoPreviewIntent.setCurrentItem(position);
                    photoPreviewIntent.setPhotoPaths(imagePaths);

                    startActivityForResult(photoPreviewIntent, REQUEST_PREVIEW_CODE);
/*                }*/
            }
        });
        gridAdapter = new MainActivity.GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);



        /*获取用户名*/
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        final String loginUserName = sp.getString("loginUserName", "未登录");
        myInfoName = findViewById(R.id.info_name);
        myInfoName.setText(loginUserName);

        /*修改密码*/

        remakePassword = findViewById(R.id.remakePassword);
        remakePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RemakePassword.class);
                startActivity(intent);
            }
        });

        /*切换用户*/
        swicthUser = findViewById(R.id.ll_SwitchUser);
        swicthUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.app.AlertDialog.Builder(MainActivity.this).setTitle("切换用户")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 点击“确认”后的操作

                                Intent intent  = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                                MainActivity.this.finish();
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

        /*我的相册*/
        findViewById(R.id.ll_my_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(intent);
            }
        });


        mLeftMenu = (LeftHold) findViewById(R.id.id_menu);  //左侧隐藏菜单
        /*主布局监听隐藏菜单*/
        RelativeLayout mMainLayout = (RelativeLayout) findViewById(R.id.id_main_menu);
        mMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });
        /* 头像点击显示/隐藏菜单 */
        my_icon = findViewById(R.id.my_icon);
        my_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMenu();
            }
        });


        insert();           //插入水印处理
        showStartPhoto();   //查看原图处理
        permission();       //获取权限操作


        /*保留操作图*/
        if (bitmap2!=null){
            mImageView.setImageBitmap(bitmap2);
        }else if (bitmap1 != null) {
            mImageView.setImageBitmap(bitmap1);
        }

        /*提取水印监听*/
        rl_getWaterMark = (RelativeLayout) findViewById(R.id.rl_getWaterMark);
        rl_getWaterMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (thePhotoIsInsert){
                    new getWaterDialog(MainActivity.this,bitmap1,mImageView,imageFormat);
                }else Toast.makeText(MainActivity.this,"请先插入图片",Toast.LENGTH_LONG).show();

            }
        });



        /*置乱按钮*/
        /*------------------------------------测试按钮-----------------------*/
        rl_covery = (RelativeLayout) findViewById(R.id.rl_covery);
        rl_covery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getPositioning();


            }
        });


        /*还原图片按钮*/
        rl_recovery = (RelativeLayout) findViewById(R.id.rl_recovery);
        rl_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (thePhotoIsInsert){
                    new recovery(mImageView,MainActivity.this,loginUserName);

                }else Toast.makeText(MainActivity.this,"请先插入图片",Toast.LENGTH_LONG).show();

            }
        });


        /*保存按钮监听*/
        rl_saveStartPhoto = (LinearLayout) findViewById(R.id.rl_savePhoto);
        rl_saveStartPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                if (thePhotoIsInsert) {


                    if (IsSingePhoto){

                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("正在保存...");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        Bitmap bitmap2 = ((BitmapDrawable) ((ImageView) mImageView).getDrawable()).getBitmap();    //获取图片中的uri保存至bitmap2
                        new saveImage(bitmap2,MainActivity.this,imageFormat);         //把bitmap2对应的图片保存图片至本地
                        progressDialog.dismiss();

                        Toast.makeText(MainActivity.this, "保存图片成功", Toast.LENGTH_SHORT).show();

                    }else {


                        for (int i = 0;i<imagePaths.size();i++){

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imagePaths.get(i)))));
                        }
                        Toast.makeText(MainActivity.this,"保存"+imagePaths.size()+"张图片",Toast.LENGTH_LONG).show();

                    }


                } else
                    Toast.makeText(MainActivity.this, "请先上传图片", Toast.LENGTH_SHORT).show();


            }
        });




        /*插入图片监听*/
        rl_insertPhoto = (RelativeLayout) findViewById(R.id.rl_insertPhoto);
        rl_insertPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("TAG","开始定位");
                requestLocation();

                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true);        // 需要在图库显示拍照按钮时候启用
                intent.setMaxTotal(9); // 最多选择照片数量，默认为6
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CAMERA_CODE);
/*                new insertPhoto();*/
            }
        });
    }

    String mCurrentPhotoPath;


    // 高德定位
    public void getPositioning() {

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //启动定位
        Log.i("TAG","开始定位");
        mLocationClient.startLocation();
    }







    /*查看原图dialog弹窗*/
    /*初始化控件*/
    private void showStartPhoto() {
        final AlertDialog.Builder customizeDialog = new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.watchstartphoto, null);  //自定义Diglog
        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        customizeDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                }
        );
        final AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.watchstartphoto);
        showStartPhoto = (ImageView) dialogView.findViewById(R.id.showStartPhoto);

        rl_getStartPhoto = (LinearLayout) findViewById(R.id.rl_getstartPhoto);
        rl_getStartPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (thePhotoIsInsert) {
                    if (IsSingePhoto){              //单图模式下查看原图
                        showStartPhoto.setImageBitmap(bitmap1);
                        dlg.show();

                    }else {                         //多图模式下查看图片
                        Toast.makeText(MainActivity.this,"多图模式下查看原图",Toast.LENGTH_LONG).show();
                    }

                } else
                    Toast.makeText(MainActivity.this, "请先插入图片", Toast.LENGTH_SHORT).show();


            }
        });


    }


    /*检测权限*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:{
            }

            /*GPS权限请求检测*/

            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }

            break;
            default:
                /*             default: Toast.makeText(TestMainActivity.this,"必须要获取全部权限才能使用程序",Toast.LENGTH_SHORT).show();*/
        }
    }//判断是否允许申请权限，是的话就打开相册，否则弹出提示


    @TargetApi(19)//使用@TargetApi， 使高版本API的代码在低版本SDK不报错
    // Android为常用的数据类型(如：音视频、图片和联系方式等)提供了大量的ContentProvider，它们被定义在android.provider包下。如多媒体的com.android.providers.media
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri))//如果document类型的Uri,则通过document id处理
        {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;//获取图片的selection
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);//通过图片的uri和selection来获取真实的图片路径
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads")
                        , Long.valueOf(docId));//解析uri的id，然后把id和contentUri连接成一个新的Uri
                imagePath = getImagePath(contentUri, null);//通过uri来获取图片的真实路径
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme()))//如果是content型的Uri，则使用普通方式处理
        {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme()))//如果是file型的Uri，直接获取图片路径即可
        {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);//它的uri是my被封装过的，所以不用解析，直接获取图片路径
    }

    private String getImagePath(Uri uri, String selection)//通过Uri和selection来获取真实的图片路径
    {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        //使用contentResolver中的query()方法，通过移动游标的位置来遍历
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {

        if (imagePath != null) {
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            //BitmapFactory.Options类是BitmapFactory对图片进行解码时使用的一个配置参数类，其中定义了一系列的public成员变量，每个成员变量代表一个配置参数。
            options.inJustDecodeBounds = true;
            //如果inJustDecoedBounds设置为true的话，解码bitmap时可以只返回其高、宽和Mime类型，而不必为其申请内存，从而节省了内存空间。
            BitmapFactory.decodeFile(imagePath, options);//加载sd卡的图片
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            mImageView.setImageBitmap(bitmap);//让图片在imageview上显示
            imageFormat = options.outMimeType;              //读取图片格式到imageFormat
            Log.i("TEST_imageFormat",imageFormat);
            bitmap1 = bitmap;   //保留原图URI


        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }


    /*插入水印处理*/
    private void insert() {
        insertWaterMark = (RelativeLayout) findViewById(R.id.rl_insertWaterMark);
        insertWaterMark.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (thePhotoIsInsert) {
                    if (IsSingePhoto){                  //单图操作
                        new getWaterMessageDialog(MainActivity.this,mImageView,IsSingePhoto, null,imageFormat, gridView);
                    }else { //多图操作
                        ArrayList<String> photoArrayList1 = new ArrayList<String>();        //存储图片转化Base64数组

                        for (int i = 0;i < imagePaths.size();i++){
                            String photoPaths = imagePaths.get(i);
                            Bitmap bitmap  = BitmapFactory.decodeFile(photoPaths);
                            String base64Arrays = String.valueOf(cz.msebera.android.httpclient.extras.Base64.encodeToString(BitmapToBytesUtil.bitmapToBytes(bitmap),cz.msebera.android.httpclient.extras.Base64.DEFAULT));
                            /*获取图片格式*/
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            //BitmapFactory.Options类是BitmapFactory对图片进行解码时使用的一个配置参数类，其中定义了一系列的public成员变量，每个成员变量代表一个配置参数。
                            options.inJustDecodeBounds = true;
                            //如果inJustDecoedBounds设置为true的话，解码bitmap时可以只返回其高、宽和Mime类型，而不必为其申请内存，从而节省了内存空间。
                            BitmapFactory.decodeFile(photoPaths, options);//加载sd卡的图片
                            options.inJustDecodeBounds = false;
                            imageFormat = options.outMimeType;              //读取图片格式到imageFormat
                            String[] foramtList = imageFormat.split("image/");
                            String format = foramtList[1];
                            Log.i("TEST_imageFormat",imageFormat);

                            photoArrayList1.add("data:"+imageFormat+";base64,"+base64Arrays);

                        }
                        new getWaterMessageDialog(MainActivity.this,null,IsSingePhoto,photoArrayList1, imageFormat,gridView);
                        Toast.makeText(MainActivity.this,"多图嵌入水印模式",Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, "请先插入图片", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void requestLocation() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        getPositioning();
        mLocationOption.setInterval(2000);
        mLocationClient.startLocation();
/*        mLocationClient.start();//调用start方法开始定位*/
    }

/*    private void initLocation() {
        LocationClientOption option = new LocationClientOption();//创建一个LocationClientOption对象
        option.setScanSpan(5000);//定位的时间间隔


        mLocationClient.setLocOption(option);
    }*/



    //    高德定位回调
    class MyAMapLocationListener implements AMapLocationListener{
        @Override
        public void onLocationChanged(final AMapLocation aMapLocation) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Location location = new Location();
                    location.setLatitude(aMapLocation.getLatitude());
                    location.setLongitude(aMapLocation.getLongitude());
                    location.setAddress(aMapLocation.getAddress());
                    location.setCountry(aMapLocation.getCountry());
                    location.setCity(aMapLocation.getCity());
                    location.setDistrict(aMapLocation.getDistrict());
                    location.setStreet(aMapLocation.getStreet());
                    location.setStreetNum(aMapLocation.getStreetNum());
                    location.setCityCode(aMapLocation.getCityCode());
                    location.setAdCode(aMapLocation.getAdCode());
                    location.setPoiName(aMapLocation.getPoiName());
                    location.setAoiName(aMapLocation.getAoiName());
                    location.setErrorCode(aMapLocation.getErrorCode());

                    Gson gson = new Gson();
                    String json = gson.toJson(location);
                    Log.i("TAG_location",json);

                    Address = location.getAddress();
                    Longitude = String.valueOf(location.getLongitude());
                    Latitude = String.valueOf(location.getLatitude());
                }
            }).start();
        }
    }



    /*改写返回物理按键*/
    private long exitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                mImageView.setDrawingCacheEnabled(true);
                bitmap2 = mImageView.getDrawingCache();
                mImageView.setDrawingCacheEnabled(false);       //保留操作图片
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    /*GridView适配器*/

    public class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        private LayoutInflater inflater;
        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
/*            if(listUrls.size() == 10){               //长度超出删除最后的上传
                listUrls.remove(listUrls.size()-1);
            }*/
            inflater = LayoutInflater.from(MainActivity.this);
        }

        public int getCount(){
            return  listUrls.size();
        }
        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.GridAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new MainActivity.GridAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.grid_item, parent,false);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (MainActivity.GridAdapter.ViewHolder)convertView.getTag();
            }

            final String path=listUrls.get(position);
            if (path.equals("paizhao")){
                holder.image.setImageResource(R.drawable.find_add_img);
            }else {
                Glide.with(MainActivity.this)
                        .load(path)
                        .placeholder(R.drawable.default_error)
                        .error(R.drawable.default_error)
                        .centerCrop()
                        .crossFade()
                        .into(holder.image);
            }
            return convertView;
        }
        class ViewHolder {
            ImageView image;
        }
    }


    /*处理结果*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_TAKE_PHOTO://返回结果
                if (resultCode != Activity.RESULT_OK) return;

                /*获得View的长宽*/
                int targetW = mImageView.getWidth();
                int targetH = mImageView.getHeight();

                /*获得Bitmap的长宽*/
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                //根据比例把Bitmap显示在View中
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
                /*将图像文件转换成Bitmap格式填充View*/
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                mImageView.setImageBitmap(bitmap);

                imageFormat = bmOptions.outMimeType;            //读取相机返回照片的格式
                Log.i("TEST_iamgeFormat",imageFormat);

                bitmap1 = bitmap;           //保留原图URI
                break;
            case CHOOSE_PHOTO:                          //返回相册处理结果
                if (resultCode == RESULT_OK)//判断手机版本号
                {
                    if (Build.VERSION.SDK_INT >= 19)//4.4以及以上系统
                    {
                        handleImageOnKitKat(data);
                    } else //4.4以下系统
                    {
                        handleImageBeforeKitKat(data);
                    }
                }


                // 选择照片
            case REQUEST_CAMERA_CODE:

                ArrayList<String> list = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                Log.d(TAG, "数量："+list.size());
                if (list.size()==1){
                    Toast.makeText(MainActivity.this,"单选模式",Toast.LENGTH_LONG).show();

                    String singephoto = list.get(0);
                    Log.i("TEST",singephoto);

                    list.clear();
                    loadAdpater(list);          //清空gridView


                    mImageView.setVisibility(View.VISIBLE);         //设置可见

                    /*获得View的长宽*/
                    int targetW1 = mImageView.getWidth();
                    int targetH1 = mImageView.getHeight();

                    /*获得Bitmap的长宽*/
                    BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
                    bmOptions1.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions1);
                    int photoW1 = bmOptions1.outWidth;
                    int photoH1 = bmOptions1.outHeight;

                    //根据比例把Bitmap显示在View中
                    int scaleFactor1 = Math.min(photoW1 / targetW1, photoH1 / targetH1);
                    /*将图像文件转换成Bitmap格式填充View*/
                    bmOptions1.inJustDecodeBounds = false;
                    bmOptions1.inSampleSize = scaleFactor1;
                    bmOptions1.inPurgeable = true;

                    Bitmap bitmap5 = BitmapFactory.decodeFile(singephoto, bmOptions1);
                    mImageView.setImageBitmap(bitmap5);

                    imageFormat = bmOptions1.outMimeType;            //读取相机返回照片的格式
                    Log.i("TEST_iamgeFormat",imageFormat);

                    bitmap1 = bitmap5;           //保留原图URI

                    mImageView.setImageBitmap(bitmap5);

                    /*记录状态*/
                    thePhotoIsInsert=true;
                    IsSingePhoto = true;

                    /*如果没有选择图片，则显示空白提示*/
                }else if (list.size()==0){
                    nullImageTip = findViewById(R.id.null_image_tip);   //空白页面
                    nullImageTip.setVisibility(View.VISIBLE);         //设置可见

                }


                /*GirdView插入图片*/
                else {
                    /*隐藏空白提示页面*/

                    mImageView.setVisibility(View.GONE);

                    /*隐藏单图片显示页面*/
                    nullImageTip = findViewById(R.id.null_image_tip);
                    nullImageTip.setVisibility(View.GONE);         //设置可见

                    loadAdpater(list);

                    /*记录状态*/
                    thePhotoIsInsert=true;
                    IsSingePhoto = false;
                }

                break;
            // 预览
            case REQUEST_PREVIEW_CODE:


                if (imagePaths!=null&& imagePaths.size()>0){              //清空原来选中的图片
                    imagePaths.clear();
                }
                ArrayList<String> ListExtra = data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT);
                loadAdpater(ListExtra);


                break;
            default:
                break;


        }
    }

    /*显示和隐藏侧滑菜单*/
    public void toggleMenu() {
        mLeftMenu.toggle();
    }
    public void closeMenu() {
        mLeftMenu.closeMenu();
    }


    /*申请权限*/
    public void permission() {
        List<String> permissionList = new ArrayList<>();//创建一个空的list集合
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }//判断是否申请了READ_PHONE_STATE权限，否的话添加到集合中
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }//判断是否申请了ACCESS_FINE_LOCATION权限，否的话添加到集合中
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }//判断是否申请了WRITE_EXTERNAL_STORAGE权限，否的话则添加到集合中
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)     //重复获取SD卡权限
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);//判断集合是否为空，否的话将List转换成数组，再调用ActivityCompat.requestPermissions()方法一次性申请
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }



    private void loadAdpater(ArrayList<String> paths){
        if (imagePaths!=null&& imagePaths.size()>0){              //清空原来选中的图片
            imagePaths.clear();
        }
/*        if (paths.contains("paizhao")){
            paths.remove("paizhao");
        }*/
        /*        paths.add("paizhao");      */                             //添加图片按钮
        imagePaths.addAll(paths);
        gridAdapter  = new MainActivity.GridAdapter(imagePaths);
        gridView.setAdapter(gridAdapter);
        try{
            JSONArray obj = new JSONArray(imagePaths);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}



