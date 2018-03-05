package com.hosigus.coc_helper.utils;

import android.os.Handler;

import com.hosigus.coc_helper.items.JSONResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by 某只机智 on 2018/2/16.
 */

public class NetConnectUtils {
    private final static Handler handler = new Handler();

    public static void requestNet(final String urlStr,final String params,final NetCallBack callBack){
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url=new URL(urlStr);
                if(urlStr.contains("https"))
                    conn=(HttpsURLConnection) url.openConnection();
                else
                    conn=(HttpURLConnection)url.openConnection();

                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if(params!=null){
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes());
                    os.flush();
                    os.close();
                }else {
                    conn.setRequestMethod("GET");
                }

                int resCode=conn.getResponseCode();
                if(resCode== HttpsURLConnection.HTTP_OK){
                    InputStream inputStream=conn.getInputStream();
                    Scanner in=new Scanner(inputStream);
                    StringBuilder builder=new StringBuilder();
                    while (in.hasNextLine()){
                        builder.append(in.nextLine()).append("\n");
                    }
                    String resStr=builder.toString();
                    handler.post(()->{
                        callBack.connectOK(JSONUtils.parserJSON(resStr));
                    });
                }else {
                    handler.post(()->{
                        callBack.connectFail(getStringFromErrorResCode(resCode));
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.post(()->{
                    callBack.connectFail("请检查网络");
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(()->{
                    callBack.connectFail("请检查网络");
                });
            }
        }).start();
    }
    public static void requestNet(final String urlStr,final NetCallBack callBack){
        requestNet(urlStr,null,callBack);
    }
    private static String  getStringFromErrorResCode(int resCode){
        switch (resCode){
            case 404:
                return "服务器地址已更新,请联系管理员";
            default:
                break;
        }
        return "无法识别错误码："+resCode;
    }
    public interface NetCallBack{
        void connectOK(JSONResult result);
        void connectFail(String resStr);
    }
}
