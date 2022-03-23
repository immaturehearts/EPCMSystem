package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminActivity extends AppCompatActivity {
    final String PREFS_NAME = "userinfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        NavigationView myselfView = findViewById(R.id.admin_home_nv);
        myselfView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_history) {
                Intent intent1 = new Intent(getApplicationContext(), HistoryActivity.class);
                intent1.putExtra("userType", 1);
                startActivity(intent1);
            }
            return false;
        });

        Button logout_btn = (Button) findViewById(R.id.admin_logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("logout","log out button pressed.");
                logout();
            }
        });
    }

    private void logout()  {
        OkHttpClient client = new OkHttpClient();
        String token = getToken();
        Request request = new Request.Builder()
                .url("http://124.70.222.113:9080/user/logout")
                .header("Cookie", "token="+token)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("logout", "onFailure: ");
                e.printStackTrace();
                showResult("退出登录失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("logout", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("logout", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    deleteToken();
                    Log.d("logout","退出登录成功");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Log.d("logout", "logout failed");
                    showResult("退出登录失败");
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

    private void deleteToken(){
        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        //得到Editor后，写入需要保存的数据
        editor.remove("token");
        editor.commit();//提交修改
        Log.i("logout", "token deleted");
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}