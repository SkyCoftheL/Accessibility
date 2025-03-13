package org.example.accessible;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import org.example.accessible.adapter.MyFragmentAdapter;
import org.example.accessible.databinding.ActivityMain2Binding;
import org.example.accessible.ui.document.DocumentFragment;
import org.example.accessible.ui.service.ServiceFragment;
import org.example.accessible.ui.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;

    private MyFragmentAdapter myFragmentAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        bottomNavigationView=findViewById(R.id.nav_view);

        initPage();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            //监听页面变化
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_service);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_settings);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.navigation_document);
                        break;
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            //监听按钮点击
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_service:
                        viewPager2.setCurrentItem(0);
                        return true;
                    case R.id.navigation_settings:
                        viewPager2.setCurrentItem(1);
                        return true;
                    case R.id.navigation_document:
                        viewPager2.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });


    }

    private void initPage() {
        viewPager2=findViewById(R.id.main_page);
        List<Fragment> list=new ArrayList<>();
        list.add(new ServiceFragment());
        list.add(new SettingsFragment());
        list.add(new DocumentFragment());

        myFragmentAdapter=new MyFragmentAdapter(getSupportFragmentManager(),getLifecycle(),list);

        viewPager2.setAdapter(myFragmentAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, getString(R.string.overlay_permission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}