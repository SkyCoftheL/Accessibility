package org.example.accessible;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.example.accessible.check.CheckRoot;
import org.example.accessible.database.MyDataBases;
import org.example.accessible.service.WindowService;


public class MainActivity extends AppCompatActivity {

    private Button start,stop,save;
    private TextView inforview;
    private WindowService mService;
    private boolean mBound;

    private MyDataBases myDataBases;
    private final static String dataBaseName="MyDatabase.db";
    private final static int dataBaseVersion=1;
    private EditText singleEdit,doubleEdit,longPressEdit;
    private ContentValues updateValues;

    private Switch upper,unimportant;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                applyOverlay();
            }
        }

//        start=findViewById(R.id.startButton);
//        stop=findViewById(R.id.stopButton);
//        save=findViewById(R.id.saveButton);
//        inforview=findViewById(R.id.infortextview);
//
//
//
//        singleEdit=findViewById(R.id.SingleTapEdit);
//        doubleEdit=findViewById(R.id.DoubleTapEdit);
//        longPressEdit=findViewById(R.id.LongPressEdit);
//
//        upper=findViewById(R.id.upper);
//        unimportant=findViewById(R.id.unimportant);



        myDataBases =new MyDataBases(this,dataBaseName,null,dataBaseVersion);
        SQLiteDatabase db = myDataBases.getWritableDatabase();

        singleEdit.setText(myDataBases.getSingle(db));
        doubleEdit.setText(myDataBases.getDouble(db));
        longPressEdit.setText(myDataBases.getLongPress(db));

        updateValues = new ContentValues();

        start.setOnClickListener(view -> {
            if (CheckRoot.isRooted()) {
                Intent intent = new Intent(this, WindowService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                inforview.setText(getString(R.string.notification_content));
            }else {
                Toast.makeText(this,getString(R.string.root_required),Toast.LENGTH_SHORT).show();
                inforview.setText(getString(R.string.root_required));
            }
        });

        stop.setOnClickListener(view -> {

            if (mBound) {
                mService.closeService();
                unbindService(mConnection);
                Toast.makeText(this,getString(R.string.service_unbounded),Toast.LENGTH_SHORT).show();
                inforview.setText(getString(R.string.service_unbounded));
                mBound=false;
            }
        });

        upper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (upper.isChecked()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.delete_on_post_notification), Toast.LENGTH_SHORT).show();
                    upper.setChecked(false);
                }
            }
        });

        unimportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (unimportant.isChecked()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.remove_unimportant_notification), Toast.LENGTH_SHORT).show();
                    unimportant.setChecked(false);
                }
            }
        });

        save.setOnClickListener(view -> {
            String whereClause = "id =?";
            String[] whereArgs ={"1"};
            int rowsAffected=0;
            long newRowId=0;

            String singletap=singleEdit.getText().toString();
            String doubletap=doubleEdit.getText().toString();
            String longpress=longPressEdit.getText().toString();
//            if (!singletap.equals("")) updateValues.put("single", singletap);
//            else updateValues.put("single",0);
//
//            if (!doubletap.equals("")) updateValues.put("double", doubletap);
//            else updateValues.put("double",0);
//
//            if (!longpress.equals("")) updateValues.put("longpress", longpress);
//            else updateValues.put("longpress",0);

            updateValues.put("single", singletap);
            updateValues.put("double", doubletap);
            updateValues.put("longpress",longpress);



           if (myDataBases.getId(db)==1)  {
               rowsAffected = db.update("my_table", updateValues,whereClause,whereArgs);
               Log.d("MainA", "update: ");
           }
           else  {
               newRowId = db.insert("my_table", null, updateValues);
               Log.d("MainA", "insert: ");
           }


            if (newRowId > 0||rowsAffected>0) {
                Toast.makeText(this,getString(R.string.db_s),Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this,getString(R.string.db_f),Toast.LENGTH_SHORT).show();




        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // 确保后续通过getIntent获取的是最新的意图
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

    private void applyOverlay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void toSetOverlay(){
        Toast.makeText(this,getString(R.string.overlay_permission),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1234);
    }


}