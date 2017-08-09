package com.ss.www.webservicetest;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ss.www.webservicetest.bean.Model;
import com.ss.www.webservicetest.utils.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView mRemember,show_mRemember,show_mTime;
    private TextView mChannel1_1, mChannel1_2, mChannel1_3, mChannel2_1, mChannel2_2, mChannel2_3, mChannel3_1, mChannel3_2, mChannel3_3,
            mChannel4_1, mChannel4_2, mChannel4_3, mChannel5_1, mChannel5_2, mChannel5_3, mChannel6_1, mChannel6_2, mChannel6_3,
            mChannel7_1, mChannel7_2, mChannel7_3, mChannel8_1, mChannel8_2, mChannel8_3, mChannel9_1, mChannel9_2, mChannel9_3,
            mChannel10_1, mChannel10_2, mChannel10_3, mChannel11_1, mChannel11_2, mChannel11_3, mChannel12_1, mChannel12_2, mChannel12_3,
            mChannel13_1, mChannel13_2, mChannel13_3, mChannel14_1, mChannel14_2, mChannel14_3, mChannel15_1, mChannel15_2, mChannel15_3,
            mChannel16_1, mChannel16_2, mChannel16_3, mChannel17_1, mChannel17_2, mChannel17_3, mChannel18_1, mChannel18_2, mChannel18_3,
            mChannel19_1, mChannel19_2, mChannel19_3, mChannel20_1, mChannel20_2, mChannel20_3, mChannel21_1, mChannel21_2, mChannel21_3,
            mChannel22_1, mChannel22_2, mChannel22_3, mChannel23_1, mChannel23_2, mChannel23_3, mChannel24_1, mChannel24_2, mChannel24_3,
            mChannel25_1, mChannel25_2, mChannel25_3, mChannel26_1, mChannel26_2, mChannel26_3, mChannel27_1, mChannel27_2, mChannel27_3,
            mChannel28_1, mChannel28_2, mChannel28_3, mChannel29_1, mChannel29_2, mChannel29_3, mChannel30_1, mChannel30_2, mChannel30_3,
            mChannel31_1, mChannel31_2, mChannel31_3, mChannel32_1, mChannel32_2, mChannel32_3, mChannel33_1, mChannel33_2, mChannel33_3,
            mChannel34_1, mChannel34_2, mChannel34_3, mChannel35_1, mChannel35_2, mChannel35_3, mChannel36_1, mChannel36_2, mChannel36_3;
    private Button mButton;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private List<Model> mList;
    private List<TextView> mListTextView;
    private String time_flag = "123";
    private boolean flag;
    private Toolbar toolbar;
    private ProgressBar mProgressBar;
    private int time_count;
    private int dec = 120;
    public static final String WSDL_URI = "http://www.xl-iot.com:9016/WXHQWebs/WxHqWebService.asmx?WSDL";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_NAME = "RTDAGetData";
    private static final int WAIT_FLAG = 4;
    private static final int COUNT_FLAG = 5;
    private static final int CLOSE_PG = 6;
    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            // Looper.prepare();
            if(flag==true){
                time_count++;
                dec--;
                Message m2 = Message.obtain();
                m2.what = COUNT_FLAG;
                m2.arg1 = dec;
                handler.sendMessage(m2);
                // Log.i("main---time_count",time_count+"");
                // Log.i("main---thread",Thread.currentThread().getName()+"");
            }
            if(time_count ==10){
                Message a = Message.obtain();
                a.what = CLOSE_PG;
                handler.sendMessage(a);
            }
            if(time_count >= 120){
                //mProgressBar.setVisibility(View.VISIBLE);
                time_count = 0;
                dec = 120;
                if(Looper.myLooper() == null){
                    Looper.prepare();
                }
                WebServiceUtils.call3(WSDL_URI,NAMESPACE,METHOD_NAME,3,time_flag,handler);
                Message m = Message.obtain();
                m.what = WAIT_FLAG;
                //m.arg1 = 10;
                handler.sendMessage(m);
                Looper.myLooper().quit();
                Looper.loop();
            }
        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case WebServiceUtils.SUCCESS_FLAG :
                    mProgressBar.setVisibility(View.GONE);
                    SoapObject soapObject = (SoapObject) msg.obj;
                    String str = String.valueOf(soapObject);
                    if(str.contains("-3")&&str.length()<50){
                        Toast.makeText(MainActivity.this,"数据相同，请稍后请求",Toast.LENGTH_SHORT).show();
                    }
                    if(str.contains("null")){
                        Toast.makeText(MainActivity.this,"无数据",Toast.LENGTH_SHORT).show();
                    }
                    if(str.contains("-1")&&str.length()<50){
                        //Toast.makeText(MainActivity.this,"参数有误",Toast.LENGTH_SHORT).show();
                    }
                    if(str.contains("-5")&&str.length()<50){
                        Toast.makeText(MainActivity.this,"数据库没有数据",Toast.LENGTH_SHORT).show();
                    }
                    if(str.length() > 50){
                        getJSON(str);
                    }
                    break;
                case WebServiceUtils.ERROR_FLAG:
                    mProgressBar.setVisibility(View.GONE);
                    Exception e = (Exception) msg.obj;
                    String str2 = String.valueOf(e);
                    Log.i("main---Exception",str2);
                    //Log.i("main---Exception",str2);
                    if(str2.contains("UnknownHostException")){
                        Toast.makeText(MainActivity.this,"没有打开网络或服务器关闭",Toast.LENGTH_SHORT).show();
                    }
                    if(str2.contains("SocketTimeoutException")){
                        Toast.makeText(MainActivity.this,"连接服务器异常",Toast.LENGTH_SHORT).show();
                    }
                    if(str2.contains("ConnectException")){
                        Toast.makeText(MainActivity.this,"域名解析异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case WAIT_FLAG:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case COUNT_FLAG:
                    int s = msg.arg1;
                    mRemember.setText(s+"");
                    break;
                case CLOSE_PG:
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListTextView = new ArrayList<>();
        mList = new ArrayList<>();
        initData();//初始化控件
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,toolbar, 0, 0);//显示左侧图标
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mTimer = new Timer();
        mTimer.schedule(mTimerTask,1000,1000);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                WebServiceUtils.call2(WSDL_URI, NAMESPACE, METHOD_NAME, 3, time_flag, new WebServiceUtils.Response() {
                    @Override
                    public void onSuccess(SoapObject result) {

                        mProgressBar.setVisibility(View.GONE);
                        String z = String.valueOf(result);
                       // Log.i("main---result",z);
                        if(z.contains("-3")&&z.length()<50){
                            Toast.makeText(MainActivity.this,"数据相同，请稍后请求",Toast.LENGTH_SHORT).show();
                        }
                        if(z.contains("-1")&&z.length()<50){
                            //Toast.makeText(MainActivity.this,"参数有误",Toast.LENGTH_SHORT).show();
                        }
                        if(z.contains("-5")&&z.length()<50){
                            Toast.makeText(MainActivity.this,"数据库没有数据",Toast.LENGTH_SHORT).show();
                        }
                        if(z.length() > 50){
                            getJSON(z);
                        }
                        if(z.contains("null")){
                            Toast.makeText(MainActivity.this,"很遗憾，返回数据为空",Toast.LENGTH_SHORT).show();
                        }
                        // Log.i("main---result2",String.valueOf(result));
                        //Log.i("main---result2",String.valueOf(result).length()+"");
                        //getJSON(String.valueOf(result));
                    }
                    @Override
                    public void onError(Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.i("main---Exception",e.toString());
                        if(e.toString().contains("UnknownHostException")){
                            Toast.makeText(MainActivity.this,"没有打开网络或服务器关闭",Toast.LENGTH_SHORT).show();
                        }
                        if(e.toString().contains("SocketTimeoutException")){
                            Toast.makeText(MainActivity.this,"服务器连接异常",Toast.LENGTH_SHORT).show();
                        }
                        if(e.toString().contains("ConnectException")){
                            Toast.makeText(MainActivity.this,"域名解析异常",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mButton.setEnabled(false);
                mButton.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mButton.setEnabled(true);
                        mButton.setVisibility(View.VISIBLE);
                    }
                },2000);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
        }
    }
    private void initData(){
        mListTextView.clear();
        mProgressBar = (ProgressBar)findViewById(R.id.waiting);
        toolbar = (Toolbar) findViewById(R.id.mToolBar);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mButton = (Button) findViewById(R.id.mUpdate);
        mRemember = (TextView) findViewById(R.id.show_count);
        show_mRemember = (TextView) findViewById(R.id.show_context);
        show_mTime = (TextView) findViewById(R.id.show_time);
        mDrawer = (DrawerLayout) findViewById(R.id.mDrawer);
        mChannel1_1 = (TextView) findViewById(R.id.channel1_1);mChannel1_2 = (TextView) findViewById(R.id.channel1_2);mChannel1_3 = (TextView) findViewById(R.id.channel1_3);
        mChannel2_1 = (TextView) findViewById(R.id.channel2_1);mChannel2_2 = (TextView) findViewById(R.id.channel2_2);mChannel2_3 = (TextView) findViewById(R.id.channel2_3);
        mChannel3_1 = (TextView) findViewById(R.id.channel3_1);mChannel3_2 = (TextView) findViewById(R.id.channel3_2);mChannel3_3 = (TextView) findViewById(R.id.channel3_3);
        mChannel4_1 = (TextView) findViewById(R.id.channel4_1);mChannel4_2 = (TextView) findViewById(R.id.channel4_2);mChannel4_3 = (TextView) findViewById(R.id.channel4_3);
        mChannel5_1 = (TextView) findViewById(R.id.channel5_1);mChannel5_2 = (TextView) findViewById(R.id.channel5_2);mChannel5_3 = (TextView) findViewById(R.id.channel5_3);
        mChannel6_1 = (TextView) findViewById(R.id.channel6_1);mChannel6_2 = (TextView) findViewById(R.id.channel6_2);mChannel6_3 = (TextView) findViewById(R.id.channel6_3);
        mChannel7_1 = (TextView) findViewById(R.id.channel7_1);mChannel7_2 = (TextView) findViewById(R.id.channel7_2);mChannel7_3 = (TextView) findViewById(R.id.channel7_3);
        mChannel8_1 = (TextView) findViewById(R.id.channel8_1);mChannel8_2 = (TextView) findViewById(R.id.channel8_2);mChannel8_3 = (TextView) findViewById(R.id.channel8_3);
        mChannel9_1 = (TextView) findViewById(R.id.channel9_1);mChannel9_2 = (TextView) findViewById(R.id.channel9_2);mChannel9_3 = (TextView) findViewById(R.id.channel9_3);
        mChannel10_1 = (TextView) findViewById(R.id.channel10_1);mChannel10_2 = (TextView) findViewById(R.id.channel10_2);mChannel10_3 = (TextView) findViewById(R.id.channel10_3);
        mChannel11_1 = (TextView) findViewById(R.id.channel11_1);mChannel11_2 = (TextView) findViewById(R.id.channel11_2);mChannel11_3 = (TextView) findViewById(R.id.channel11_3);
        mChannel12_1 = (TextView) findViewById(R.id.channel12_1);mChannel12_2 = (TextView) findViewById(R.id.channel12_2);mChannel12_3 = (TextView) findViewById(R.id.channel12_3);
        mChannel13_1 = (TextView) findViewById(R.id.channel13_1);mChannel13_2 = (TextView) findViewById(R.id.channel13_2);mChannel13_3 = (TextView) findViewById(R.id.channel13_3);
        mChannel14_1 = (TextView) findViewById(R.id.channel14_1);mChannel14_2 = (TextView) findViewById(R.id.channel14_2);mChannel14_3 = (TextView) findViewById(R.id.channel14_3);
        mChannel15_1 = (TextView) findViewById(R.id.channel15_1);mChannel15_2 = (TextView) findViewById(R.id.channel15_2);mChannel15_3 = (TextView) findViewById(R.id.channel15_3);
        mChannel16_1 = (TextView) findViewById(R.id.channel16_1);mChannel16_2 = (TextView) findViewById(R.id.channel16_2);mChannel16_3 = (TextView) findViewById(R.id.channel16_3);
        mChannel17_1 = (TextView) findViewById(R.id.channel17_1);mChannel17_2 = (TextView) findViewById(R.id.channel17_2);mChannel17_3 = (TextView) findViewById(R.id.channel17_3);
        mChannel18_1 = (TextView) findViewById(R.id.channel18_1);mChannel18_2 = (TextView) findViewById(R.id.channel18_2);mChannel18_3 = (TextView) findViewById(R.id.channel18_3);
        mChannel19_1 = (TextView) findViewById(R.id.channel19_1);mChannel19_2 = (TextView) findViewById(R.id.channel19_2);mChannel19_3 = (TextView) findViewById(R.id.channel19_3);
        mChannel20_1 = (TextView) findViewById(R.id.channel20_1);mChannel20_2 = (TextView) findViewById(R.id.channel20_2);mChannel20_3 = (TextView) findViewById(R.id.channel20_3);
        mChannel21_1 = (TextView) findViewById(R.id.channel21_1);mChannel21_2 = (TextView) findViewById(R.id.channel21_2);mChannel21_3 = (TextView) findViewById(R.id.channel21_3);
        mChannel22_1 = (TextView) findViewById(R.id.channel22_1);mChannel22_2 = (TextView) findViewById(R.id.channel22_2);mChannel22_3 = (TextView) findViewById(R.id.channel22_3);
        mChannel23_1 = (TextView) findViewById(R.id.channel23_1);mChannel23_2 = (TextView) findViewById(R.id.channel23_2);mChannel23_3 = (TextView) findViewById(R.id.channel23_3);
        mChannel24_1 = (TextView) findViewById(R.id.channel24_1);mChannel24_2 = (TextView) findViewById(R.id.channel24_2);mChannel24_3 = (TextView) findViewById(R.id.channel24_3);
        mChannel25_1 = (TextView) findViewById(R.id.channel25_1);mChannel25_2 = (TextView) findViewById(R.id.channel25_2);mChannel25_3 = (TextView) findViewById(R.id.channel25_3);
        mChannel26_1 = (TextView) findViewById(R.id.channel26_1);mChannel26_2 = (TextView) findViewById(R.id.channel26_2);mChannel26_3 = (TextView) findViewById(R.id.channel26_3);
        mChannel27_1 = (TextView) findViewById(R.id.channel27_1);mChannel27_2 = (TextView) findViewById(R.id.channel27_2);mChannel27_3 = (TextView) findViewById(R.id.channel27_3);
        mChannel28_1 = (TextView) findViewById(R.id.channel28_1);mChannel28_2 = (TextView) findViewById(R.id.channel28_2);mChannel28_3 = (TextView) findViewById(R.id.channel28_3);
        mChannel29_1 = (TextView) findViewById(R.id.channel29_1);mChannel29_2 = (TextView) findViewById(R.id.channel29_2);mChannel29_3 = (TextView) findViewById(R.id.channel29_3);
        mChannel30_1 = (TextView) findViewById(R.id.channel30_1);mChannel30_2 = (TextView) findViewById(R.id.channel30_2);mChannel30_3 = (TextView) findViewById(R.id.channel30_3);
        mChannel31_1 = (TextView) findViewById(R.id.channel31_1);mChannel31_2 = (TextView) findViewById(R.id.channel31_2);mChannel31_3 = (TextView) findViewById(R.id.channel31_3);
        mChannel32_1 = (TextView) findViewById(R.id.channel32_1);mChannel32_2 = (TextView) findViewById(R.id.channel32_2);mChannel32_3 = (TextView) findViewById(R.id.channel32_3);
        mChannel33_1 = (TextView) findViewById(R.id.channel33_1);mChannel33_2 = (TextView) findViewById(R.id.channel33_2);mChannel33_3 = (TextView) findViewById(R.id.channel33_3);
        mChannel34_1 = (TextView) findViewById(R.id.channel34_1);mChannel34_2 = (TextView) findViewById(R.id.channel34_2);mChannel34_3 = (TextView) findViewById(R.id.channel34_3);
        mChannel35_1 = (TextView) findViewById(R.id.channel35_1);mChannel35_2 = (TextView) findViewById(R.id.channel35_2);mChannel35_3 = (TextView) findViewById(R.id.channel35_3);
        mChannel36_1 = (TextView) findViewById(R.id.channel36_1);mChannel36_2 = (TextView) findViewById(R.id.channel36_2);mChannel36_3 = (TextView) findViewById(R.id.channel36_3);
        mListTextView.add( mChannel1_1);mListTextView.add( mChannel1_2);mListTextView.add( mChannel1_3);mListTextView.add( mChannel2_1);mListTextView.add( mChannel2_2);mListTextView.add( mChannel2_3);
        mListTextView.add( mChannel3_1);mListTextView.add( mChannel3_2);mListTextView.add( mChannel3_3);mListTextView.add( mChannel4_1);mListTextView.add( mChannel4_2);mListTextView.add( mChannel4_3);
        mListTextView.add( mChannel5_1);mListTextView.add( mChannel5_2);mListTextView.add( mChannel5_3);mListTextView.add( mChannel6_1);mListTextView.add( mChannel6_2);mListTextView.add( mChannel6_3);
        mListTextView.add( mChannel7_1);mListTextView.add( mChannel7_2);mListTextView.add( mChannel7_3);mListTextView.add( mChannel8_1);mListTextView.add( mChannel8_2);mListTextView.add( mChannel8_3);
        mListTextView.add( mChannel9_1);mListTextView.add( mChannel9_2);mListTextView.add( mChannel9_3);mListTextView.add( mChannel10_1);mListTextView.add( mChannel10_2);mListTextView.add( mChannel10_3);
        mListTextView.add( mChannel11_1);mListTextView.add( mChannel11_2);mListTextView.add( mChannel11_3);mListTextView.add( mChannel12_1);mListTextView.add( mChannel12_2);mListTextView.add( mChannel12_3);
        mListTextView.add( mChannel13_1);mListTextView.add( mChannel13_2);mListTextView.add( mChannel13_3);mListTextView.add( mChannel14_1);mListTextView.add( mChannel14_2);mListTextView.add( mChannel14_3);
        mListTextView.add( mChannel15_1);mListTextView.add( mChannel15_2);mListTextView.add( mChannel15_3);mListTextView.add( mChannel16_1);mListTextView.add( mChannel16_2);mListTextView.add( mChannel16_3);
        mListTextView.add( mChannel17_1);mListTextView.add( mChannel17_2);mListTextView.add( mChannel17_3);mListTextView.add( mChannel18_1);mListTextView.add( mChannel18_2);mListTextView.add( mChannel18_3);
        mListTextView.add( mChannel19_1);mListTextView.add( mChannel19_2);mListTextView.add( mChannel19_3);mListTextView.add( mChannel20_1);mListTextView.add( mChannel20_2);mListTextView.add( mChannel20_3);
        mListTextView.add( mChannel21_1);mListTextView.add( mChannel21_2);mListTextView.add( mChannel21_3);mListTextView.add( mChannel22_1);mListTextView.add( mChannel22_2);mListTextView.add( mChannel22_3);
        mListTextView.add( mChannel23_1);mListTextView.add( mChannel23_2);mListTextView.add( mChannel23_3);mListTextView.add( mChannel24_1);mListTextView.add( mChannel24_2);mListTextView.add( mChannel24_3);
        mListTextView.add( mChannel25_1);mListTextView.add( mChannel25_2);mListTextView.add( mChannel25_3);mListTextView.add( mChannel26_1);mListTextView.add( mChannel26_2);mListTextView.add( mChannel26_3);
        mListTextView.add( mChannel27_1);mListTextView.add( mChannel27_2);mListTextView.add( mChannel27_3);mListTextView.add( mChannel28_1);mListTextView.add( mChannel28_2);mListTextView.add( mChannel28_3);
        mListTextView.add( mChannel29_1);mListTextView.add( mChannel29_2);mListTextView.add( mChannel29_3);mListTextView.add( mChannel30_1);mListTextView.add( mChannel30_2);mListTextView.add( mChannel30_3);
        mListTextView.add( mChannel31_1);mListTextView.add( mChannel31_2);mListTextView.add( mChannel31_3);mListTextView.add( mChannel32_1);mListTextView.add( mChannel32_2);mListTextView.add( mChannel32_3);
        mListTextView.add( mChannel33_1);mListTextView.add( mChannel33_2);mListTextView.add( mChannel33_3);mListTextView.add( mChannel34_1);mListTextView.add( mChannel34_2);mListTextView.add( mChannel34_3);
        mListTextView.add( mChannel35_1);mListTextView.add( mChannel35_2);mListTextView.add( mChannel35_3);mListTextView.add( mChannel36_1);mListTextView.add( mChannel36_2);mListTextView.add( mChannel36_3);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                if(flag == true){
                    Toast.makeText(this,"目前已是自动请求模式",Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder out = new AlertDialog.Builder(MainActivity.this);
                    out.setTitle("温馨提示");
                    out.setMessage("确定开启自动请求数据？");
                    out.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            flag = true;
                            mRemember.setVisibility(View.VISIBLE);
                            show_mRemember.setVisibility(View.VISIBLE);
                        }
                    });
                    out.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    out.create().show();
                }
                break;
            case R.id.cut:
                if(time_count!=0){
                    AlertDialog.Builder stop = new AlertDialog.Builder(MainActivity.this);
                    stop.setTitle("温馨提示");
                    stop.setMessage("确定关闭自动请求数据？");
                    stop.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            flag = false;
                            time_count = 0;
                            dec = 120;
                            mRemember.setVisibility(View.GONE);
                            show_mRemember.setVisibility(View.GONE);
                        }
                    });
                    stop.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
                    stop.create().show();
                }else {
                    Toast.makeText(this,"没有开启自动请求，不需要关闭",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.out:
                AlertDialog.Builder getout = new AlertDialog.Builder(MainActivity.this);
                getout.setTitle("温馨提示");
                getout.setMessage("确定要离开？");
                getout.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                });
                getout.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                getout.create().show();
                break;
        }
        return true;
    }
    public void getJSON(String str){
        if(str!=null){
            String s = str.substring(str.indexOf("[{"),str.indexOf("}]")+2);
            //Log.i("main--s",s);
            initJSON(s);
        }
    }
    /*解析JSON字符串，一层一层的解析数组*/
    public void initJSON(String s){
        mList.clear();
        try {/*创建JSONArray数组用来存放截取的字符串s，是字符串数组*/
            JSONArray array = new JSONArray(s);
            /*定义一个集合数组*/
            // model_list = new ArrayList[array.length()];
            /*遍历array，取出里面的元素，根据返回结果知道还是数组*/
            for (int i = 0; i <array.length() ; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Model model = new Model();
                model.setmChannelName(jsonObject.getString("ChannelNO"));
                model.setmData1(jsonObject.getString("Data"));
                model.setmMeasureTime(jsonObject.getString("MeasureTime"));
                mList.add(model);
            }
            time_flag = mList.get(0).getmMeasureTime();
            show_mTime.setText("本次采集时间："+time_flag);
            for (int j = 0; j < mList.size(); j++) {
                mListTextView.get(Integer.parseInt(mList.get(j).getmChannelName())-1).setText(mList.get(j).getmData1());
            }
            for (int h = Integer.parseInt(mList.get(mList.size()-1).getmChannelName()); h < mListTextView.size(); h++) {
                mListTextView.get(h).setVisibility(View.GONE);
            }

            Log.i("main---mList",mList.size()+"");

            //displayData(changeToValue(mList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private long firstTime;//用来定义再按一次退出的变量

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mDrawer.isDrawerOpen(mNavigationView)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (firstTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                System.exit(0);
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
            firstTime = System.currentTimeMillis();
        }
    }
}
