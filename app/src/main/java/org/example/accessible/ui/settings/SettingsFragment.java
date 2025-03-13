package org.example.accessible.ui.settings;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.example.accessible.R;
import org.example.accessible.database.MyDataBases;

public class SettingsFragment extends Fragment {

    private MyDataBases myDataBases;
    private final static String dataBaseName="MyDatabase.db";
    private final static int dataBaseVersion=1;
    SQLiteDatabase db;
    private EditText singleEdit,doubleEdit,longPressEdit;
    private ContentValues updateValues;

    private Button save;

    private Switch upper,unimportant;



    private View settingsView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (settingsView ==null)
            settingsView=inflater.inflate(R.layout.fragment_settings,container,false);

        initFinder();
        initDatabase();
        initListener();

        singleEdit.setText(myDataBases.getSingle(db));
        doubleEdit.setText(myDataBases.getDouble(db));
        longPressEdit.setText(myDataBases.getLongPress(db));







        return settingsView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void initFinder(){
        singleEdit=settingsView.findViewById(R.id.SingleTapEdit);
        doubleEdit=settingsView.findViewById(R.id.DoubleTapEdit);
        longPressEdit=settingsView.findViewById(R.id.LongPressEdit);

        save=settingsView.findViewById(R.id.saveButton);

        upper=settingsView.findViewById(R.id.upper);
        unimportant=settingsView.findViewById(R.id.unimportant);

        updateValues = new ContentValues();
    }

    private void initListener(){
        upper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (upper.isChecked()) {
                    Toast.makeText(getContext(),
                            getString(R.string.delete_on_post_notification), Toast.LENGTH_SHORT).show();
                    upper.setChecked(false);
                }
            }
        });

        unimportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (unimportant.isChecked()) {
                    Toast.makeText(getContext(),
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
            if (!singletap.equals("")) updateValues.put("single", singletap);
            else updateValues.put("single",0);

            if (!doubletap.equals("")) updateValues.put("double", doubletap);
            else updateValues.put("double",0);

            if (!longpress.equals("")) updateValues.put("longpress", longpress);
            else updateValues.put("longpress",0);

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
                Toast.makeText(getContext(),getString(R.string.db_s),Toast.LENGTH_SHORT).show();
            }else Toast.makeText(getContext(),getString(R.string.db_f),Toast.LENGTH_SHORT).show();




        });

    }
    private void initDatabase(){
        myDataBases =new MyDataBases(getContext(),dataBaseName,null,dataBaseVersion);
        db = myDataBases.getWritableDatabase();
    }

    // TODO: 2025/3/13 检查更新 



}