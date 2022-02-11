package com.proteam.showroomaudit.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.proteam.showroomaudit.R;
import com.proteam.showroomaudit.databinding.ActivityMainBinding;
import com.proteam.showroomaudit.databinding.DashboardLayoutBinding;
import com.proteam.showroomaudit.databinding.NavigationLayoutBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    DashboardLayoutBinding dashboardLayoutBinding;
    TextView logout;
    SharedPreferences sh;
    CheckBox cb_itemval, cb_rackval, cb_gcval, cb_itemlen_val;
    boolean gc ,item,rack,length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        dashboardLayoutBinding=mainBinding.mainLayout;
        logout = findViewById(R.id.tv_logout);
        cb_gcval = findViewById(R.id.ch_global_validation);
        cb_itemval = findViewById(R.id.ch_item_validation);
        cb_rackval = findViewById(R.id.ch_rack_validation);
        cb_itemlen_val =findViewById(R.id.ch_item_length);



         sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        String s1 = sh.getString("name", null);

        if(s1==null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        dashboardLayoutBinding.tvUser.setText(s1);

        dashboardLayoutBinding.tvNavMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainBinding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        dashboardLayoutBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 gc = cb_gcval.isChecked();
                 item = cb_itemval.isChecked();
                 rack = cb_rackval.isChecked();
                length = cb_itemlen_val.isChecked();

                if(length){
                    openCustomDialog();
                }else{
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    intent.putExtra("gc",gc);
                    intent.putExtra("item",item);
                    intent.putExtra("rack",rack);
                    intent.putExtra("length",length);
                    startActivity(intent);
                }

            }
        });

   /*     mainBinding.tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });*/

        mainBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              editor.clear();
              editor.commit();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mainBinding.tvEmail.setText(s1);


    }


    private void openCustomDialog()
    {
        final Dialog dialog =new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.activity_lengthval);
        dialog.show();
        EditText et_user = dialog.findViewById(R.id.edt_barcode);
        Button bt_login = dialog.findViewById(R.id.btn_submit1);


        Boolean state = false;

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("gc",gc);
                intent.putExtra("item",item);
                intent.putExtra("rack",rack);
                intent.putExtra("length",length);
                intent.putExtra("barcode",et_user.getText().toString());
                dialog.dismiss();
                startActivity(intent);



            }
        });


    }
}