package com.example.epcmsystem;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
//import com.example.epcmsystem.ui.gallery.GalleryFragment;
import com.example.epcmsystem.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epcmsystem.databinding.ActivityNavigationBinding;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration, bAppBarConfiguration;
    private ActivityNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext()); //百度地图初始化
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
        binding.appBarNavigation.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HealthPunchInActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //抽屉导航栏
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_comic, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        //底部导航栏
        bAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_notification, R.id.navigation_home)
                .setOpenableLayout(drawer)
                .build();
        NavController bottomNavController = Navigation.findNavController(this,R.id.nav_host_fragment_activity_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupActionBarWithNavController(this, bottomNavController, bAppBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, bottomNavController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        drawer.closeDrawers();
                        bottomNav.setSelectedItemId(R.id.navigation_home);
                        break;
                    case R.id.nav_gallery:
                        drawer.closeDrawers();
                        Intent intent = new Intent(getApplicationContext(), RiskAreaActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_comic:
                        drawer.closeDrawers();
                        Intent intent2 = new Intent(getApplicationContext(), ComicActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_slideshow:
                        drawer.closeDrawers();
                        break;
                    default:
                }
                return false;
            }
        });
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

//        NavHostFragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_navigation);
//        NavController bottomNavController = navHostFragment.getNavController();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}