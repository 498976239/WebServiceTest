package com.ss.www.webservicetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ss.www.webservicetest.utils.WebServiceUtils;

import org.ksoap2.serialization.SoapObject;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar mProgressBar;
    private EditText mLogin,mPassword;
    private Button mButton_login,mButton_cancel;
    private CheckBox rememberPass;
    private int count;
    private boolean action;
    public static final int PASSDATE = 1;
    public static final String WSDL_URI = "http://www.xl-iot.com:9016/WXHQWebs/WxHqWebService.asmx?WSDL";
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String METHOD_NAME = "RTDALogin";
    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask(){

        @Override
        public void run() {
            if(action){
                count++;
            }
            if(count >= 8){
                Message m = Message.obtain();
                m.what = PASSDATE;
                handler.sendMessage(m);
            }
        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PASSDATE:
                    mProgressBar.setVisibility(View.GONE);
                    count = 0;
                    action = false;
                    Toast.makeText(LoginActivity.this,"有异常",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mLogin = (EditText) findViewById(R.id.login_name);
        mPassword = (EditText) findViewById(R.id.password_in);
        mButton_login = (Button) findViewById(R.id.login_btn);
        mButton_cancel = (Button) findViewById(R.id.cancel_action_btn);
        rememberPass = (CheckBox) findViewById(R.id.remember_flag);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        mTimer = new Timer();
        mTimer.schedule(mTimerTask,1000,1000);
        boolean isRemember = pref.getBoolean("remember_password",false);
        Log.i("main--isRemember",isRemember+"");
        if(isRemember){
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            mLogin.setText(account);
            mPassword.setText(password);
            rememberPass.setChecked(true);
        }
        mButton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action  = true;
                mProgressBar.setVisibility(View.VISIBLE);
                final String account = mLogin.getText().toString();
                final String password = mPassword.getText().toString();
                WebServiceUtils.call(WSDL_URI, NAMESPACE, METHOD_NAME, account, password, new WebServiceUtils.Response() {
                    @Override
                    public void onSuccess(SoapObject result) {
                        mProgressBar.setVisibility(View.GONE);
                        action = false;
                        count = 0;
                        String str = String.valueOf(result);
                        if(str.contains("RTDALoginResult=1")){
                            editor = pref.edit();
                            if(rememberPass.isChecked()){
                                editor.putBoolean("remember_password", true);
                                editor.putString("account", account);
                                editor.putString("password", password);
                            }else {
                               editor.clear();
                            }
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        if(str.contains("RTDALoginResult=-1")){
                            Toast.makeText(LoginActivity.this,"用户名或者密码不能为空",Toast.LENGTH_SHORT).show();
                        }
                        if(str.contains("RTDALoginResult=-5")){
                            Toast.makeText(LoginActivity.this,"用户名或者密码错误",Toast.LENGTH_SHORT).show();
                        }
                        Log.i("main-login-result",result+"");
                    }

                    @Override
                    public void onError(Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        action = false;
                        count = 0;
                        Log.i("main-login-Exception",e+"");
                        String str2 = String.valueOf(e);
                        Log.i("main---Exception",str2);
                        //Log.i("main---Exception",str2);
                        if(str2.contains("UnknownHostException")){
                            Toast.makeText(LoginActivity.this,"没有打开网络或服务器关闭",Toast.LENGTH_SHORT).show();
                        }
                        if(str2.contains("SocketTimeoutException")){
                            Toast.makeText(LoginActivity.this,"服务器连接异常",Toast.LENGTH_SHORT).show();
                        }
                        if(str2.contains("ConnectException")){
                            Toast.makeText(LoginActivity.this,"域名解析异常",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
            count = 0;
            action = false;
        }
    }
    private long firstTime;//用来定义再按一次退出的变量

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
            if (firstTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                System.exit(0);
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
            firstTime = System.currentTimeMillis();
    }
}
