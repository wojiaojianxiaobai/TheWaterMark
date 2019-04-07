package com.xiaobai.thewatermark.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xiaobai.thewatermark.Activit.AlbumActivity;
import com.xiaobai.thewatermark.R;

public class showPhotoByBitmap {

    public showPhotoByBitmap(Activity context, Bitmap bitmap) {
        final android.support.v7.app.AlertDialog.Builder customizeDialog = new android.support.v7.app.AlertDialog.Builder(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.watchstartphoto, null);  //自定义Diglog
        customizeDialog.setCancelable(true);
        customizeDialog.setView(dialogView);
        customizeDialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }
        );
        final android.support.v7.app.AlertDialog dlg = customizeDialog.create();
        dlg.setContentView(R.layout.watchstartphoto);

        final ImageView showStartPhoto;
        showStartPhoto = (ImageView) dialogView.findViewById(R.id.showStartPhoto);
        showStartPhoto.setImageBitmap(bitmap);
        dlg.show();
    }
}
