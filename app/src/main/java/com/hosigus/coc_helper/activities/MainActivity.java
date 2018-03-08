package com.hosigus.coc_helper.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.MainPagerAdapter;
import com.hosigus.coc_helper.configs.NetConfig;
import com.hosigus.coc_helper.configs.PermissionConstants;
import com.hosigus.coc_helper.fragments.HomePageFragment;
import com.hosigus.coc_helper.fragments.InvestigatorPageFragment;
import com.hosigus.coc_helper.fragments.ModulePageFragment;
import com.hosigus.coc_helper.items.Investigator;
import com.hosigus.coc_helper.items.JSONResult;
import com.hosigus.coc_helper.utils.COCUtils;
import com.hosigus.coc_helper.utils.DownloadTask;
import com.hosigus.coc_helper.utils.FileUtils;
import com.hosigus.coc_helper.utils.NetConnectUtils;
import com.hosigus.coc_helper.utils.ToastUtils;
import com.hosigus.coc_helper.views.dialogs.AddInvestigatorDialog;
import com.hosigus.coc_helper.views.dialogs.AddStoryDialog;
import com.hosigus.coc_helper.views.FoldFabGroup;
import com.hosigus.coc_helper.views.PolygonView;
import com.hosigus.coc_helper.views.dialogs.InveListDialog;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    public static final int CHOOSE_PIC = 0;

    private SharedPreferences mPreferences;

    private DrawerLayout mDrawerLayout;
    private PolygonView yummyInvestigatorPolygon;
    private AddStoryDialog addStoryDialog;
    private AddInvestigatorDialog addIDialog;
    private ProgressDialog progressDialog;
    private InveListDialog chooseIDialog;

    private Investigator yummyInvestigator;
    private MenuItem bottomMenuItem;

    public static void actionStart(Context context){
        Intent intent=new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUpdate();
        initData();
        initView();
    }

    private void checkUpdate() {
        PackageManager packageManager=getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final String finalVersionName = versionName;
        NetConnectUtils.requestNet(NetConfig.GetLastVersion, new NetConnectUtils.NetCallBack() {
            @Override
            public void connectOK(JSONResult result) {
                String version = null;
                try {
                    version = result.getData().getString("version");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                if (version.equals(finalVersionName)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("检测到有新版本,是否立即下载?")
                            .setPositiveButton("立即下载", (d, w) -> {
                                if (Build.VERSION.SDK_INT>22&&
                                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionConstants.REQUEST_STORAGE_PIC);
                                }else{
                                    downloadApk();
                                }
                            })
                            .setNegativeButton("先凑活用",(d,w)->{})
                            .create().show();
                }
            }
            @Override
            public void connectFail(String resStr) {}
        });
    }

    private void downloadApk() {
        DownloadTask task = new DownloadTask(this);
        task.execute("http://coc.api.hosigus.tech/COC_Helper.apk");
    }

    /**
     * 导入数据 项目有：
     * 1.初始化database和share preferences //database已改成由WelcomeActivity初始化了
     * 2.导入本地储存的 "mStory"记录
     * 3.导入传奇调查员序号，默认为-1
     */
    private void initData() {
        // TODO: 2018/2/11
        mPreferences=getSharedPreferences("yummyInvestigator",Context.MODE_PRIVATE);
        initSetting();
        yummyInvestigator = new Investigator();
        yummyInvestigator.setId(mPreferences.getInt("investigator_id",-1));
    }

    private void initSetting(){
        // TODO: 2018/2/25
    }
    
    /**
     * 初始化View 项目有：
     * 1.左侧传奇调查员
     * 2.FoldFab
     * 3.三个page的fragment
     * 4.homepage的2个fragment 【网络请求更新数据】
     */
    private void initView() {
        Toolbar toolbar=findViewById(R.id.tb_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout =findViewById(R.id.dl_drawer);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this, mDrawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initPages();
        initDrawer();
        initFoldFabGroup();
    }

    private void initPages() {
        ViewPager vp=findViewById(R.id.vp_main);

        BottomNavigationView navigationView=findViewById(R.id.bnv_main);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_page_home:
                    vp.setCurrentItem(0);
                    break;
                case R.id.action_page_investigator:
                    vp.setCurrentItem(1);
                    break;
                case R.id.action_page_module:
                    vp.setCurrentItem(2);
                    break;
            }
            return false;
        });

        List<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(new HomePageFragment());
        fragmentList.add(new InvestigatorPageFragment());
        fragmentList.add(new ModulePageFragment());

        vp.setOffscreenPageLimit(3);
        vp.setAdapter(new MainPagerAdapter(getSupportFragmentManager(),fragmentList));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                if (bottomMenuItem != null) {
                    bottomMenuItem.setChecked(false);
                } else {
                    navigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomMenuItem = navigationView.getMenu().getItem(position);
                bottomMenuItem.setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 初始化FoldFab
     * 开始游戏-KP：
     *      建立socket房间
     * 开始游戏-PC：
     *      加入socket房间
     */
    private void initFoldFabGroup() {
        FoldFabGroup group=findViewById(R.id.ffg_main);
        List<FoldFabGroup.OnChildViewClickListener> listeners=new ArrayList<>();
        listeners.add(()->{
            InveListDialog cid=new InveListDialog(this, i -> {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("iId", i.getId());
                intent.putExtra("type", GameActivity.PC);
                startActivity(intent);
            });
            cid.show();
        });
        listeners.add(()->{
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("type", GameActivity.KP);
            startActivity(intent);
        });
        group.setClickListeners(listeners);
    }

    /**
     * 初始化侧边栏，即传奇调查员
     */
    private void initDrawer() {
        yummyInvestigatorPolygon = findViewById(R.id.pv_nav_investigator);
        if(yummyInvestigator.getId()!=-1){
            initYummyIPolygon();
        }else {
            initLovecraftPolygon();
        }
        Button changeBtn = findViewById(R.id.btn_choose_yi);
        chooseIDialog=new InveListDialog(this, i->{
            SharedPreferences.Editor editor= mPreferences.edit();
            editor.putInt("investigator_id",i.getId());
            editor.apply();
            yummyInvestigator = i;
            initYummyIPolygon();
        });
        changeBtn.setOnClickListener(v-> chooseIDialog.show());
    }

    private void initYummyIPolygon() {
        ImageView headImage=findViewById(R.id.iv_nav_head);
        TextView nameText=findViewById(R.id.tv_nav_name);
        TextView detailText = findViewById(R.id.tv_nav_detail);
        yummyInvestigator= COCUtils.selectInvestigatorById(yummyInvestigator.getId());
        FileUtils.readBitmapInto(headImage, R.drawable.ic_default_head,yummyInvestigator.getName() + yummyInvestigator.getId());
        nameText.setText(yummyInvestigator.getName());
        String str="现居"+yummyInvestigator.getAddress()+"的"+yummyInvestigator.getAge()+"岁"+yummyInvestigator.getProfession().getName();
        detailText.setText(str);
        List<Integer> pointList = yummyInvestigator.getAttributes().getAttAsList();
        List<Float> pointValue = new ArrayList<>();
        for (int point : pointList) {
            pointValue.add(point / 100f);
        }
        yummyInvestigatorPolygon.setPointValue(pointValue);
        yummyInvestigatorPolygon.draw();
    }

    private void initLovecraftPolygon(){
        TextView nameText=findViewById(R.id.tv_nav_name);
        TextView detailText = findViewById(R.id.tv_nav_detail);
        nameText.setText(R.string.name_lovecraft);
        detailText.setText(R.string.detail_lovecraft);
        List<Float> pointValue = new ArrayList<>();
        for (int i = 0, count = yummyInvestigatorPolygon.getEageCount(); i < count; i++) {
            pointValue.add((float)Math.random());
        }
        yummyInvestigatorPolygon.setPointValue(pointValue);
        yummyInvestigatorPolygon.draw();
    }

    private void initAddStoryDialog(){
        addStoryDialog=new AddStoryDialog(this, story -> {
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("正在连接服务器……");
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetConnectUtils.requestNet(NetConfig.AddStory,"title="+story.getTitle()+"&detail="+story.getDetail(),
                    new NetConnectUtils.NetCallBack() {
                        @Override
                        public void connectOK(JSONResult result) {
                            if (!result.isOK()){
                                ToastUtils.show("上传失败:"+result.getMessage());
                                progressDialog.dismiss();
                                return;
                            }
                            COCUtils.saveStory(story);

                            ToastUtils.show("上传成功!");
                            addStoryDialog.dismiss();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void connectFail(String resStr) {
                            progressDialog.dismiss();
                            ToastUtils.show("连接服务器失败"+resStr);
                        }
                    });
        });
    }
    private void initAddIDialog() {
        addIDialog=new AddInvestigatorDialog(this, () -> {
            if (Build.VERSION.SDK_INT>22&&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionConstants.REQUEST_STORAGE_PIC);
            }else{
                choosePic();
            }
        });
    }
    private void choosePic(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PIC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i:grantResults) {
            if (i!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "没有授权伦家真的什么都做不了啦", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (requestCode==PermissionConstants.REQUEST_STORAGE_PIC)
            choosePic();
        if (requestCode==PermissionConstants.REQUEST_STORAGE_DOWNLOAD);
            downloadApk();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK)
            return;
        switch (requestCode){
            case CHOOSE_PIC:
                Uri uri=data.getData();
                Crop.of(uri,Uri.fromFile(new File(FileUtils.getFilesDir(), FileUtils.encodeName("temp")))).asSquare().start(this);
                break;
            case Crop.REQUEST_CROP:
                Bitmap bitmap = FileUtils.getBitmapFromFile("temp");
                if (bitmap==null)
                    return;
                int line = bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
                DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
                float newLine= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        98, displayMetrics);
                float scale = 1f * newLine / line;
                Matrix matrix = new Matrix();
                matrix.postScale(scale,scale);
                bitmap=Bitmap.createBitmap(bitmap,0,0,line,line,matrix,true);
                FileUtils.saveBitmapToFile(bitmap,"temp");
                addIDialog.setHeadImage();
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    /**
     * 处理主页菜单点击事件
     * 1.查找调查员
     * 2.查找模组
     * 3.设置界面
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // TODO: 2018/2/11
            case R.id.action_settings:
                break;
            case R.id.action_add_story:
                initAddStoryDialog();
                addStoryDialog.show();
                break;
            case R.id.action_add_card:
                initAddIDialog();
                addIDialog.show();
                break;
            case R.id.action_add_module:
                break;
            case R.id.action_search_story:
                break;
            case R.id.action_search_card:
                break;
            case R.id.action_search_module:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}
