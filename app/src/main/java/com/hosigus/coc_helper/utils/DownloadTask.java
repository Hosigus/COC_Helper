package com.hosigus.coc_helper.utils;

/**
 * Created by 某只机智 on 2018/3/8.
 */
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class DownloadTask extends AsyncTask<String, Integer, Boolean> {

    private Context mContext;
    private ProgressDialog dialog;

    public DownloadTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在下载安装包，请稍候……");
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        InputStream is = null;
        FileOutputStream fos = null;

        String downloadUrl = strings[0];
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"app-release.apk");
        if (file.exists())
            if (!file.delete())
                return false;

        HttpURLConnection conn=null;

        boolean flag = false;
        try {
            conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
            conn.setConnectTimeout(5000);

            int contentLength = conn.getContentLength();

            is = conn.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            int point = 0;
            while ((len=is.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                fos.flush();
                point += len;
                publishProgress(point,contentLength);
            }
            flag = true;
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is!=null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fos!=null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return flag;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        dialog.setMessage(String.format(Locale.CHINA,"共 %.2f M,已下载 %.2f M",values[1] / 1024.0 / 1024, values[0] / 1024.0 / 1024));
        dialog.setProgress(100*values[0]/values[1]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        dialog.dismiss();
        if (aBoolean) {
            File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"app-release.apk");
            Uri fileUri;
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(mContext, "com.hosigus.coc_helper.fileProvider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(fileUri
                    ,"application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }else {
            Toast.makeText(mContext, "下载失败",Toast.LENGTH_SHORT).show();
        }
    }
}
