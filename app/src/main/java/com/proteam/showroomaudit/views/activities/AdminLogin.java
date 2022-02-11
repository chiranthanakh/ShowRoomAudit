package com.proteam.showroomaudit.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.proteam.showroomaudit.R;

public class AdminLogin extends AppCompatActivity {

    EditText et_user, et_pass;
    Button submit;
    boolean isPassValid, isUserValid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        et_user = findViewById(R.id.edt_admin_user);
        et_pass = findViewById(R.id.edt_admin_password);
        submit = findViewById(R.id.btn_admin_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();

                //Intent intent = new Intent(AdminLogin.this, UploadListActivity.class);
                //startActivity(intent);

            }
        });

    }

    public void SetValidation()
    {
        if (et_user.getText().toString().isEmpty()) {
            et_user.setError(getResources().getString(R.string.email_error));
            isUserValid = false;
        }
        else
        {
            isUserValid=true;
        }
        if (et_pass.getText().toString().isEmpty()) {
            et_pass.setError(getResources().getString(R.string.pass_error));
            isPassValid = false;
        }
        else {
            isPassValid= true;
        }
        if (isUserValid && isPassValid) {

           if(et_user.getText().toString().equals("Admin") && et_pass.getText().toString().equals("Admin123") ){
               Intent intent = new Intent(AdminLogin.this, UploadListActivity.class);
               startActivity(intent);
           }else{
               Toast.makeText(AdminLogin.this,"login fails",Toast.LENGTH_LONG).show();
           }

        }
    }


}