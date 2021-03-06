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
        //??????LocationClient???
        mLocationClient.registerLocationListener(myListener);
        //??????????????????
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
        //mLocationClient???????????????????????????LocationClient??????
        //??????????????????LocationClientOption???????????????setLocOption???????????????LocationClient????????????
        mLocationClient.start();

        location_btn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(locationDescribe)) {
                location_tv.setText(locationDescribe.substring(2));
            }
        });

        submit_btn.setOnClickListener(v -> {
            if(TextUtils.isEmpty(health_tv.getText()) || TextUtils.isEmpty(location_tv.getText()) ){
                AlertDialog alertDialog = new AlertDialog.Builder(HealthPunchInActivity.this)
                        .setTitle("????????????")
                        .setMessage("??????????????????????????????")
                        .setPositiveButton("??????", (dialog, which) -> {
                        })
                        .create();
                alertDialog.show();
            } else if(!isTemperature(health_tv.getText().toString())) {
                AlertDialog alertDialog1 = new AlertDialog.Builder(HealthPunchInActivity.this)
                        .setTitle("??????????????????")
                        .setMessage("????????????????????????????????????")
                        .setPositiveButton("??????", (dialog, which) -> {
                        })
                        .create();
                alertDialog1.show();
                health_tv.requestFocus();
            } else {
                try {
                    if (!checkTimeDiff()) {
                        AlertDialog alertDialog2 = new AlertDialog.Builder(HealthPunchInActivity.this)
                                .setMessage("????????????????????????????????????")
                                .setPositiveButton("??????", (dialog, which) -> {
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
        //                showResult("??????????????????");
        //                finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        fillBasicInfo();
    }

    //????????????????????????????????????
    private void checkBasicInfo(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if(userInfo.contains("personName") && userInfo.contains("personGender") && userInfo.contains("personID")
                && userInfo.contains("personPhone") && userInfo.contains("personEmail")) {
            return;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(HealthPunchInActivity.this)
                    .setTitle("????????????")
                    .setMessage("?????????????????????????????????????????????????????????????????????")
                    .setPositiveButton("??????", (dialog, which) -> {
                        Intent intent = new Intent(getApplicationContext(), BasicInfoActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .create();
            alertDialog.show();
        }
    }

    //????????????????????????
    private void fillBasicInfo(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        name_tv.setText(userInfo.getString("personName", ""));
        id_tv.setText(userInfo.getString("personID", ""));
        phone_tv.setText(userInfo.getString("personPhone", ""));

        name_tv.setEnabled(false);
        id_tv.setEnabled(false);
        phone_tv.setEnabled(false);
    }

    //????????????????????????
    private void saveTime(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        Date date = new Date(System.currentTimeMillis());
        last_punch_in_time = format.format(date);
        editor.putString("last_punch_in_time", last_punch_in_time);
        editor.apply();
    }

    //?????????????????????????????????
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
            //?????????BDLocation?????????????????????????????????????????????get??????????????????????????????????????????
            //????????????????????????????????????????????????????????????
            //??????????????????????????????????????????????????????BDLocation???????????????

            locationDescribe = location.getAddrStr();    //????????????????????????
        }
    }

    //??????????????????????????????
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
                showResult("??????????????????");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("punchIn", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("punchIn", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    Log.d("punchIn","??????????????????");
                    showResult("??????????????????");
                    saveTime();
                    finish();
                }
                else{
                    Log.d("punchIn", "logout failed");
                    showResult("??????????????????");
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