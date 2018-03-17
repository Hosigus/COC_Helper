package com.hosigus.coc_helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.hosigus.coc_helper.utils.LogUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hosigus.coc_helper.configs.NetConfig.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Scanner;

public class SocketService extends Service {
    private boolean isRun ;

    private ReadThread mReadThread;
    private WeakReference<Socket> mSocket;

    private Handler mHandler = new Handler();
    private SocketBind mBind = new SocketBind();

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            sendHB();
            if (isRun)
                mHandler.postDelayed(this,HEART_BEAT_RATE);
            else
                mHandler.post(()-> send(OUT_STR));
        }
    };

    private void sendHB() {
        send(HB_STR);
    }
    private void send(String msg) {
        LogUtils.d("Test", "send: "+msg);
        if ((mSocket==null || mSocket.get()==null)&&!restartSocket())
            return;
        Socket soc = mSocket.get();
        if (soc.isClosed() && !soc.isOutputShutdown()&&!restartSocket())
            return;
        new Thread(()->{
            try {
                OutputStream os = soc.getOutputStream();
                os.write((msg+"\n").getBytes("UTF-8"));
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void initSocket(){
        new Thread(()->{
            try {
                Socket socket=new Socket(HOST,PORT);
                mSocket = new WeakReference<>(socket);
                mReadThread=new ReadThread(socket);
                mReadThread.start();
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable,HEART_BEAT_RATE);
                mHandler.post(()->{
                    String vitalMsg = mBind.callBack.restart();
                    if (vitalMsg!=null) {
                        send(vitalMsg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.show("连接不到服务器\n注:服务器在国外,可能被墙,过会再试试");
            }
        }).start();
    }
    private boolean restartSocket(){
        if (mReadThread==null){
            initSocket();
        }else {
            mReadThread.release();
            releaseLastSocket(mSocket);
            initSocket();
        }
        if (mReadThread==null){
            mBind.callBack.receive(ERROR,null);
            ToastUtils.show("连接不到服务器\n注:服务器在国外,可能被墙,过会再试试");
            return false;
        }
        return true;
    }
    private void releaseLastSocket(WeakReference<Socket> mSocket){
        try {
            if(mSocket!=null){
                Socket sk = mSocket.get();
                if (sk!=null&&!sk.isClosed()){
                    sk.close();
                }
                sk=null;
                mSocket=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        isRun = true;
        initSocket();
    }

    @Override
    public void onDestroy() {
        isRun = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    private class ReadThread extends Thread{
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;
        ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<>(socket);
        }
        void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                try {
                    Scanner in = new Scanner(socket.getInputStream());
                    while (!socket.isClosed() && !socket.isInputShutdown()
                            && isStart) {
                        if (in.hasNextLine()) {
                            String message = in.nextLine();
                            JSONObject json;
                            try {
                                json = new JSONObject(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtils.show("服务器出错！");
                                break;
                            }
                            int type = json.getInt("type");
                            if (type != HB) {
                                mBind.callBack.receive(type,json);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SocketBind extends Binder{
        private CallBack callBack;

        public void setCallBack(CallBack callBack) {
            this.callBack = callBack;
        }

        public void sendCreate(String roomName, String roomPwd){
            JSONObject json = new JSONObject();
            try {
                json.put("type", CREATE);
                json.put("room_name", roomName);
                json.put("room_pwd", roomPwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendBan(String name,int state){
            JSONObject json = new JSONObject();
            try {
                json.put("type", BAN);
                json.put("name", name);
                json.put("state", state);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendKick(String name){
            JSONObject json = new JSONObject();
            try {
                json.put("type", KICK);
                json.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendEnter(String name,String roomName,String roomPwd){
            JSONObject json = new JSONObject();
            try {
                json.put("type", ENTER);
                json.put("room_name", roomName);
                json.put("room_pwd", roomPwd);
                json.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendRoll(String rollName,int point){
            JSONObject json = new JSONObject();
            try {
                json.put("type", ROLL);
                json.put("roll_type",ROLL_SKILL);
                json.put("roll_name", rollName);
                json.put("point", point);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendRoll(String rollName,String formula){
            JSONObject json = new JSONObject();
            try {
                json.put("type", ROLL);
                json.put("roll_type",ROLL_CUSTOM);
                json.put("roll_name", rollName);
                json.put("roll_formula", formula);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendKPRoll(String rollName,String formula){
            JSONObject json = new JSONObject();
            try {
                json.put("type", ROLL);
                json.put("roll_type", ROLL_CUSTOM_KP);
                json.put("roll_name", rollName);
                json.put("roll_formula", formula);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendCLOSE(){
            send(CLOSE_STR);
        }
        public void sendStart(String title) {
            JSONObject json = new JSONObject();
            try {
                json.put("type", START);
                json.put("title", title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
        public void sendEnd(){
            send(END_STR);
        }
        public void sendMsg(String msg){
            JSONObject json = new JSONObject();
            try {
                json.put("type", MSG);
                json.put("msg", msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send(json.toString());
        }
    }

    public interface CallBack {
        void receive(int type,JSONObject object);
        String restart();
    }
}
