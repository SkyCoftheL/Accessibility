package org.example.accessible.ui.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.example.accessible.R;
import org.example.accessible.check.CheckRoot;
import org.example.accessible.service.WindowService;

public class ServiceFragment extends Fragment {



    private TextView inforview;

    private Switch startSwitch;

    private WindowService mService;
    private boolean mBound;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WindowService.LocalBinder binder = (WindowService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    private View serviceView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (serviceView==null) serviceView=inflater.inflate(R.layout.fragment_service,container,false);

        setOverLay();
        initFinder();
        initListener();

        return serviceView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }



    private void initFinder(){
        startSwitch=serviceView.findViewById(R.id.startSwitch);
        inforview=serviceView.findViewById(R.id.infortextview);

    }
    private void initListener(){
        startSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (startSwitch.isChecked()){
                    startService();
                }else stopService();
            }
        });
    }

    private void startService(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                applyOverlay();
            }
        }else{


        if (CheckRoot.isRooted()) {
            setOverLay();
            Intent intent = new Intent(getContext(), WindowService.class);
            getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            inforview.setText(getString(R.string.notification_content));
        }else {
            Toast.makeText(getContext(),getString(R.string.root_required),Toast.LENGTH_SHORT).show();
            inforview.setText(getString(R.string.root_required));
            startSwitch.setChecked(false);
        }
        }
    }

    private void stopService(){
        if (mBound) {
            mService.closeService();
            getContext().unbindService(mConnection);
            Toast.makeText(getContext(),getString(R.string.service_unbounded),Toast.LENGTH_SHORT).show();
            inforview.setText(getString(R.string.service_unbounded));
            mBound=false;
        }
    }

    private void setOverLay(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                applyOverlay();
            }
        }
    }

    private void applyOverlay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.apply_for_permission))
                .setMessage(getString(R.string.overlay_permission))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 在这里处理确定按钮的点击事件
                        toSetOverlay();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 在这里处理取消按钮的点击事件
                        startSwitch.setChecked(false);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toSetOverlay(){
        startSwitch.setChecked(false);
        Toast.makeText(getContext(),getString(R.string.overlay_permission),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getContext().getPackageName()));
        startActivityForResult(intent, 1234);
    }

    // TODO: 2025/3/13 免root
}