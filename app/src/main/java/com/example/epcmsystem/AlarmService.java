package com.example.epcmsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.epcmsystem.ui.notifications.NotificationFragment;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//定时任务服务
public class AlarmService extends Service {
    //存放定位信息
    public static String mCity = null, mLocation = null;
    public static Double mLatitude =null, mLongitude = null;

    final String PREFS_NAME = "userinfo";
//    private static NotificationFragment.UploadPositionTask uploadPositionTask;
//    private static Context uContext;
//
//    public static void startService(Context c, NotificationFragment.UploadPositionTask u){
//        uploadPositionTask = u;
//        uContext = c;
//        Intent in = new Intent(c, AlarmService.class);
//        c.startService(in);
//    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "service started");
        //启动后台任务——上传定位信息
        if(mCity != null){
            UploadPositionTask mUploadTask = new UploadPositionTask();
            mUploadTask.execute((Void) null);
            Log.d("service", "service started task started");
        }

        //设置定时器Alarm，每隔30秒向服务器上传定位数据
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int interval = 30 * 1000; //30s上传一次
        long triggerAtTime = System.currentTimeMillis() + interval;

        //循环启动服务，将定位数据存入intent的bundle包中
        Intent i = new Intent(this, AlarmService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.setExact(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        Log.d("service", "service started again");
        return super.onStartCommand(intent, flags, startId);
    }

    //后台任务：上传位置信息
    public class UploadPositionTask extends AsyncTask<Void, Void, Boolean> {
        public UploadPositionTask(){ }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                locationUpload(mCity,mLatitude,mLocation,mLongitude);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        private void locationUpload(String city, Double latitude, String location, Double longitude)  {
            OkHttpClient client = new OkHttpClient();
            String token = getToken();
            RequestBody requestBody = new FormBody.Builder()
                    .add("city", city)
                    .add("latitude", String.valueOf(latitude))
                    .add("location", location)
                    .add("longitude", String.valueOf(longitude))
                    .build();
            Request request = new Request.Builder()
                    .url("http://124.70.222.113:9080/position/upload")
                    .header("Cookie", "token="+token)
                    .post(requestBody)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("position", "onFailure: ");
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("position", "onResponse: ");
                    int code = response.code();
                    String responseStr = response.body().string();
                    Log.d("position", "responseStr: " + responseStr);
                    if(code == HttpURLConnection.HTTP_OK){
                        Log.d("position","定位数据上传成功");
                    }
                    else{
                        Log.d("position", "position upload failed");
                    }
                }
            });
        }

        private String getToken(){
            SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String token = userInfo.getString("token", "");
            Log.i("token", token);
            return token;
        }

    }
}
