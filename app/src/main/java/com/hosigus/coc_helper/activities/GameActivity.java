package com.hosigus.coc_helper.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.RecordRecycleAdapter;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.items.Record;
import com.hosigus.coc_helper.services.SocketService;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.utils.JSONUtils;
import com.hosigus.coc_helper.utils.ToastUtils;
import com.hosigus.coc_helper.views.dialogs.InputInfoDialog;
import com.hosigus.coc_helper.views.dialogs.ShowInvestigatorDialog;
import com.hosigus.coc_helper.views.dialogs.ShowRecordDialog;

import static com.hosigus.coc_helper.configs.NetConfig.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivityTest";

    public static final int KP = 1;
    public static final int PC = 2;

    private int mType;
    private Investigator i;
    private String roomName;

    private ProgressDialog waitDialog;
    private InputInfoDialog inputInfoDialog;
    private ShowRecordDialog recordDialog;

    private Handler mHandler = new Handler();
    private RecordRecycleAdapter adapter;
    private SocketService.SocketBind socketBind;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketBind= (SocketService.SocketBind) service;
            socketBind.setCallBack(new SocketService.CallBack() {
                @Override
                public void receive(int type, JSONObject json) {mHandler.post(()->{receiveMsg(type,json);});}

                @Override
                public String restart() {
                    return null;
                    // TODO: 2018/3/12  
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketBind=null;
        }
    };

    private void receiveMsg(int type, JSONObject json) {
        switch (type){
            case CLOSE: {
                Record record = new Record();
                record.setType(Record.TYPE_NOTICE);
                record.setDetail(getTimeTip() + "KP已关上房门");
                adapter.addRecord(record);
                return;
            }
            case KICK:{
                Record record=new Record();
                record.setType(Record.TYPE_NOTICE);
                record.setDetail(getTimeTip()+"KP将 "+ JSONUtils.parserStr(json, "name") +" 踢出了房间");
                adapter.addRecord(record);
                return;
            }
            case BAN:{
                Record record=new Record();
                record.setType(Record.TYPE_NOTICE);
                record.setDetail(getTimeTip()+"KP禁止了 "+ JSONUtils.parserStr(json, "name") +" 发言");
                adapter.addRecord(record);
                return;
            }
            case ROLL: {
                int rType = Integer.parseInt(JSONUtils.parserStr(json, "roll_type"));
                if (rType == ROLL_CUSTOM_KP) {
                    if (mType == PC)
                        recordDialog.addDetail("KP悄悄的扔了一个暗骰");
                    else if (mType == KP)
                        recordDialog.addDetail("KP投掷了: " + JSONUtils.parserStr(json, "roll_name") + " " + JSONUtils.parserStr(json, "point_result"));
                    return;
                }
                StringBuilder sb = new StringBuilder(getTimeTip());
                String pr = JSONUtils.parserStr(json, "point_result");
                sb.append(JSONUtils.parserStr(json, "name")).append(" 投掷了: ").append(JSONUtils.parserStr(json, "roll_name")).append(" ");
                if (rType == ROLL_SKILL) {
                    String skillP = JSONUtils.parserStr(json, "point");
                    sb.append("(").append(skillP).append(") : ").append(pr).append(" ( ");
                    int r = Integer.valueOf(pr), p = Integer.valueOf(skillP);
                    if (r > 95)
                        sb.append("大失败!");
                    else if (r < 6)
                        sb.append("大成功!");
                    else if (r <= p / 5)
                        sb.append("极难成功");
                    else if (r <= p / 2)
                        sb.append("困难成功");
                    else if (r <= p)
                        sb.append("成功");
                    else
                        sb.append("失败");
                    sb.append(" )");
                } else
                    sb.append(pr);
                recordDialog.addDetail(sb.toString());
                break;
            }
            case OUT:{
                String name = JSONUtils.parserStr(json, "name");
                if (name.equals("KP")) {
                    new AlertDialog.Builder(this)
                            .setTitle("KP融毁了房间")
                            .setMessage("已升起的或会沉没，已沉没的或会升起")
                            .setPositiveButton("献祭", (dialog, which) -> finish())
                            .show();
                }else {
                    Record record = new Record();
                    record.setType(Record.TYPE_NOTICE);
                    record.setDetail(getTimeTip()+name+"离开了仪式");
                    adapter.addRecord(record);
                }
                break;
            }
            case ERROR:{
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("已断开连接")
                            .setMessage("在永恒的宅邸拉莱耶中,长眠的克苏鲁候汝入梦")
                            .setPositiveButton("长眠", (d, w) -> finish())
                            .show();
                }
                break;
            }
            case MSG:{
                recordDialog.addDetail(JSONUtils.parserStr(json,"name")+"："+JSONUtils.parserStr(json,"msg"));
                break;
            }
        }

        if (mType == PC)
            dealPCMsg(type, json);
        else if (mType==KP)
            dealKPMsg(type,json);
    }

    private void dealPCMsg(int type, JSONObject json) {
        switch (type){
            case ENTER: {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                    ToastUtils.show(JSONUtils.parserStr(json, "message"));
                    if (JSONUtils.parserStr(json, "result").equals("true")) {
                        initView();
                        inputInfoDialog.dismiss();
                    }
                } else {
                    Record record = new Record();
                    record.setType(Record.TYPE_NOTICE);
                    record.setDetail(getTimeTip() + JSONUtils.parserStr(json, "name") + "加入了仪式");
                    adapter.addRecord(record);
                }
                break;
            }
            case START:{
                recordDialog = new ShowRecordDialog(this, PC);
                Record record = new Record();
                record.setTitle(JSONUtils.parserStr(json,"title"));
                record.setDetail(getTimeTip() + "KP创建了" + record.getTitle());
                recordDialog.setRollCallBack(new ShowRecordDialog.PCBtnListener() {
                    @Override
                    public void rollSkill(String name, int point) {
                        socketBind.sendRoll(name,point);
                    }

                    @Override
                    public void rollCustom(String hint, String formula) {
                        socketBind.sendRoll(hint,formula);
                    }

                    @Override
                    public void sendMsg(String msg) {
                        socketBind.sendMsg(msg);
                    }
                });
                recordDialog.setInvestigator(i);
                recordDialog.setRecord(record);
                recordDialog.show();
                break;
            }
            case END:{
                if (recordDialog.isShowing()){
                    adapter.addRecord(recordDialog.getRecord());
                    recordDialog.dismiss();
                }
                break;
            }
        }
    }

    private void dealKPMsg(int type, JSONObject json) {
        switch (type){
            case CREATE:{
                waitDialog.dismiss();
                if (JSONUtils.parserStr(json, "result").equals("true")){
                    initView();
                    inputInfoDialog.dismiss();
                }
                ToastUtils.show(JSONUtils.parserStr(json,"message"));
                break;
            }
            case ENTER:{
                Record record = new Record();
                record.setType(Record.TYPE_NOTICE);
                record.setDetail(getTimeTip() + JSONUtils.parserStr(json, "name") + "加入了仪式");
                adapter.addRecord(record);
                break;
            }
            case START:{
                waitDialog.dismiss();
                recordDialog = new ShowRecordDialog(this, KP);
                recordDialog.setKPBtnListener(new ShowRecordDialog.KPBtnListener() {
                    @Override
                    public void onFinish() {
                        socketBind.sendEnd();
                        adapter.addRecord(recordDialog.getRecord());
                        recordDialog.dismiss();
                    }
                    @Override
                    public void rollDark(String hint, String formula) {
                        socketBind.sendKPRoll(hint,formula);
                    }
                    @Override
                    public void roll(String hint, String formula) {
                        socketBind.sendRoll(hint, formula);
                    }
                    @Override
                    public void sendMsg(String msg) {
                        socketBind.sendMsg(msg);
                    }
                });
                Record record = new Record();
                record.setTitle(JSONUtils.parserStr(json,"title"));
                record.setDetail(getTimeTip() + "KP创建了" + record.getTitle());
                recordDialog.setRecord(record);
                inputInfoDialog.dismiss();
                recordDialog.show();
                break;
            }
        }
    }

    private void onSocketService(){
        startService(new Intent(this, SocketService.class));
        bindService(new Intent(this,SocketService.class),connection,BIND_AUTO_CREATE);
    }
    private void offSocketService(){
        unbindService(connection);
        stopService(new Intent(this, SocketService.class));
    }

    private void initDialogs() {
        waitDialog = new ProgressDialog(this);
        waitDialog.setCancelable(false);
        inputInfoDialog = new InputInfoDialog(this,(String room_name, String roomPwd)-> {
                if (mType==KP){
                    waitDialog.setTitle("正在准备仪式……");
                    waitDialog.setMessage("克总发糖中……");
                    socketBind.sendCreate(room_name,roomPwd);
                } else if (mType == PC) {
                    waitDialog.setTitle("正在加入仪式……");
                    waitDialog.setMessage("检验门之匙……");
                    socketBind.sendEnter(i.getName(),room_name,roomPwd);
                }
                waitDialog.show();
                roomName = room_name;
            }, InputInfoDialog.TYPE_ROOM);
        inputInfoDialog.setBackListener(()->{finish();return true;});
        inputInfoDialog.show();
    }

    private void initView(){
        setContentView(R.layout.activity_game);
        Button gameBtn = findViewById(R.id.btn_game);
        Toolbar toolbar = findViewById(R.id.tb_toolbar);

        RecyclerView rv = findViewById(R.id.rv_game);
        rv.setLayoutManager(new LinearLayoutManager(this));
        List<Record> recordList = new ArrayList<>();
        Record r = new Record();

        if (mType == KP) {
            gameBtn.setText("骰子空间");
            gameBtn.setOnClickListener(v->{
                waitDialog.setTitle("正在创建骰子空间……");
                waitDialog.setMessage("召唤骰子娘中……");
                inputInfoDialog = new InputInfoDialog(this,(String title, String info2)->{
                    socketBind.sendStart(title);
                    waitDialog.show();
                },InputInfoDialog.TYPE_SPACE);
                inputInfoDialog.show();
            });
            toolbar.setTitle("KP : "+roomName);
            r.setDetail(getTimeTip()+"KP创建了仪式 "+roomName);
        }else if (mType == PC){
            gameBtn.setText("查看人物卡");
            gameBtn.setOnClickListener(v->{
                new  ShowInvestigatorDialog(this, i);
            });
            toolbar.setTitle("PC : "+i.getName()+"  "+roomName);
            r.setDetail(getTimeTip()+"PC "+i.getName()+" 加入了仪式 "+roomName);
        }
        setSupportActionBar(toolbar);

        r.setType(Record.TYPE_NOTICE);
        recordList.add(r);
        adapter=new RecordRecycleAdapter(recordList,(record -> {
            recordDialog = new ShowRecordDialog(this, -1);
            recordDialog.setRecord(record);
            recordDialog.show();
        }));

        rv.setAdapter(adapter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // TODO: 2018/2/27  menuSelect
    private void onPCItemSelected(int itemId) {
    }
    private void onKPItemSelected(int itemId) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra=this.getIntent().getExtras();
        if (extra == null) {
            finish();
            return;
        }
        mType=extra.getInt("type");
        if (mType == PC) {
            i = COCUtils.selectInvestigatorById(extra.getInt("iId"));
        }
        onSocketService();

        initDialogs();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mType == KP)
            getMenuInflater().inflate(R.menu.game_kp,menu);
        else if (mType == PC)
            getMenuInflater().inflate(R.menu.game_pc,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mType == KP)
            onKPItemSelected(item.getItemId());
        else if (mType == PC)
            onPCItemSelected(item.getItemId());

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        offSocketService();
        super.onDestroy();
    }

    public String getTimeTip() {
        Calendar calendar = Calendar.getInstance();
        return " ( "+calendar.get(Calendar.HOUR_OF_DAY)
                +":"+calendar.get(Calendar.MINUTE)
                +":"+calendar.get(Calendar.SECOND)+" ) ";
    }
}
