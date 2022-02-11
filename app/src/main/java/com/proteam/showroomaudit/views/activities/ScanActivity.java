package com.proteam.showroomaudit.views.activities;

import static com.proteam.showroomaudit.views.activities.WebServices.ApiType.auditdata;
import static com.proteam.showroomaudit.views.activities.WebServices.ApiType.checkmodel;

import static java.lang.Integer.parseInt;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import com.google.android.material.snackbar.Snackbar;
import com.opencsv.CSVWriter;
import com.proteam.showroomaudit.R;
import com.proteam.showroomaudit.databinding.ActivityScanBinding;
import com.proteam.showroomaudit.databinding.BarcodeLayoutBinding;
import com.proteam.showroomaudit.databinding.DialogeLoginBinding;
import com.proteam.showroomaudit.views.utility.AuditApi;
import com.proteam.showroomaudit.views.utility.OnResponseListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener , OnResponseListener {


    ListView list;
    SqlDb sqlDb;
    Cursor cursor;


    ActivityScanBinding scanBinding;
    Context context=this;
    DialogeLoginBinding dialogeLoginBinding;
    BarcodeLayoutBinding barcodeLayoutBinding;
    EditText et_audit,et_rack,et_item,et_quentity,et_tagged;
    TextView totalrackcount,totalitemcount,totalrack,previousitem, syncdata,tv_user;
    Button btn_val_rack,btn_audit_val,btn_item_val,btn_rescan,btn_sync,btn_quentity;
    ElegantNumberButton numberbtn;
    LinearLayout synclayout;
    private List mProductList = new ArrayList();
    TextToSpeech textToSpeech;
    SimpleDateFormat sdf;
    SharedPreferences sh;
    String s1,auditcode;
    CheckBox cb_quentity;
    int l;
    View progressDialog;


    ArrayList racklist = new ArrayList();
    ArrayList<Listviewmodel> itemlist = new ArrayList();
    ArrayList itemsnumberslist = new ArrayList();
    List gcvalarray = new ArrayList();
    List currentitemlist = new ArrayList();
    HashMap gcmap = new HashMap();
    int racksize = 0;
    String rn="a";
    ArrayList lis;
    Boolean gc_val;
    Boolean item_val;
    Boolean rack_val, length;
    ProgressBar mProgressBar;
    SharedPreferences racksharedPreferences;
    String barcode_length;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanBinding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(scanBinding.getRoot());
        dialogeLoginBinding = scanBinding.dialogeLogin;
        sqlDb = new SqlDb(this);



        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        s1 = sh.getString("name", null);
        auditcode = sh.getString("auditcode",null);
        System.out.println("auditcode------"+auditcode);


        initilize();
        getrackdata();
        getScanneddata();
        //getItemlist();

        Intent in = getIntent();
        Bundle extras = in.getExtras();
         gc_val = extras.getBoolean("gc");
         item_val = extras.getBoolean("item");
         rack_val = extras.getBoolean("rack");
         length = extras.getBoolean("length");
         barcode_length = extras.getString("barcode",null);




         if(item_val){
             AsyncTask.execute(new Runnable() {
                 @Override
                 public void run() {

                     getItemlist();
                 }
             });
         }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    //views initilization part
    private void initilize() {

        et_audit = findViewById(R.id.edt_audit);
        et_rack = findViewById(R.id.edt_rackcode);
        et_item = findViewById(R.id.edt_itemnumber);
        et_quentity = findViewById(R.id.edt_quentity);
        et_tagged = findViewById(R.id.edt_untag);
        btn_val_rack = findViewById(R.id.btn_rack_validation);
        btn_audit_val = findViewById(R.id.btn_audit_code_validate);
        btn_item_val =findViewById(R.id.btn_item_val);
        btn_rescan = findViewById(R.id.btn_rescan);
        btn_quentity = findViewById(R.id.btn_quantity);
        numberbtn = findViewById(R.id.numberbtn);
        btn_sync = findViewById(R.id.btn_sync);
        progressDialog = findViewById(R.id.progressBar);
        previousitem = findViewById(R.id.tv_previousitemscanned);
        totalrack = findViewById(R.id.tv_totalrack);
        totalrackcount = findViewById(R.id.tv_totalrackscount);
        syncdata = findViewById(R.id.tv_sync_data);
        totalitemcount = findViewById(R.id.tv_totalitemcount);
        cb_quentity = findViewById(R.id.ch_item_quentity);
       // progressDialog.setVisibility(View.VISIBLE);
        synclayout = findViewById(R.id.synclayout);
        tv_user = findViewById(R.id.tv_user);
        et_quentity.setText("1");
        et_tagged.setText("0");
        tv_user.setText(s1);

        et_item.setInputType(InputType.TYPE_NULL);
        et_rack.setInputType(InputType.TYPE_NULL);

        /*if (Build.VERSION.SDK_INT < 19) {
            et_item.setInputType(InputType.TYPE_NULL);
            et_rack.setInputType(InputType.TYPE_NULL);
        }else {

        }*/

        //hideKeyboard(this);
        et_rack.setOnClickListener(this);
        et_item.setOnClickListener(this);
        btn_val_rack.setOnClickListener(this);
        btn_audit_val.setOnClickListener(this);
        btn_item_val.setOnClickListener(this);
        btn_rescan.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        btn_quentity.setOnClickListener(this);

        et_rack.addTextChangedListener(textWatcher);
        et_item.addTextChangedListener(textWatcher);

        et_audit.setText(auditcode);

        scanBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scanBinding.ivBack.setOnClickListener(view -> onBackPressed());

        scanBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gc_val){

                    gcvalidation("valid");
                    //openSaveDialogforgcmissmatch();

                }else {
                    openSaveDialog();
                }

               // validateedittext();
            }

        });

        synclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean internet=Utilities.isConnectedToInternet(ScanActivity.this);
                if(internet){

                    progressDialog.setVisibility(View.VISIBLE);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {

                            syncdata();
                        }
                    });

                    //

                }else {

                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No internet connection, Please connect to internet", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }



            }
        });


        numberbtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                et_tagged.setText(String.valueOf(newValue));
            }
        });

        et_item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //hideKeyboard(ScanActivity.this);
                return false;
            }
        });


        MyListAdapter adapter = new MyListAdapter(this,itemlist);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);


    }


    ///////////////////////////////////get data from sqlite//////////////////////////////////////////////////////

    private void getItemlist() {

        SharedPreferences prefs = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        try {
                    currentitemlist = (ArrayList) Serilize.deserialize(prefs.getString("itemArray", Serilize.serialize(new ArrayList())));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (currentitemlist.size()==0){

            //Toast.makeText(ScanActivity.this,"please add item list",Toast.LENGTH_LONG).show();
        }


        Log.e("current task", String.valueOf(currentitemlist.size()));
        //Log.e("item", String.valueOf(currentitemlist.get(123456)));
    }

    private void getrackdata() {
        cursor = sqlDb.getAllDatarack();
        String rackid = et_rack.getText().toString();
        racklist.clear();

        if(cursor.getCount()==0){

        }else{
            while (cursor.moveToNext()){

                racklist.add(cursor.getString(0).toLowerCase());
                gcvalarray.add(cursor.getString(0)+"-"+cursor.getString(1));
                gcmap.put(cursor.getString(0).toLowerCase(),cursor.getString(1));

                System.out.println("qwer : "+cursor.getString(0)+"-"+cursor.getString(1));
            }
            racksize=racklist.size();
            //totalrack.setText(String.valueOf("Total racks : " +racklist.size()));
            totalrackcount.setText(String.valueOf(racklist.size()));
        }
    }

    private void getScanneddata() {

        cursor = sqlDb.getalldataofaudit();

        totalitemcount.setText(String.valueOf(cursor.getCount()));
        int rackremaining = 0;
        lis =new ArrayList();
        lis.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(ScanActivity.this, "No data", Toast.LENGTH_LONG).show();

        } else {
            int i=0;
            Log.e("i",String.valueOf(i));

            while (cursor.moveToNext()) {
                Listviewmodel listviewmodel = new Listviewmodel();
                    previousitem.setText(cursor.getString(3));
                if(lis.contains(cursor.getString(2))){

                }else {
                    lis.add(cursor.getString(2));
                    Log.e("scannedracks",cursor.getString(2));
                    Log.e("lis size", String.valueOf(lis.size()));

                }

            }
            totalrack.setText(String.valueOf(lis.size()));

        }
    }




    ////////////////////////////////////////validation part ////////////////////////////////////////////////

    //item validation
    private void validateedittext() {

        if(!et_audit.getText().toString().trim().equals("")){

            if(!et_rack.getText().toString().trim().equals("")){

                if(!et_item.getText().toString().trim().equals("")){

                    System.out.println("itemnumber"+ et_item.getText().toString());
                    if(!et_quentity.getText().toString().trim().equals("")){

                        if(!et_tagged.getText().toString().trim().equals("")){

                            if(item_val){

                                System.out.println("itemnumber"+ et_item.getText().toString());
                                String itemforcheck = et_item.getText().toString().trim().toLowerCase();

                                System.out.println("currentlistdata----"+ currentitemlist.get(3));
                                System.out.println("currentlistdata----"+ currentitemlist.get(4));
                                System.out.println("currentlistdata----"+ itemforcheck);
                                if(currentitemlist.contains(itemforcheck)){

                                    //gc validation commenter
                                    //gcvalidation("valid");
                                    incertdata("valid");
                                    System.out.println("data"+ "valid item");
                                }else{

                                    openDialog2(et_item.getText().toString().trim());
                                    textToSpeech.speak("invalid",TextToSpeech.QUEUE_FLUSH,null);
                                    System.out.println("data"+ "data invalid");
                                }

                            }else {
                                incertdata("valid");
                              // gcvalidation("valid");

                            }

                        }else{
                            Toast.makeText(ScanActivity.this,"enter untagged item",Toast.LENGTH_LONG).show();
                           // Log.e("untag","untag");
                        }


                    }else{
                        Toast.makeText(ScanActivity.this,"Enter item quentity",Toast.LENGTH_LONG).show();
                       // Log.e("untag","quentity");
                    }

                }else{
                   // Toast.makeText(ScanActivity.this,"Scan item code",Toast.LENGTH_LONG).show();
                    Log.e("untag"," item");
                }

            }else{
                Toast.makeText(ScanActivity.this,"enter rack id",Toast.LENGTH_LONG).show();
                Log.e("untag","rack");
            }


        }else{
            Toast.makeText(ScanActivity.this,"enter audit code",Toast.LENGTH_LONG).show();
            Log.e("untag","audit");
        }

    }

    //rack validation
    private void rackvalidation() {

        cursor = sqlDb.getrackdataofaudit();
        //to save the data of previous rack
        if (cursor.getCount() == 0) {

        String rackid = et_rack.getText().toString().trim().toLowerCase();
        String racname;
        System.out.println("asd  : " + rackid);
        rackid = rackid.replace("\n", "");

        if (!racklist.contains(rackid) && rack_val == true) {
            aleartdialog();
            //openDialog();
        } else if ((lis.contains(rackid)) && (lis.get(lis.size() - 1) != rackid) && (rack_val == true)) {

            openDialog();
            Log.e("norack", "no rack found");
        } else {

            et_item.setFocusable(true);
            et_item.setFocusableInTouchMode(true);
            et_item.requestFocus();
            et_item.requestFocus(1);
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Valid rack, please scan items", Snackbar.LENGTH_LONG);
            snackbar.show();
            racksharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = racksharedPreferences.edit();
            myEdit.putString("rackname", et_rack.getText().toString().trim().toLowerCase());
            myEdit.commit();

            //hideKeyboard(this);

        }

        //Log.e("racks", String.valueOf(racksize)+","+String.valueOf(i));
        Log.e("rn", rn);
    }else{
            Toast.makeText(ScanActivity.this,"Please save the data of previous rack",Toast.LENGTH_LONG).show();
            et_rack.setText("");
        }
    }

    //gc validation
    private void gcvalidation(String state) {


        SharedPreferences sh1 = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        String rackname = sh1.getString("rackname", null);


            cursor = sqlDb.getrackdataofaudit();
            if (cursor.getCount() == 0) {
                Toast.makeText(ScanActivity.this,"No data to save, please scan then save",Toast.LENGTH_LONG).show();


            }else {
                int gc = cursor.getCount();

                String gcvalue = String.valueOf(gcmap.get(rackname));
                System.out.println("gcval   ---"+ rackname);

                if (gcvalue != null) {

                if(gc== parseInt(gcvalue)){

                    openSaveDialog();

                    System.out.println("gcvalidation"+ gc);
                }else {

                    openSaveDialogforgcmissmatch(gc, Integer.parseInt(gcvalue));
                    //opengcmissCustomDialog(gc, Integer.parseInt(gcvalue) );
                    System.out.println("gcvalidation"+"---"+ Integer.parseInt(gcvalue));

                }}
            }

    }


    //item validat
    private void incertdata(String valid){


             System.out.println("ettext!!!!!!"+ et_item.getText().toString().length());
            sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());


            Boolean isincerted = sqlDb.insertSeckond(et_audit.getText().toString().trim(),
                et_rack.getText().toString().trim().toLowerCase(),et_item.getText().toString().trim().toLowerCase(),valid, currentDateandTime, s1,et_quentity.getText().toString().trim());

             Boolean isincerted2 = sqlDb.insertrackdata(et_audit.getText().toString(),
                et_rack.getText().toString().trim().toLowerCase(),et_item.getText().toString().trim().toLowerCase(),valid, currentDateandTime, s1,et_quentity.getText().toString().trim());

            if(isincerted){
           // textToSpeech.speak("incerted",TextToSpeech.QUEUE_FLUSH,null);
            getScanneddata();
            Toast.makeText(ScanActivity.this,"done",Toast.LENGTH_LONG).show();
            Log.e("untag","untag");
            }else {
                textToSpeech.speak("Not incerted",TextToSpeech.QUEUE_FLUSH,null);
            Toast.makeText(ScanActivity.this,"not incerted",Toast.LENGTH_LONG).show();
            }

    }


    //this method is for some special cases
    private void incertdata2(String valid){


        System.out.println("ettext!!!!!!"+ et_item.getText().toString().length());
        sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());


        Boolean isincerted = sqlDb.insertSeckond(et_audit.getText().toString(),
                et_rack.getText().toString().trim().toLowerCase(),valid,"invalid", currentDateandTime, s1,et_quentity.getText().toString().trim());

        Boolean isincerted2 = sqlDb.insertrackdata(et_audit.getText().toString(),
                et_rack.getText().toString().trim().toLowerCase(),valid,"invalid", currentDateandTime, s1,et_quentity.getText().toString().trim());

        if(isincerted){
            // textToSpeech.speak("incerted",TextToSpeech.QUEUE_FLUSH,null);
            getScanneddata();
            Toast.makeText(ScanActivity.this,"done",Toast.LENGTH_LONG).show();
            Log.e("untag","untag");
        }else {
            textToSpeech.speak("Not incerted",TextToSpeech.QUEUE_FLUSH,null);
            Toast.makeText(ScanActivity.this,"not incerted",Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_rack_validation:

                //getrackdata();
                //rackvalidation();
                //hideKeyboard(this);

                break;

            case R.id.btn_audit_code_validate:
                hideKeyboard(this);
                et_rack.setFocusable(true);
               //et_rack.requestFocus();
                et_rack.setFocusableInTouchMode(true);
                //et_rack.setCursorVisible(true);
                //et_rack.setInputType(InputType.TYPE_NULL);
                //disableSoftInputFromAppearing(et_rack);
                break;

            case R.id.btn_rescan:
                openCustomDialog();
                break;

            case R.id.edt_itemnumber:
                //openCustomDialog();
                  //hideKeyboard(ScanActivity.this);
                break;

            case R.id.edt_rackcode:
                //openCustomDialog();
                //et_rack.getText().clear();
                //hideKeyboard(ScanActivity.this);
                break;
            case R.id.btn_quantity:


                if(length){
                    if(l== parseInt(barcode_length)){
                        validateedittext();
                        et_item.getText().clear();
                    }else {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Item length not matching", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        et_item.getText().clear();
                    }

                }else{
                    validateedittext();
                    et_quentity.setText("1");
                    et_item.getText().clear();
                    et_item.requestFocus();
                }

        }
    }

    private void exportdatabase() {

        SharedPreferences sh1 = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        String rackname = sh1.getString("rackname", null);


        cursor = sqlDb.getrackdataofaudit();

        File exportDir = new File(Environment.getExternalStorageDirectory(), "Download");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }



        File file = new File(exportDir, s1+"_"+rackname+".csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            cursor = sqlDb.getrackdataofaudit();
            csvWrite.writeNext(cursor.getColumnNames());
            while(cursor.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={cursor.getString(0),cursor.getString(1), cursor.getString(2),
                                   cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            cursor.close();

            Boolean isIncerted=sqlDb.deleterackdata();

            if(isIncerted){

            }else {
                Toast.makeText(ScanActivity.this,"Racks are not cleard, pls save again",Toast.LENGTH_LONG).show();
            }

        }
        catch(Exception sqlEx)
        {
            Log.e("excel export error", sqlEx.getMessage(), sqlEx);
        }

    }

    private void openSuccessDialog()
    {
        final Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.success_alert);
        Button exit=dialog.findViewById(R.id.btn_success_continue);
        dialog.show();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    //////////////////////////////////////////aleart dialog //////////////////////////////////////////////////
    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aleart");
        builder.setMessage("Rack alredy Scanned, you need to rescan");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

                Boolean isIncerted = sqlDb.deleterack(et_rack.getText().toString().trim().toLowerCase());
                sqlDb.deleterackdata();
                getScanneddata();

                if(isIncerted){
                    Toast.makeText(ScanActivity.this,"Rack item data deleted, rescan all items again",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ScanActivity.this,"Racks are not cleard",Toast.LENGTH_LONG).show();
                }

                racksharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = racksharedPreferences.edit();
                myEdit.putString("rackname", et_rack.getText().toString().trim().toLowerCase());
                myEdit.commit();
                et_item.requestFocus();
               et_item.setFocusable(true);
                et_item.setFocusableInTouchMode(true);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                et_rack.setText("");
                et_item.setFocusable(false);
                et_item.setFocusableInTouchMode(false);

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void openDialoggcval() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aleart");
        builder.setMessage("You reached GC limit, you not allowed to scan in this rack");
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                et_item.setFocusable(false);
                et_item.setFocusableInTouchMode(false);

            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void openDialog2(String it) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aleart");
        builder.setMessage("Invalid item, Do you need to insert");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                /*if(gc_val){
                    gcvalidation2(it);
                }else {
                    incertdata2(it);
                }*/

                incertdata2(it);
                dialog.cancel();



                //et_item.setFocusable(true);
                //et_item.setFocusableInTouchMode(true);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();


            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void openSaveDialog() {

        final Dialog dialog =new Dialog(context);

        dialog.setContentView(R.layout.dialog_save);
        dialog.show();
        EditText et_untag = dialog.findViewById(R.id.edt_untag);
        Button bt_login = dialog.findViewById(R.id.btn_save_submit);
        et_untag.setText("0");

        Boolean state = false;

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor = sqlDb.getalldataofaudit();
                String audit = "";
                String rack = "";

                if(cursor.getCount()==0){
                    Toast.makeText(ScanActivity.this, "No Audit data in database", Toast.LENGTH_LONG).show();
                }else{

                    while (cursor.moveToNext()){

                        audit = cursor.getString(1);
                        rack = cursor.getString(2);
                    }
                }

                cursor.close();

                cursor = sqlDb.getrackdataofaudit();

                Boolean isIncerted=sqlDb.insertSummery(s1,audit,rack,String.valueOf(racksize),String.valueOf(lis.size()),String.valueOf(cursor.getCount()),et_untag.getText().toString(),"4","4");
                if(isIncerted){

                    Toast.makeText(ScanActivity.this,"inserted",Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(ScanActivity.this,"not inserted",Toast.LENGTH_LONG).show();
                }

                exportdatabase();
                dialog.dismiss();
                et_rack.requestFocus();
                et_rack.setText("");

            }
        });

    }


    private void openSaveDialogforgcmissmatch(int scanned, int gc) {

        final Dialog dialog =new Dialog(context);

        dialog.setContentView(R.layout.dialog_save);
        dialog.show();
        EditText et_untag = dialog.findViewById(R.id.edt_untag);
        Button bt_login = dialog.findViewById(R.id.btn_save_submit);
        et_untag.setText("0");

        Boolean state = false;

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursor = sqlDb.getalldataofaudit();
                String audit = "";
                String rack = "";



                dialog.dismiss();
                int total = scanned+parseInt(et_untag.getText().toString());

                if(gc==total){
                    exportdatabase();
                    et_rack.requestFocus();
                     et_rack.setText("");
                }else{
                    opengcmissCustomDialog(gc, total,et_untag.getText().toString().trim());
                }

               // et_rack.requestFocus();
               // et_rack.setText("");

            }
        });


    }


    private void openCustomDialog() {
        final Dialog dialog =new Dialog(context);

        dialog.setContentView(R.layout.dialoge_login);
        dialog.show();
        EditText et_rac = dialog.findViewById(R.id.edt_rk);
        EditText et_user = dialog.findViewById(R.id.edt_user);
        EditText et_pass = dialog.findViewById(R.id.edt_password);
        Button bt_login = dialog.findViewById(R.id.btn);
        cursor = sqlDb.getAllData();

        Boolean state = false;

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(ScanActivity.this);
                if (et_rac.getText().toString().trim().toLowerCase().equals("") || et_user.getText().toString().trim().equals("") || et_pass.getText().toString().trim().equals("")) {

                    Toast.makeText(ScanActivity.this,"please enter all fields",Toast.LENGTH_LONG).show();

                }else{

                    if (cursor.getCount()==0){

                        Toast.makeText(ScanActivity.this,"No scanned racks",Toast.LENGTH_LONG).show();

                    }else {

                        Boolean state = true;
                        while (cursor.moveToNext()) {

                            System.out.println(cursor.getString(2));


                            if (et_user.getText().toString().equals(cursor.getString(0))
                                    && et_pass.getText().toString().equals(cursor.getString(1)) &&
                                    cursor.getString(2).equalsIgnoreCase("teamlead")) {

                                Boolean isIncerted = sqlDb.deleterack(et_rac.getText().toString());
                                getScanneddata();
                                state =false;
                                if(isIncerted){
                                    lis.remove(et_rac.getText().toString());
                                    Toast.makeText(ScanActivity.this,"Rack item data cleard, rescan all items in rack",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(ScanActivity.this,"Racks are not cleard",Toast.LENGTH_LONG).show();
                                }

                                dialog.dismiss();

                            }else{
                            }
                        }
                        if(state) {
                            dialog.dismiss();
                            Toast.makeText(ScanActivity.this, "You are not allowed to rescan", Toast.LENGTH_LONG).show();
                        }

                    }


                    //Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "you are not allowed to rescan", Snackbar.LENGTH_LONG);
                    //snackbar.show();

                }


            }
        });
    }

    ////rescan dialog box
    private void opengcmissCustomDialog(int gc , int acgc , String untag) {
        final Dialog dialog =new Dialog(context);

        dialog.setContentView(R.layout.dialoge_gcteamlead);
        dialog.show();

        EditText et_user = dialog.findViewById(R.id.edt_gc_user);
        EditText et_pass = dialog.findViewById(R.id.edt_gc_password);
        Button bt_login = dialog.findViewById(R.id.btn_gc);
        cursor = sqlDb.getAllData();

        Boolean state = false;


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(ScanActivity.this);

                if (cursor.getCount()==0){

                    Toast.makeText(ScanActivity.this,"rack not scanned",Toast.LENGTH_LONG).show();

                }else {

                    Boolean state = true;
                    while (cursor.moveToNext()) {

                        System.out.println(cursor.getCount());


                        if (et_user.getText().toString().trim().toLowerCase().equals(cursor.getString(0))
                                && et_pass.getText().toString().trim().toLowerCase().equals(cursor.getString(1)) &&
                                cursor.getString(2).equalsIgnoreCase("teamlead")) {

                            dialog.dismiss();

                            opengcadminDialog(gc,acgc,untag);
                            state = false;
                            //openSaveDialog();

                        }else{
                           // Toast.makeText(ScanActivity.this, "Teamlead login requird", Toast.LENGTH_LONG).show();

                        }
                    }
                    if(state) {
                        dialog.dismiss();
                        Toast.makeText(ScanActivity.this, "Team lead login failed", Toast.LENGTH_LONG).show();
                    }

                }


                    //Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "you are not allowed to rescan", Snackbar.LENGTH_LONG);
                    //snackbar.show();

            }
        });
    }

    private void opengcadminDialog(int gc , int acgc , String untag) {
        final Dialog dialog =new Dialog(context);

        dialog.setContentView(R.layout.dialog_gcadmincount);
        dialog.show();

        TextView tv_gccount = dialog.findViewById(R.id.tv_gc_count);
        TextView tv_acgccount = dialog.findViewById(R.id.tv_actual_count);
        EditText et_count = dialog.findViewById(R.id.edt_gc_count);
        et_count.setText("0");

        Button bt_submit = dialog.findViewById(R.id.btn_gc_submit);
        Button bt_gc_rescan = dialog.findViewById(R.id.btn_gc_rescan);


        Boolean state = false;

        tv_gccount.setText(String.valueOf(gc));
        tv_acgccount.setText(String.valueOf(acgc));

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int count1 = Integer.parseInt(et_count.getText().toString());

                if(count1!=0){

                    if(count1 == acgc){

                cursor = sqlDb.getalldataofaudit();
                String audit = "";
                String rack = "";

                if(cursor.getCount()==0){
                    Toast.makeText(ScanActivity.this, "No Audit data in database", Toast.LENGTH_LONG).show();
                }else{

                    while (cursor.moveToNext()){

                        audit = cursor.getString(1);
                        rack = cursor.getString(2);
                    }
                }

                cursor.close();
                cursor = sqlDb.getrackdataofaudit();

                Boolean isIncerted=sqlDb.insertSummery(s1,audit,rack,String.valueOf(racksize),String.valueOf(lis.size()),String.valueOf(cursor.getCount()),String.valueOf(untag), String.valueOf(gc), String.valueOf(count1));
                if(isIncerted){

                    Toast.makeText(ScanActivity.this,"inserted",Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(ScanActivity.this,"not inserted",Toast.LENGTH_LONG).show();
                }

               // int total = scanned+parseInt(et_untag.getText().toString());


                        exportdatabase();
                        dialog.dismiss();
                        et_rack.requestFocus();
                        et_rack.setText("");
                    }else {
                        Toast.makeText(ScanActivity.this, "GC missmatch", Toast.LENGTH_LONG).show();
                    }

                }else{

                    Toast.makeText(ScanActivity.this, "count not be 0", Toast.LENGTH_LONG).show();

                }
            }
        });



        bt_gc_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialogrescanconform();
                dialog.dismiss();
            }
        });
    }

    public void openDialogrescanconform() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Aleart");
        builder.setMessage("are you sure you want to rescan");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                SharedPreferences sh1 = getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sh.edit();
                String rackname = sh1.getString("rackname", null);


                Boolean isIncerted = sqlDb.deleterack(rackname);
                sqlDb.deleterackdata();
                if(isIncerted){
                    Toast.makeText(ScanActivity.this,"Rack scanned data cleared, rescan all items again",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ScanActivity.this,"Racks data not cleared, try again",Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
                getScanneddata();


            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();


            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void aleartdialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(ScanActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Please scan a vaild rack id");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        et_rack.setText("");
                        et_rack.requestFocus();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
      //  view.clearFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }


        //Hide:
        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }



    /////////////////////////////////////Api calling///////////////////////////////////

    private void syncdata(){

        progressDialog.setVisibility(View.VISIBLE);
        callcheck(et_audit.getText().toString().trim(),s1);

        try {
            progressDialog.setVisibility(View.VISIBLE);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cursor = sqlDb.getalldataofaudit();
        if(cursor.getCount()==0){
            //Toast.makeText(ScanActivity.this, "No Audit data in database", Toast.LENGTH_LONG).show();
        }else{

            while (cursor.moveToNext()){

                callsendauditinfoApi(cursor.getString(6),cursor.getString(1),cursor.getString(2),
                  cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(7));

            }
            cursor.close();
        }



        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cursor = sqlDb.getsummerydata();
        if(cursor.getCount()==0){

        }else {
            while (cursor.moveToNext()){

                callauditsaveAPI(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),
                      cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));

            }
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String path = Environment.getExternalStorageDirectory().toString()+"/Download";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
           calluploadfile(path+"/"+files[i].getName(),files[i].getName());
            //uploadFile(path+"/"+files[i].getName());
            Log.d("Files", path+"/"+files[i].getName());
        }

        //calluploadfile(path);

        //openSuccessDialog();


    }


    //////////////////////////////////////API responce//////////////////////////////


    private void callauditsaveAPI(String user,String auditid,String rackid,String totalracks,String completedracks,
                                  String totalitems,String untaged,String gc,String agc) {


        Auditimformation logisticsIn=new Auditimformation(user,auditid,rackid,totalracks,completedracks,totalitems,untaged,user+"_"+rackid+".csv",gc,agc);


        if (Utilities.isConnectedToInternet(getApplicationContext())) {

            WebServices<ApiResponce> webServices = new WebServices<ApiResponce>(ScanActivity.this);
            webServices.imformationIn(Utilities.getBaseURL(ScanActivity.this), WebServices.ApiType.auditinfoIn, logisticsIn);

        } else {
            //Utilities.showToast(ScanActivity.this,getResources().getString(R.string.err_msg_nointernet));
        }
    }

    private void calluploadfile(String path,String fname) {


        if (Utilities.isConnectedToInternet(getApplicationContext())) {

            WebServices<ApiResponce> webServices = new WebServices<ApiResponce>(ScanActivity.this);
            webServices.fileupload(Utilities.getBaseURL(ScanActivity.this), WebServices.ApiType.uploadfile, path,fname);

        } else {
            //Utilities.showToast(ScanActivity.this,getResources().getString(R.string.err_msg_nointernet));
        }
    }

    private void callsendauditinfoApi( String userid, String auditcode, String rack, String item, String valid, String time, String quentity) {

        AuditiinfofromDatabase auditdatabase=new AuditiinfofromDatabase(userid,auditcode,rack,item, valid,time,quentity);
        if (Utilities.isConnectedToInternet(getApplicationContext())) {

            //toggleVisibility(true,mProgressBar);
            //hideSuccessAndfailureLayouts();

            WebServices<ApiResponce> webServices = new WebServices<ApiResponce>(ScanActivity.this);
            webServices.auditDatabase(Utilities.getBaseURL(ScanActivity.this), auditdata, auditdatabase);

        } else {
            //Utilities.showToast(ScanActivity.this,getResources().getString(R.string.err_msg_nointernet));
        }
    }


    private void callcheck( String userid, String auditcode) {

        Checkmodel auditdatabase=new Checkmodel(userid,auditcode);
        if (Utilities.isConnectedToInternet(getApplicationContext())) {

            //toggleVisibility(true,mProgressBar);
            //hideSuccessAndfailureLayouts();

            WebServices<ApiResponce> webServices = new WebServices<ApiResponce>(ScanActivity.this);
            webServices.check(Utilities.getBaseURL(ScanActivity.this), checkmodel, auditdatabase);

        } else {
            //Utilities.showToast(ScanActivity.this,getResources().getString(R.string.err_msg_nointernet));
        }
    }



    //Api responce
    @Override
    public void onResponse(Object response, WebServices.ApiType URL, boolean isSucces, int code) {

        switch (URL)
        {
            case auditinfoIn:


                if(isSucces) {

                    ApiResponce apiResponce = new ApiResponce();
                    String api= apiResponce.getMessage();
                   // Toast.makeText(ScanActivity.this,apiResponce.getMessage(),Toast.LENGTH_LONG).show();

                }
                break;


            case auditdata:

                break;

            case uploadfile:

                if(isSucces) {

                    if(response!=null){
                        progressDialog.setVisibility(View.INVISIBLE);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Data sync successful", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else {
                        progressDialog.setVisibility(View.INVISIBLE);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sync Failed, resync again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }else {
                    progressDialog.setVisibility(View.INVISIBLE);
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sync Failed, resync again", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s != null && !s.toString().equalsIgnoreCase("")) {

                    System.out.println("print hash code---" + s.hashCode());
                    System.out.println("print hash item---" + et_rack.getText().hashCode());

                    if (et_rack.getText().hashCode() == s.hashCode()){

                        getrackdata();
                        rackvalidation();

                    }

                    if (et_item.getText().hashCode() == s.hashCode()){


                        if(et_item.getText().toString().isEmpty()){

                        }else{
                            //hideKeyboard(ScanActivity.this);
                            l = et_item.getText().toString().length();

                            System.out.println("print hash code---" + s.hashCode());
                            System.out.println("print hash item---" + et_item.getText().hashCode());

                            if (!cb_quentity.isChecked()) {

                                if (length) {
                                    if (l == parseInt(barcode_length)) {
                                        validateedittext();
                                        et_item.getText().clear();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Item length not matching", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        et_item.getText().clear();
                                    }

                                } else {
                                    validateedittext();
                                    et_item.getText().clear();
                                }
                            } else {


                            }}
                    }
                }
            }
        };
    }

    ///////////////////////////////////////////edit text watcher///////////////////////////////////////////


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {


            if (s != null && !s.toString().equalsIgnoreCase("")) {

                System.out.println("print hash code---" + s.hashCode());
                System.out.println("print hash item---" + et_rack.getText().hashCode());

                if (et_rack.getText().hashCode() == s.hashCode()){

                    getrackdata();
                    rackvalidation();

                }

                if (et_item.getText().hashCode() == s.hashCode()){


                    if(et_item.getText().toString().isEmpty()){

                    }else{
                //hideKeyboard(ScanActivity.this);
                l = et_item.getText().toString().length();
                        System.out.println("print hash item---" + et_item.getText().hashCode());

             System.out.println("print hash code---" + s.hashCode());

            if (!cb_quentity.isChecked()) {

                if (length) {
                    if (l == parseInt(barcode_length)) {
                        validateedittext();
                        et_item.getText().clear();
                    } else {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Item length not matching", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        et_item.getText().clear();
                    }

                } else {
                    validateedittext();
                    et_item.getText().clear();
                }
            } else {


            }}
        }
        }
        }
    };
}