package com.example.epcmsystem.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.epcmsystem.BasicInfoActivity;
import com.example.epcmsystem.HealthPunchInActivity;
import com.example.epcmsystem.IDActivity;
import com.example.epcmsystem.LoginActivity;
import com.example.epcmsystem.R;
import com.example.epcmsystem.databinding.FragmentHomeBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    TextView name_tv, email_tv;
    final String PREFS_NAME = "userinfo";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        NavigationView myselfView = root.findViewById(R.id.home_nv);
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.home_header, binding.getRoot(), false);
        name_tv = header.findViewById(R.id.user_name);
        email_tv = header.findViewById(R.id.user_email);
        myselfView.addHeaderView(header);
        inputInfo();

        myselfView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.id:
//                        Intent intent1 = new Intent(getActivity(), IDActivity.class);
//                        startActivity(intent1);
                    break;
                case R.id.info:
                    Intent intent2 = new Intent(getActivity(), BasicInfoActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.punch_in:
                    Intent intent3 = new Intent(getActivity(), HealthPunchInActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.service:
                    Intent intent4 = new Intent(Intent.ACTION_VIEW);
                    intent4.setData(Uri.parse("https://github.com/immaturehearts/EPCMSystem"));
                    startActivity(intent4);
                default:
            }
            return false;
        });

        Button logout_btn = (Button) root.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("logout","log out button pressed.");
                logout();
            }
        });
        return root;
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
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else{
                    Log.d("logout", "logout failed");
                    showResult("退出登录失败");
                }

            }
        });
//        Thread.sleep(500);
    }

    private String getToken(){
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

    private void deleteToken(){
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        //得到Editor后，写入需要保存的数据
        editor.remove("token");
        editor.commit();//提交修改
        Log.i("logout", "token deleted");
    }

    private void inputInfo(){
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        name_tv.setText(userInfo.getString("personName", "Android Studio"));
        email_tv.setText(userInfo.getString("personEmail", "android.studio@android.com"));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showResult(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}