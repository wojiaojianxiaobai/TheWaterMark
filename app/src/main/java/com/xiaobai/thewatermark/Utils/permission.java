package com.xiaobai.thewatermark.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.xiaobai.thewatermark.Activit.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class permission {



    public void permission(Context context, Activity activity) {


        List<String> permissionList = new ArrayList<>();//创建一个空的list集合
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }//判断是否申请了READ_PHONE_STATE权限，否的话添加到集合中
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }//判断是否申请了ACCESS_FINE_LOCATION权限，否的话添加到集合中
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }//判断是否申请了WRITE_EXTERNAL_STORAGE权限，否的话则添加到集合中
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)     //重复获取SD卡权限
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, 1);//判断集合是否为空，否的话将List转换成数组，再调用ActivityCompat.requestPermissions()方法一次性申请
        }
    }
}
