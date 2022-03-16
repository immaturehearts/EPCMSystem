package com.example.epcmsystem.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.epcmsystem.BasicInfoActivity;
import com.example.epcmsystem.CommonInfo;
import com.example.epcmsystem.HealthPunchInActivity;
import com.example.epcmsystem.R;
import com.example.epcmsystem.RiskArea;
import com.example.epcmsystem.RiskAreaActivity;
import com.example.epcmsystem.databinding.FragmentNotificationBinding;
import com.example.epcmsystem.ui.notifications.NotificationViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationFragment extends Fragment {

    private NotificationViewModel notificationViewModel;
    private FragmentNotificationBinding binding;
    public LocationClient mLocationClient;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private String tipNote;
    final String PREFS_NAME = "userinfo";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        notificationViewModel =
                new ViewModelProvider(this).get(NotificationViewModel.class);

        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
//        SDKInitializer.initialize(getContext());
        positionText = (TextView) root.findViewById(R.id.position_tv);
        mapView = (MapView) root.findViewById(R.id.bdmapview);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        List<String> peimissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            peimissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            peimissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            peimissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!peimissionList.isEmpty()){
            String[] permissions = peimissionList.toArray(new String[peimissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else{
            requestLocation();
        }

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(3000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void navigateTo(BDLocation bdLocation){
        if(isFirstLocate){
            LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()); //获取经纬度
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(15f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
            checkAreaList(bdLocation);
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(bdLocation.getLatitude());
        locationBuilder.longitude(bdLocation.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData); //在地图上显示我的位置

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getContext(),"必须同意权限才可使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                } else{
                    Toast.makeText(getContext(),"发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mapView 销毁后不在处理新接收的位置
                    if (bdLocation == null || mapView == null){
                        return;
                    }
                    StringBuilder currentPosition = new StringBuilder();
                    currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
                    currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
                    currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
                    currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
                    currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
                    currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
                    currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
                    currentPosition.append("定位方式：");
                    if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                        currentPosition.append("GPS");
                    }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                        currentPosition.append("网络");
                    }
                    if(bdLocation.getLocType() == BDLocation.TypeGpsLocation
                            || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                        navigateTo(bdLocation);  //定位到当前位置
                    }
                    positionText.setText(currentPosition);
                }
            });
        }
    }

    public void checkAreaList(BDLocation bdLocation){
        if(!CommonInfo.getRiskAreas().isEmpty()){
            for ( RiskArea area : CommonInfo.getRiskAreas() ) {
                if(TextUtils.equals(area.getDistrict(),bdLocation.getDistrict())){
                    tipNote = "您所在区域：" + bdLocation.getDistrict() + "\n为中高风险地区，请加强防范！";
                    Snackbar.make(getView(),tipNote,Snackbar.LENGTH_INDEFINITE)
                            .setAction("好的", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
//                    showResult(tipNote);
                    return;
                }
            }
            for ( RiskArea area : CommonInfo.getRiskAreas() ) {
                if(TextUtils.equals(area.getCity(),bdLocation.getCity())){
                    tipNote = "您所在城市：" + bdLocation.getCity() + "\n存在中高风险区域，请注意防范！";
                    Snackbar.make(getView(),tipNote,Snackbar.LENGTH_INDEFINITE)
                            .setAction("好的", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
                    break;
                }
            }
        }
    }

    ///TODO: 创建定时器，定时上传定位信息
    private void locationUpload(String city, BigDecimal latitude, String location, BigDecimal longitude)  {
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
//        Thread.sleep(500);
    }

    private String getToken(){
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = userInfo.getString("token", "");
        Log.i("token", token);
        return token;
    }

    public void showResult(final String msg) {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mLocationClient.stop();
        mapView.onDestroy();
        mapView = null;
        baiduMap.setMyLocationEnabled(false);
    }
}