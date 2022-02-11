package com.proteam.showroomaudit.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.proteam.showroomaudit.PoiActivity;
import com.proteam.showroomaudit.R;
import com.proteam.showroomaudit.databinding.ActivityLoginBinding;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button import_btn;
    public static final int requestcode = 1;
    static String tableName;
    SqlDb sqlDb;
    Cursor cursor;
    String directory_path = Environment.getExternalStorageDirectory().getPath() + "com.oneplus.filemanager/Storage/Internal storage/testing/userlist.xlsx";
    private final int CHOOSE_PDF_FROM_DEVICE = 1001;
    EditText et_name,et_pass;
    private List mProductList = new ArrayList();

    ArrayList list = new ArrayList();
    List list2 = new ArrayList();
    Button submitbtn;
    Button adminbtn;


    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submitbtn=findViewById(R.id.btn_submit);
        adminbtn=findViewById(R.id.btn_admin_login);

        requestForPermission();
        sqlDb = new SqlDb(this);
        initilize();





        //mProductList = mDBHelper.getListProduct();

       // int dbsige= mDBHelper.getListProduct().size();
       // System.out.println("size"+ dbsige);

    }

    private void initilize() {

        import_btn = findViewById(R.id.btn_import_user);
        et_name = findViewById(R.id.edt_user);
        et_pass = findViewById(R.id.edt_password);


        /*import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // Intent intent = new Intent(LoginActivity.this, PoiActivity.class);
                //startActivity(intent);

                if(mProductList.contains("3.30283155009E11")){
                    System.out.println("data"+ "data present 3.30283155009E11 ");
                }
                int dbsige=mProductList.size();
                System.out.println("size"+ dbsige);


               cursor = mDBHelper.getAllData();
                if(cursor.getCount()==0){
                    Toast.makeText(LoginActivity.this,"No data in database",Toast.LENGTH_LONG).show();
                }else {

                    while (cursor.moveToNext()){
                        mProductList.add(cursor.getString(0));
                    }

                    Log.e("size", String.valueOf(mProductList.size()));


                }

                List currentTasks = new ArrayList();
                SharedPreferences prefs = getSharedPreferences("myPref", Context.MODE_PRIVATE);

                try {
                    currentTasks = (ArrayList) Serilize.deserialize(prefs.getString("itemArray", Serilize.serialize(new ArrayList())));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


                Log.e("current task", String.valueOf(currentTasks.size()));
                Log.e("item", String.valueOf(currentTasks.get(123456)));

                   //getfilepath();
               // Intent intent = new Intent(LoginActivity.this,Addlistactivity.class);
                //startActivity(intent);
            }
        });*/

        adminbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AdminLogin.class);
                startActivity(intent);
            }
        });


        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor = sqlDb.getAllData();
                String etname = et_name.getText().toString().trim().toLowerCase();
                String etpass = et_pass.getText().toString().trim().toLowerCase();

                if(cursor.getCount()==0){
                    Toast.makeText(LoginActivity.this,"Add user data first",Toast.LENGTH_LONG).show();
                }else {

                    if(!etname.equals("")){

                        if(!etpass.equals("")){

                            while (cursor.moveToNext()){
                                String name = cursor.getString(0);
                                String pass = cursor.getString(1);
                                String auditcode = cursor.getString(3);

                                System.out.println("name"+name);
                                 if(!name.equals(null)){

                                    if(!pass.equals(null)){

                                        if(name.equals(etname) && pass.equals(etpass)){

                                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                            myEdit.putString("name", name);
                                            myEdit.putString("auditcode",auditcode);
                                            myEdit.commit();
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }

                        }else {
                            Toast.makeText(LoginActivity.this,"Enter password",Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(LoginActivity.this,"Enter Name",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }




    private void getfilepath() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,CHOOSE_PDF_FROM_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
           // Toast.makeText(getApplicationContext(), (CharSequence) data,Toast.LENGTH_LONG).show();
        }

        Log.e("path", data.getData().getPath());
        System.out.println("path"+data.getData().getPath());


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    String line = null;
                    FileInputStream fileInputStream = new FileInputStream (new File(data.getData().getPath()));

                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (line = bufferedReader.readLine()) != null )
                    {

                        list2.add(line);
                        Log.e("string", line );
                        //stringBuilder.append(line + System.getProperty("line.separator"));
                    }
                    fileInputStream.close();
                    line = stringBuilder.toString();
                    Log.e("line size", String.valueOf(list2.size()) + ",,," + list2.get(2100));

                    bufferedReader.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        //readinotherformate();
       // readExcelFileFromAssets(data.getData().getPath());
        /*File file = new File(data.getData().getPath());


        if (!file.exists()) {
            //Utils.showSnackBar(view, "No file");
            Log.e("path", String.valueOf(file));
            Toast.makeText(getApplicationContext(),"No File",Toast.LENGTH_LONG).show();
            return;
        }*/

        //read data
        /*ExcelToSQLite excelToSQLite = new ExcelToSQLite(getApplicationContext(), sqlDb.DATABASE_NAME, false);
        excelToSQLite.importFromFile(data.getData().getPath(), new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {
                Toast.makeText(LoginActivity.this,"Import started",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompleted(String dbName) {

                Log.e("dbname", dbName);
                Toast.makeText(LoginActivity.this,"Imported successfully"+ dbName,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("error", e.getMessage());
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/


        /*excelToSQLite.importFromAsset("racklist.xls", new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {
                Toast.makeText(LoginActivity.this,"Import started",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompleted(String dbName) {

                Log.e("dbname", dbName);
                Toast.makeText(LoginActivity.this," rack number Imported successfully"+ dbName,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                Log.e("error", e.getMessage());
                Toast.makeText(LoginActivity.this,e.getMessage() + "error in rackincert",Toast.LENGTH_LONG).show();
            }
        });*/

    }






    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {

        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }



}