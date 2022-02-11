package com.proteam.showroomaudit.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVReader;
import com.proteam.showroomaudit.R;
import com.proteam.showroomaudit.views.utility.OnResponseListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UploadListActivity extends AppCompatActivity implements View.OnClickListener , OnResponseListener {

    private final int CHOOSE_PDF_FROM_DEVICE = 1001;

    Button itemslist,racklist,userlist, btn_clear;

    List listitem = new ArrayList();
    SqlDb sqlDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);

        itemslist = findViewById(R.id.btn_import_item_list);
        racklist = findViewById(R.id.btn_import_rack_list);
        userlist = findViewById(R.id.btn_import_user);
        btn_clear = findViewById(R.id.btn_clear_list);
        btn_clear.setOnClickListener(this);
        userlist.setOnClickListener(this);
        racklist.setOnClickListener(this);
        itemslist.setOnClickListener(this);
        sqlDb = new SqlDb(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_import_item_list:
                getfilepath();
                break;

            case R.id.btn_import_rack_list:
                getfilepath2();
                break;

            case R.id.btn_import_user:
                getfilepath3();
                break;

            case R.id.btn_clear_list:
                clearalldata();
                break;

        }
  }


    private void getfilepath3() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,321);
    }

    private void getfilepath2() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,123);
    }

    private void getfilepath() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,CHOOSE_PDF_FROM_DEVICE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(UploadListActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
        Uri uri = data.getData();
        File originalFile = new File(FileUtils.getRealPath(this,uri));
        System.out.println("path======"+originalFile);

        String path1 = data.getData().getPath();
        String path2 = path1.replace("/document/raw:", "");

        Log.e("path", path2);
        System.out.println("path" + path2);


        if (data.getData().getPath().endsWith(".csv")) {

            listitem.clear();

            if (data == null) {
                Toast.makeText(getApplicationContext(), "no data in file", Toast.LENGTH_LONG).show();
            }

            if (requestCode == CHOOSE_PDF_FROM_DEVICE) {



                Log.e("path", data.getData().getPath());
                System.out.println("path" + data.getData().getPath());

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {


                        String line = null;

                        try {
                            CSVReader reader = new CSVReader(new FileReader(path2));
                            String[] nextLine;

                            String dat[] = reader.readNext();


                            while ((nextLine = reader.readNext()) != null) {
                                // nextLine[] is an array of values from the line
                                listitem.add(nextLine[0].toLowerCase());
                                //System.out.println(nextLine[0]);
                            }

                            SharedPreferences prefs = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("itemArray", Serilize.serialize(listitem));
                            editor.commit();
                            Log.e("listitmsize", String.valueOf(listitem.size()));
                            Log.e("line size", "inserted successfully");

                            //Toast.makeText(UploadListActivity.this, "Item list readed successfully", Toast.LENGTH_LONG).show();
                            Log.e("last item", "inserted successfully");
                        } catch (IOException e) {
                            //Toast.makeText(UploadListActivity.this, "not uploaded   :  " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("error", e.getMessage());
                        }
                    }
                });
            } else if (requestCode == 123) {


                try {
                    CSVReader reader = new CSVReader(new FileReader(originalFile));
                    String[] nextLine;

                    int dat = reader.readNext().length;
                    if (dat == 2) {

                        while ((nextLine = reader.readNext()) != null) {
                            // nextLine[] is an array of values from the line

                            Boolean isIncerted = sqlDb.insertitemslist(nextLine[0].toLowerCase(), nextLine[1]);
                            if (isIncerted) {

                            } else {
                                Toast.makeText(UploadListActivity.this, "not inserted rack list", Toast.LENGTH_LONG).show();
                            }

                            System.out.println(nextLine[0] + "," + nextLine[1]);
                        }

                        Toast.makeText(UploadListActivity.this, "Rack list readed successfully", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(UploadListActivity.this, "you uploaded wrong file", Toast.LENGTH_LONG).show();
                    }


                } catch (IOException e) {

                    Toast.makeText(UploadListActivity.this, "not uploaded   :  " + e.getMessage(), Toast.LENGTH_LONG).show();

                }


            } else if (requestCode == 321) {

                try {
                    CSVReader reader = new CSVReader(new FileReader(originalFile));
                    String[] nextLine;

                    int dat = reader.readNext().length;
                    if (dat == 4) {

                        while ((nextLine = reader.readNext()) != null) {
                            // nextLine[] is an array of values from the line


                            Boolean isIncerted = sqlDb.insertData(nextLine[0].toLowerCase(), nextLine[1].toLowerCase(), nextLine[2], nextLine[3]);
                            if (isIncerted) {
                                // Toast.makeText(UploadListActivity.this,"Imported successfully",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UploadListActivity.this, "User list not imported", Toast.LENGTH_LONG).show();
                            }

                            System.out.println(nextLine[0] + "," + nextLine[1]);
                        }
                    } else {
                        Toast.makeText(UploadListActivity.this, "you uploaded wrong file", Toast.LENGTH_LONG).show();

                    }
                    Toast.makeText(UploadListActivity.this, "user list readed successfully", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    Toast.makeText(UploadListActivity.this, "not uploaded :  " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        } else {

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "upload only .csv file", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
    }



    private void clearalldata() {

        Boolean clear=sqlDb.clearalldata();
        if(clear){

            Toast.makeText(UploadListActivity.this,"cleard",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(UploadListActivity.this,"not cleard",Toast.LENGTH_LONG).show();
        }

        listitem.clear();
        SharedPreferences prefs = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("itemArray", Serilize.serialize(listitem));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();

        File dir = new File("storage/emulated/0/Documents");
        for(File tempFile : dir.listFiles()) {
            tempFile.delete();
        }
    }



    @Override
    public void onResponse(Object response, WebServices.ApiType URL, boolean isSucces, int code) {

    }
}