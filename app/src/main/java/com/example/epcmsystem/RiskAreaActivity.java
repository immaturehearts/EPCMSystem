package com.example.epcmsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RiskAreaActivity extends AppCompatActivity {

    private int middle_count, high_count;
    private TabLayout mh_tabLayout;
    //    private ProgressBar myProgressBar;
    private MyItemRecyclerViewAdapter adapter;
    String base_url = "https://diqu.gezhong.vip/api.php";//"https://m.sm.cn/api/rest?format=json&method=Huoshenshan.riskArea&_=1628665447912";//https://diqu.gezhong.vip/api.php";
//    private Spinner spinner;
//    private List<String> data_list;
//    private ArrayAdapter<String> arr_adapter;

//    private List<RiskArea> riskAreas = new ArrayList<>();
//    private List<RiskArea> middleRiskAreas = new ArrayList<>();
//    private List<RiskArea> highRiskAreas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_area);

//        myProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        mh_tabLayout = (TabLayout) findViewById(R.id.mh_tabLayout);
//        myProgressBar.setVisibility(View.VISIBLE);
//        Log.d("gallery","progress bar");

        CommonInfo.clearRiskAreaList();
        CommonInfo.clearMiddleRiskAreaList();
        CommonInfo.clearHighRiskAreaList();
//        riskAreas.clear();
//        middleRiskAreas.clear();
//        highRiskAreas.clear();


        new Thread(new Runnable() {
            @Override
            public void run() {
                getRiskArea();
            }
        }).start();
//        mh_tabLayout.getTabAt(0).select();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gallery_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyItemRecyclerViewAdapter(CommonInfo.getMiddleRiskAreas());
        recyclerView.setAdapter(adapter);

        mh_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tab.setText("?????????(" + CommonInfo.getMiddleRiskAreas().size() + ")");
                        adapter = new MyItemRecyclerViewAdapter(CommonInfo.getMiddleRiskAreas());
                        recyclerView.setAdapter(adapter);
                        break;
                    case 1:
                        tab.setText("?????????(" + CommonInfo.getHighRiskAreas().size() + ")");
                        adapter = new MyItemRecyclerViewAdapter(CommonInfo.getHighRiskAreas());
                        recyclerView.setAdapter(adapter);
                        break;
                    default:
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tab.setText("?????????(" + CommonInfo.getMiddleRiskAreas().size() + ")");
                        break;
                    case 1:
                        tab.setText("?????????(" + CommonInfo.getHighRiskAreas().size() + ")");
                        break;
                    default:
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

//????????????
//        spinner = (Spinner) root.findViewById(R.id.spinner);
//        //??????
//        data_list = new ArrayList<String>();
//        data_list.add("?????????");
//        data_list.add("?????????");
//        //?????????
//        arr_adapter= new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data_list);
//        //????????????
//        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //???????????????
//        spinner.setAdapter(arr_adapter);
    }

    private void getRiskArea() {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();//?????????????????????

        Request request = new Request.Builder()
                .url(base_url)
                .get()
//                .post(formBody.build())
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("gallery", "onFailure: ");
                e.printStackTrace();
                showResult("????????????????????????");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("gallery", "onResponse: ");
                int code = response.code();
                String responseStr = response.body().string();
                Log.d("gallery", "responseStr: " + responseStr);
                if(code == HttpURLConnection.HTTP_OK){
                    try{
//                        JSONObject dicts = new JSONObject(responseStr).getJSONObject("data");
//                        Log.d("gallery", dicts.toString());
//                        JSONObject count = dicts.getJSONObject("count");
//                        Log.d("gallery", count.toString());
//                        middle_count = Integer.parseInt(count.getString("1"));
//                        high_count = Integer.parseInt(count.getString("2"));
//                        Log.d("gallery", String.valueOf(middle_count) + "&" + String.valueOf(high_count));
//                        JSONObject citymaps = dicts.getJSONObject("map");
//                        Log.d("gallery", citymaps.toString());
//                        JSONObject middleList = citymaps.getJSONObject("1");
//                        Log.d("gallery", middleList.toString());
//                        JSONObject highList = citymaps.getJSONObject("2");
//                        Log.d("gallery", highList.toString());
//                        for(JSONObject item:citymaps)

                        JSONObject dicts = new JSONObject(responseStr).getJSONObject("data");
                        Log.d("gallery", dicts.toString());
                        JSONArray middleList = dicts.getJSONArray("middlelist");
                        Log.d("gallery", middleList.toString());
                        //???????????????
                        for(int i = 0; i < middleList.length(); i++){
                            JSONObject area = (JSONObject) middleList.opt(i);
                            String province = area.getString("province");
                            String city = area.getString("city");
                            String district = area.getString("county");
                            JSONArray street = area.getJSONArray("communitys");
                            List<String> streets = new ArrayList<>();
                            for(int j = 0; j < street.length(); j++){
                                streets.add(street.opt(j).toString());
                            }
                            RiskArea riskArea = new RiskArea(province, city, district, false, streets);
                            CommonInfo.addToRiskAreaList(riskArea);
                            CommonInfo.addToMiddleRiskAreaList(riskArea);
                        }

                        JSONArray highList = dicts.getJSONArray("highlist");
                        Log.d("gallery", highList.toString());
                        //???????????????
                        for(int k = 0; k < highList.length(); k++){
                            JSONObject area = (JSONObject) highList.opt(k);
                            String province = area.getString("province");
                            String city = area.getString("city");
                            String district = area.getString("county");
                            JSONArray street = area.getJSONArray("communitys");
                            List<String> streets = new ArrayList<>();
                            for(int m = 0; m < street.length(); m++){
                                streets.add(street.opt(m).toString());
                            }
                            RiskArea riskArea = new RiskArea(province, city, district, true, streets);
                            CommonInfo.addToRiskAreaList(riskArea);
                            CommonInfo.addToHighRiskAreaList(riskArea);
                        }
                        Log.d("gallery",CommonInfo.getRiskAreas().get(5).getCity() + CommonInfo.getRiskAreas().get(5).getStreets() + CommonInfo.getRiskAreas().get(5).getRiskType());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("gallery", "error");
                    }

                    showResult("????????????????????????");
                }
                else{
                    Log.d("gallery", "risk area failed");
                    showResult("????????????????????????");
                }
            }
        });

//        myProgressBar.setVisibility(View.GONE);
//        Thread.sleep(500);
    }

    public void showResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext() ,msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}