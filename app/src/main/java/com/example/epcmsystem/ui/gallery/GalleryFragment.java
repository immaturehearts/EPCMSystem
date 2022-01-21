//package com.example.epcmsystem.ui.gallery;
//
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ProgressBar;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.epcmsystem.MyItemRecyclerViewAdapter;
//import com.example.epcmsystem.R;
//import com.example.epcmsystem.RiskArea;
//import com.example.epcmsystem.databinding.FragmentGalleryBinding;
//import com.google.android.material.tabs.TabLayout;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class GalleryFragment extends Fragment {
//
//    private GalleryViewModel galleryViewModel;
//    private FragmentGalleryBinding binding;
//    private int middle_count, high_count;
//    private TabLayout mh_tabLayout;
////    private ProgressBar myProgressBar;
//    private MyItemRecyclerViewAdapter adapter;
//    String base_url = "https://diqu.gezhong.vip/api.php";//"https://m.sm.cn/api/rest?format=json&method=Huoshenshan.riskArea&_=1628665447912";//https://diqu.gezhong.vip/api.php";
////    private Spinner spinner;
////    private List<String> data_list;
////    private ArrayAdapter<String> arr_adapter;
//
//    private List<RiskArea> riskAreas = new ArrayList<>();
//    private List<RiskArea> middleRiskAreas = new ArrayList<>();
//    private List<RiskArea> highRiskAreas = new ArrayList<>();
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        galleryViewModel =
//                new ViewModelProvider(this).get(GalleryViewModel.class);
//
//        binding = FragmentGalleryBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
////        myProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
//        mh_tabLayout = (TabLayout) root.findViewById(R.id.mh_tabLayout);
////        myProgressBar.setVisibility(View.VISIBLE);
////        Log.d("gallery","progress bar");
//
//        riskAreas.clear();
//        middleRiskAreas.clear();
//        highRiskAreas.clear();
//
//        try {
//            getRiskArea();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
////        mh_tabLayout.getTabAt(0).select();
//
//        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.gallery_rv);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new MyItemRecyclerViewAdapter(middleRiskAreas);
//        recyclerView.setAdapter(adapter);
//
//        mh_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()){
//                    case 0:
//                        tab.setText("中风险(" + middleRiskAreas.size() + ")");
//                        adapter = new MyItemRecyclerViewAdapter(middleRiskAreas);
//                        recyclerView.setAdapter(adapter);
//                        break;
//                    case 1:
//                        tab.setText("高风险(" + highRiskAreas.size() + ")");
//                        adapter = new MyItemRecyclerViewAdapter(highRiskAreas);
//                        recyclerView.setAdapter(adapter);
//                        break;
//                    default:
//                }
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                switch (tab.getPosition()){
//                    case 0:
//                        tab.setText("中风险(" + middleRiskAreas.size() + ")");
//                        break;
//                    case 1:
//                        tab.setText("高风险(" + highRiskAreas.size() + ")");
//                        break;
//                    default:
//                }
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//
////下拉列表
////        spinner = (Spinner) root.findViewById(R.id.spinner);
////        //数据
////        data_list = new ArrayList<String>();
////        data_list.add("中风险");
////        data_list.add("高风险");
////        //适配器
////        arr_adapter= new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data_list);
////        //设置样式
////        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        //加载适配器
////        spinner.setAdapter(arr_adapter);
//
//        return root;
//    }
//
//    private void getRiskArea() throws InterruptedException {
//        OkHttpClient client = new OkHttpClient();
//        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
//
//        Request request = new Request.Builder()
//                .url(base_url)
//                .get()
////                .post(formBody.build())
//                .build();
//
//        Call call = client.newCall(request);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("gallery", "onFailure: ");
//                e.printStackTrace();
//                showResult("获取风险区域失败");
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("gallery", "onResponse: ");
//                int code = response.code();
//                String responseStr = response.body().string();
//                Log.d("gallery", "responseStr: " + responseStr);
//                if(code == HttpURLConnection.HTTP_OK){
//                    try{
////                        JSONObject dicts = new JSONObject(responseStr).getJSONObject("data");
////                        Log.d("gallery", dicts.toString());
////                        JSONObject count = dicts.getJSONObject("count");
////                        Log.d("gallery", count.toString());
////                        middle_count = Integer.parseInt(count.getString("1"));
////                        high_count = Integer.parseInt(count.getString("2"));
////                        Log.d("gallery", String.valueOf(middle_count) + "&" + String.valueOf(high_count));
////                        JSONObject citymaps = dicts.getJSONObject("map");
////                        Log.d("gallery", citymaps.toString());
////                        JSONObject middleList = citymaps.getJSONObject("1");
////                        Log.d("gallery", middleList.toString());
////                        JSONObject highList = citymaps.getJSONObject("2");
////                        Log.d("gallery", highList.toString());
////                        for(JSONObject item:citymaps)
//
//                        JSONObject dicts = new JSONObject(responseStr).getJSONObject("data");
//                        Log.d("gallery", dicts.toString());
//                        JSONArray middleList = dicts.getJSONArray("middlelist");
//                        Log.d("gallery", middleList.toString());
//                        //中风险地区
//                        for(int i = 0; i < middleList.length(); i++){
//                            JSONObject area = (JSONObject) middleList.opt(i);
//                            String province = area.getString("province");
//                            String city = area.getString("city");
//                            String district = area.getString("county");
//                            JSONArray street = area.getJSONArray("communitys");
//                            List<String> streets = new ArrayList<>();
//                            for(int j = 0; j < street.length(); j++){
//                                streets.add(street.opt(j).toString());
//                            }
//                            RiskArea riskArea = new RiskArea(province, city, district, false, streets);
//                            riskAreas.add(riskArea);
//                            middleRiskAreas.add(riskArea);
//                        }
//
//                        JSONArray highList = dicts.getJSONArray("highlist");
//                        Log.d("gallery", highList.toString());
//                        //高风险地区
//                        for(int k = 0; k < highList.length(); k++){
//                            JSONObject area = (JSONObject) highList.opt(k);
//                            String province = area.getString("province");
//                            String city = area.getString("city");
//                            String district = area.getString("county");
//                            JSONArray street = area.getJSONArray("communitys");
//                            List<String> streets = new ArrayList<>();
//                            for(int m = 0; m < street.length(); m++){
//                                streets.add(street.opt(m).toString());
//                            }
//                            RiskArea riskArea = new RiskArea(province, city, district, true, streets);
//                            riskAreas.add(riskArea);
//                            highRiskAreas.add(riskArea);
//                        }
////                        Log.d("gallery",riskAreas.get(25).getCity() + riskAreas.get(25).getStreets() + riskAreas.get(25).getRiskType());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Log.d("gallery", "error");
//                    }
//
//                    showResult("获取风险区域成功");
//                }
//                else{
//                    Log.d("gallery", "risk area failed");
//                    showResult("获取风险区域失败");
//                }
//            }
//        });
//
////        myProgressBar.setVisibility(View.GONE);
////        Thread.sleep(500);
//    }
//
//    public void showResult(final String msg) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getContext() ,msg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}