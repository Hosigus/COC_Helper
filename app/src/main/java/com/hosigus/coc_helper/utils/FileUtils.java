package com.hosigus.coc_helper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import com.hosigus.coc_helper.MyApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 某只机智 on 2018/2/22.
 */

public class FileUtils {
    /**
     * 获取储存路径目录
     */
    public static File getFilesDir() {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            file = MyApplication.getContext().getFilesDir();
        }
        return file;
    }

    /**
     * 保存图片
     */
    public static void saveBitmapToFile(Bitmap bitmap, int quality, String name){
        try {
            File desFile = new File(getFilesDir(),encodeName(name));
            FileOutputStream fos = new FileOutputStream(desFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveBitmapToFile(Bitmap bitmap,String name){
        saveBitmapToFile(bitmap,100,name);
    }
    /**
     * 提供设置图片的核心方法
     */
    public static void readBitmapInto(ImageView iv, int holderResId, String name) {
        iv.setImageResource(holderResId);
        readBitmapInto(iv,name);
    }
    public static void readBitmapInto(ImageView imageView, String name) {
        Bitmap diskBitmap = getBitmapFromFile(name);
        if (diskBitmap != null) {
            imageView.setImageBitmap(diskBitmap);
        }
    }
    /**
     * 从文件中获取bitmap
     */
    public static Bitmap getBitmapFromFile(String name) {
        File file = new File(getFilesDir(),encodeName(name));
        if (file.exists() && file.length() > 0) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            return null;
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String name){
        return new File(getFilesDir(),encodeName(name)).delete();
    }

    /**
     * 重命名文件
     */
    public static boolean renameFile(String oldName,String newName){
        return new File(getFilesDir(),encodeName(oldName)).renameTo(new File(getFilesDir(),encodeName(newName)));
    }
    /**
     * 抄的，按MD5
     */
    public static String encodeName(String name) {
        try {
            // 得到信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            // 通过信息摘要器，将密码字符串转换成字节数组
            byte[] bys = digest.digest(name.getBytes());
            // 定义一个字符串缓冲区存储字符串
            StringBuffer buffer = new StringBuffer();
            // 遍历得到每一个字节
            for (byte b : bys) {
                // 每一个字节都与上一个十六进制，得到十进制数
                int number = b & 0xff;
                // 将十进制数转换成十六进制数进行显示
                String str = Integer.toHexString(number);
                // 如果得到的十六进制数只有四位，则需要将其补全到八位
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
