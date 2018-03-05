package com.hosigus.coc_helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.hosigus.coc_helper.utils.JSONUtils;
import com.hosigus.coc_helper.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hosigus.coc_helper.configs.NetConfig.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketService extends Service {
    private long sendTime = 0L;
    private long receiveTime ;

    private ReadThread mReadThread;
    private WeakReference<Socket> mSocket;

    private Handler mHander = new Handler();
    private SocketBind mBind = new SocketBind();

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if(System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE){
                sendHB();
            }
            mHander.postDelayed(this,HEART_BEAT_RATE);
        }
    };

    private void sendHB() {
        if(System.currentTimeMillis()-receiveTime>NOT_RESPONSE_LIMIT)
            restartSocket();
        send(HB_STR);
    }
    private void send(String msg) {
        if (mSocket==null || mSocket.get()==null)
            restartSocket();
        Socket soc = mSocket.get();
        if (soc.isClosed() && !soc.isOutputShutdown()) {restartSocket();}
        new Thread(()->{
            try {
                OutputStream os = soc.getOutputStream();
                os.write((msg+"\n").getBytes("UTF-8"));
                os.flush();
                sendTime = System.currentTimeMillis();//发送后更改心跳时间
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void initSocket(){
        new Thread(()->{
            try {
                receiveTime=System.currentTimeMillis();
                Socket socket=new Socket(HOST,PORT);
                mSocket= new WeakReference<>(socket);
                mReadThread=new ReadThread(socket);
                mReadThread.start();
                mHander.postDelayed(heartBeatRunnable,HEART_BEAT_RATE);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void restartSocket(){
        mHander.removeCallbacks(heartBeatRunnable);
        mReadThread.release();
        releaseLastSocket(mSocket);
        initSocket();
    }
    private void releaseLastSocket(WeakReference<Socket> mSocket){
        try {
            if(mSocket!=null){
                Socket sk = mSocket.get();
                if (!sk.isClosed()){
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
        initSocket();
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
                            receiveTime = System.currentTimeMillis();//收到信息，更新间隔时间
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
                                mBind.onReceiveMsg.receive(type,json);
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
        private OnReceiveMsg onReceiveMsg;

        public void setOnReceiveMsg(OnReceiveMsg onReceiveMsg) {
            this.onReceiveMsg = onReceiveMsg;
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
    }

    public interface OnReceiveMsg{
        void receive(int type,JSONObject object);
    }
}
