package com.example.epcmsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HealthPunchInActivity extends AppCompatActivity {

    final String PREFS_NAME = "userinfo";
    EditText name_tv, id_tv, phone_tv, health_tv, location_tv;
    Spinner health_spinner;
    ImageButton location_btn;
    Button submit_btn;
    SwitchCompat vaccine_switch;
    String locationDescribe;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    String last_punch_in_time;
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_punch_in);

        name_tv = findViewById(R.id.health_name_tv);
        id_tv = findViewById(R.id.health_id_tv);
        phone_tv = findViewById(R.id.health_phone_tv);
        health_tv = findViewById(R.id.health_temp_tv);
        health_spinner = findViewById(R.id.spinner3);
        vaccine_switch = findViewById(R.id.switch1);
        location_tv = findViewById(R.id.health_location_tv);
        location_btn = findViewById(R.id.health_location_btn);
        submit_btn = findViewById(R.id.health_submit_btn);

        checkBasicInfo();

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.start();

        location_btn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(locationDescribe)) {
                location_tv.setText(locationDescribe.substring(2));
            }
        });

        submit_btn.setOnClickListener(v -> {
            if(TextUtils.isEmpty(health_tv.getText()) || TextUtils.isEmpty(location_tv.getText()) ){
                AlertDialog alertDialog = new AlertDialog.Builder(HealthPunchInActivity.this)
                        .setTitle("信息不全")
                        .setMessage("请填写所有信息后提交")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog.show();
            } else if(!isTemperature(health_tv.getText().toString())) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(HealthPunchInActivity.this)
                        .setTitle("体温格式错误")
                        .setMessage("体温格式错误，请重新填写")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create();
                alertDialog1.show();
                health_tv.requestFocus();
            } else {
                try {
                    if (!checkTimeDiff()) {
                        AlertDialog alertDialog2 = new AlertDialog.Builder(HealthPunchInActivity.this)
                                .setMessage("今日已打卡，请明天再来！")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    finish();
                                })
                                .create();
                        alertDialog2.show();
                    } else {
                        int checked = 1;
                        if(!vaccine_switch.isChecked()){
                            checked = 0;
                        }
                        punchInUpload(health_tv.getText().toString(), health_spinner.getSelectedItemPosition(),
                                location_tv.getText().toString(), checked);
        //                showResult("健康上报成功");
        //                finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        fillBasicInfo();
    }

    //检查个人基本信息是否填写
    private void checkBasicInfo(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(userInfo.contains("personName") && userInfo.contains("personGender") && userInfo.contains("personID")
                && userInfo.contains("personPhone") && userInfo.contains("personEmail")) {
            return;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(HealthPunchInActivity.this)
                    .setTitle("信息不全")
                    .setMessage("您还未填写个人基本信息，请填写后进行健康上报！")
                    .setPositiveButton("前往", (dialog, which) -> {
                        Intent intent = new Intent(getApplicationContext(), BasicInfoActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .create();
            alertDialog.show();
        }
    }

    //填入用户基本信息
    private void fillBasicInfo(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        name_tv.setText(userInfo.getString("personName", ""));
        id_tv.setText(userInfo.getString("personID", ""));
        phone_tv.setText(userInfo.getString("personPhone", ""));

        name_tv.setEnabled(false);
        id_tv.setEnabled(false);
        phone_tv.setEnabled(false);
    }

    //保存本次提交时间
    private void saveTime(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        Date date = new Date(System.currentTimeMillis());
        last_punch_in_time = format.format(date);
        editor.putString("last_punch_in_time", last_punch_in_time);
        editor.apply();
    }

    //限制提交，一天提交一次
    private boolean checkTimeDiff() throws ParseException {
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(userInfo.contains("last_punch_in_time")){
            String lastTime = userInfo.getString("last_punch_in_time","");
            Date now = new Date(System.currentTimeMillis());
            String thisTime = format.format(now);
            return !TextUtils.equals(lastTime, thisTime);
        }
        return true;
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取位置描述信息相关的结果
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            locationDescribe = location.getAddrStr();    //获取位置描述信息
        }
    }

    //检查体温度数是否合法
    public static boolean isTemperature(String temp) {
        String regex = "^((3[5-9])|40)(.\\d)?$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(temp);
        return m.matches();
    }

    private void punchInUpload(String degree, Integer health, String location, Integer vaccine)  {
        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        RequestBody requestBody = new FormBody.Builder()
                .add("degree", degree)
                .add("health", String.valueOf(health))
                .add("location", location)
                .add("vaccine", String.valueOf(vaccine))
                .build();
        Request request = new Request.Builder()
                .url("http://124.70.222.113:9080/history/punchIn")
                .header("Cookie", "token="+token)
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("punchIn", "onFailure: ");
                e.printStackTrace();
                showResult("健康打卡失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("punchIn", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("punchIn", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    Log.d("punchIn","健康打卡成功");
                    showResult("健康上报成功");
                    saveTime();
                    finish();
                }
                else{
                    Log.d("punchIn", "logout failed");
                    showResult("健康打卡失败");
                }
            }
        });
    }

    private String getToken(){
        SharedPreferences userInfo = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

    public void showResult(final String msg) {
        runOnUiThread(() -> Toast.makeText(HealthPunchInActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}