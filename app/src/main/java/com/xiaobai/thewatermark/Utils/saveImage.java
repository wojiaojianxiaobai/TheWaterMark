package com.xiaobai.thewatermark.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*保存图片设置*/
public class saveImage{


    public saveImage(Bitmap bmp,Context context,String imageFormat) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "WaterMarkPhoto");    //把照片保存到SD卡的WaterMarkPhoto目录下
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String[] getImageFormat = imageFormat.split("image/");
        String format = getImageFormat[1];
        String fileName = System.currentTimeMillis() + "."+format;
        Log.i("TEST_format",format);


        switch (format) {
            case "jpeg": {             //jpeg处理

                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
                Log.i("TEST_appDir", appDir + "");
                break;
            }
            case "png": {            //png处理
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
                Log.i("TEST_appDir", appDir + "");
                break;
            }
            case "bmp": {           //bitmap处理
                if (bmp == null)
                    return;
                // 位图大小
                int nBmpWidth = bmp.getWidth();
                int nBmpHeight = bmp.getHeight();
                // 图像数据大小
                int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
                try {
                    // 存储文件名
/*                    String filename = "/sdcard/test.bmp";*/
                    File file = new File(fileName);
                    Log.i("TEST_fileNameForBitmap",fileName);

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileos = new FileOutputStream(fileName);
                    // bmp文件头
                    int bfType = 0x4d42;
                    long bfSize = 14 + 40 + bufferSize;
                    int bfReserved1 = 0;
                    int bfReserved2 = 0;
                    long bfOffBits = 14 + 40;
                    // 保存bmp文件头
                    writeWord(fileos, bfType);
                    writeDword(fileos, bfSize);
                    writeWord(fileos, bfReserved1);
                    writeWord(fileos, bfReserved2);
                    writeDword(fileos, bfOffBits);
                    // bmp信息头
                    long biSize = 40L;
                    long biWidth = nBmpWidth;
                    long biHeight = nBmpHeight;
                    int biPlanes = 1;
                    int biBitCount = 24;
                    long biCompression = 0L;
                    long biSizeImage = 0L;
                    long biXpelsPerMeter = 0L;
                    long biYPelsPerMeter = 0L;
                    long biClrUsed = 0L;
                    long biClrImportant = 0L;
                    // 保存bmp信息头
                    writeDword(fileos, biSize);
                    writeLong(fileos, biWidth);
                    writeLong(fileos, biHeight);
                    writeWord(fileos, biPlanes);
                    writeWord(fileos, biBitCount);
                    writeDword(fileos, biCompression);
                    writeDword(fileos, biSizeImage);
                    writeLong(fileos, biXpelsPerMeter);
                    writeLong(fileos, biYPelsPerMeter);
                    writeDword(fileos, biClrUsed);
                    writeDword(fileos, biClrImportant);
                    // 像素扫描
                    byte bmpData[] = new byte[bufferSize];
                    int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
                    for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                        for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                            int clr = bmp.getPixel(wRow, nCol);
                            bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                            bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                            bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                        }

                    fileos.write(bmpData);
                    fileos.flush();
                    fileos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


    }



    /*位图处理*/
    protected void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    protected void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    protected void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }


}