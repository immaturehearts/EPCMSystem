package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {

    String base_url = "http://124.70.222.113:9080/history/historyUser";
    private int type = 0;
    final String PREFS_NAME = "userinfo";
    private Uri[] image_urls = {
            Uri.parse("https://iconfont.alicdn.com/t/649a1428-ef07-4c08-989f-bbb2fde977ff.png"),
            Uri.parse("https://iconfont.alicdn.com/t/3c99b58e-c319-4736-8613-2c5f34b7b8e4.png"),
            Uri.parse("https://iconfont.alicdn.com/t/fbd32d88-901f-48e6-88b5-28c5d4f3935d.png"),
            Uri.parse("https://iconfont.alicdn.com/t/bb6ab768-da6d-4b83-927f-2d23802432e2.png"),
            Uri.parse("https://iconfont.alicdn.com/t/37922153-881d-4c40-9648-6ed3087783d5.png"),
            Uri.parse("https://iconfont.alicdn.com/t/c137de34-29c7-4a2f-921b-b795fb2707b4.png"),
            Uri.parse("https://iconfont.alicdn.com/t/4935db2f-33ed-4d5c-b7da-39062a287a6b.png"),
            Uri.parse("https://iconfont.alicdn.com/t/94983a61-bd7c-47f6-83c9-60f7a42c96fc.png"),
            Uri.parse("https://iconfont.alicdn.com/t/5b31b66a-3d47-45d3-8c74-cf7e105f9b65.png"),
            Uri.parse("https://iconfont.alicdn.com/t/8edd4372-11bd-4da6-a0b8-f6e9d5a289e6.png")
    };

    private List<HistoryCard> cardList = new ArrayList<>();

    private HistoryCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent1 = getIntent();
        if(intent1.getIntExtra("userType", 0) == 1){
            base_url = "http://124.70.222.113:9080/history/all/history";
            type = 1;
        }
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();// 隐藏ActionBar
        }
        Glide.get(this).clearMemory();//clear memory
        cardList.clear();
        try {
            getHist();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.history_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryCardAdapter(cardList, type);
        recyclerView.setAdapter(adapter);
    }

    private void getHist() throws InterruptedException {
        long page = 1;
        int pageSize = 20;
        String url = base_url+"?page="+page+"&pageSize="+pageSize;

        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", "token="+token)
                .get()
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("history", "onFailure: ");
                e.printStackTrace();
                showResult("获取历史记录失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("history", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("history", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    try{
                        JSONArray jsonArray=new JSONObject(responseStr).getJSONArray("data");
                        Log.d("history", jsonArray.toString());
                        int maxhis = jsonArray.length();
                        if(maxhis>pageSize) maxhis=pageSize;
                        for(int i=0;i<maxhis;i++){
                            JSONObject jsonObject = (JSONObject)jsonArray.opt(i);
                            long uid = jsonObject.getLong("uid");
                            String date = jsonObject.getString("gmtModify");//.substring(0,20);
                            String degree = jsonObject.getString("degree");
                            int health = jsonObject.getInt("health");
                            String location = jsonObject.getString("location");
                            long id = jsonObject.getLong("id");
                            HistoryCard card = new HistoryCard(id, uid, formatCSTDate(date), degree, health, location, image_urls[i%image_urls.length]);
                            cardList.add(card);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("history", "json error");
                    }
                }
                else{
                    Log.d("history", "history failed");
                    showResult("获取历史记录失败");
                }
            }
        });

        Thread.sleep(1000);
    }

    public static String formatCSTDate(String cstDateStr) {
        Date date = null;
        SimpleDateFormat sdf1 = null;
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        sdf2.setTimeZone(TimeZone.getTimeZone("GMT-6"));
        try {
            sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
            sdf1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            date = sdf1.parse(cstDateStr);
        } catch (ParseException e) {
            Log.d("date","CST日期格式转换出错，原因：" + e.getMessage());
            return "null";
        }
        return sdf2.format(date);
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HistoryActivity.this,msg, Toast.LENGTH_SHORT).show();
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