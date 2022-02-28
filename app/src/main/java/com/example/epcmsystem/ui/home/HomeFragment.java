package com.example.epcmsystem.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.example.epcmsystem.BasicInfoActivity;
import com.example.epcmsystem.HealthPunchInActivity;
import com.example.epcmsystem.IDActivity;
import com.example.epcmsystem.R;
import com.example.epcmsystem.databinding.FragmentHomeBinding;
import com.google.android.material.navigation.NavigationView;

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
                default:
            }
            return false;
        });

        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}