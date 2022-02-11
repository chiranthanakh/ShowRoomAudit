package com.proteam.showroomaudit.views.activities;

import static com.google.firebase.crashlytics.buildtools.reloc.com.google.common.net.HttpHeaders.FROM;
import static java.nio.file.attribute.AclEntryPermission.DELETE;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlDb extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "ShowRoomAudit.db";
    public static final String TABLE_NAME = "userlist2";

    public static final String COL_1 = "name";
    public static final String COL_2 = "pass";
    public static final String COL_3 = "position";
    public static final String COL_4 = "auditcode";


    public static final String TABLE_SECKOND = "Scanneddata";
    public static final String COL1 = "SN";
    public static final String COL2 = "Auditcode";
    public static final String COL3 = "rackid";
    public static final String COL4 = "itemnumber";
    public static final String COL5 = "quentity";
    public static final String COL6 = "Time";
    public static final String COL7 = "employee";
    public static final String COL8 = "q";



    public static final String TABLE_ITEMS = "Racklist";
    public static final String COL_11 = "rackid";
    public static final String COL_12 = "gccount";


    //Summery table
    public static final String TABLE_SUMMERY = "Summery";
    public static final String COL11 = "user";
    public static final String COL12 = "audit_code1";
    public static final String COL13 = "rack_code1";
    public static final String COL14 = "total_racks1";
    public static final String COL15 = "racks_completed1";
    public static final String COL16 = "total_itemqty1";
    public static final String COL17 = "total_untagged1";
    public static final String COL18 = "gc";
    public static final String COL19 = "agc";





    //Summery table
    public static final String TABLE_rackdata = "Rackdata";
    public static final String COL21 = "sn";
    public static final String COL22 = "auditCode";
    public static final String COL23 = "rackId";
    public static final String COL24 = "itemNumber";
    public static final String COL25 = "validate";
    public static final String COL26 = "Time1";
    public static final String COL27 = "employee1";
    public static final String COL28 = "quentity2";



    public SqlDb(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("create table " + TABLE_NAME +" (name TEXT,pass TEXT UNIQUE)");
        db.execSQL("create table " + TABLE_NAME +" (name TEXT,pass TEXT,position TEXT,auditcode TEXT)");
        db.execSQL("create table " + TABLE_SECKOND +" (SN INTEGER PRIMARY KEY AUTOINCREMENT,Auditcode TEXT,rackid INTEGER,itemnumber TEXT, quentity TEXT,Time TEXT, employee TEXT, q TEXT )");
        db.execSQL("create table " + TABLE_ITEMS +" (rackid TEXT, gccount TEXT )");
        db.execSQL("create table " + TABLE_SUMMERY +" (user TEXT,audit_code1 TEXT,rack_code1 INTEGER,total_racks1 TEXT, racks_completed1 TEXT,total_itemqty1 TEXT, total_untagged1 TEXT,gc TEXT,agc TEXT )");
        db.execSQL("create table " + TABLE_rackdata +" (sn INTEGER PRIMARY KEY AUTOINCREMENT,auditCode TEXT,rackId TEXT,itemNumber TEXT, validate TEXT,Time1 TEXT, employee1 TEXT, quentity2 TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SECKOND);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SUMMERY);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_rackdata);
        onCreate(db);
    }

    public boolean insertData(String Color, String placeName, String position, String auditcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,Color);
        contentValues.put(COL_2,placeName);
        contentValues.put(COL_3,position);
        contentValues.put(COL_4,auditcode);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertSeckond(String auditcode, String rackcode, String itemid, String quentity, String tagged, String employe, String q ) {
        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("validation data"+auditcode +",,," +rackcode +",,,,"+itemid +",,," +quentity +",,,,"+tagged  );

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,auditcode);
        contentValues.put(COL3,rackcode);
        contentValues.put(COL4,itemid);
        contentValues.put(COL5,quentity);
        contentValues.put(COL6,tagged);
        contentValues.put(COL7,employe);
        contentValues.put(COL8,q);
        long result = db.insert(TABLE_SECKOND,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertSummery(String user,String audit1, String rack1, String totalracks,
                                 String totalitemcount, String s1, String untagged,String gc,String agc ) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL11,user);
        contentValues.put(COL12,audit1);
        contentValues.put(COL13,rack1);
        contentValues.put(COL14,totalracks);
        contentValues.put(COL15,totalitemcount);
        contentValues.put(COL16,s1);
        contentValues.put(COL17,untagged);
        contentValues.put(COL18,gc);
        contentValues.put(COL19,agc);


        long result = db.insert(TABLE_SUMMERY,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean insertrackdata(String auditcode1, String rackcode1, String itemid1, String quentity1, String tagged1, String employe1, String employe2 ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL22,auditcode1);
        contentValues.put(COL23,rackcode1);
        contentValues.put(COL24,itemid1);
        contentValues.put(COL25,quentity1);
        contentValues.put(COL26,tagged1);
        contentValues.put(COL27,employe1);
        contentValues.put(COL28,employe2);
        long result = db.insert(TABLE_rackdata,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME,null);
        return res;
    }



    public Cursor getAllDatarack() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_ITEMS,null);
        return res;
    }

    public Cursor getid() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID from "+TABLE_NAME,null);
        return res;
    }


    public Cursor getalldataofaudit() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SECKOND,null);
        return res;
    }

    public Cursor getrackdataofaudit() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_rackdata,null);
        return res;
    }

    public Cursor getsummerydata() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SUMMERY,null);
        return res;
    }

    public int getUpdate(int snNO) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL4,"true");
        int res=db.update(TABLE_SECKOND, cv, "SN="+snNO, null);
        return res;

    }


    public Cursor getsn() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SECKOND, null);
        return res;
    }

    public boolean deleterack(String rack) {
        SQLiteDatabase db = getWritableDatabase();
        //String whereArgs[] = {item.id.toString()};
        long result =db.delete(TABLE_SECKOND, COL3 + " = ?", new String[]{rack});
        if(result == -1)
            return false;
        else
            return true;

    }


    public boolean deleterackdata() {
        SQLiteDatabase db = getWritableDatabase();
        //String whereArgs[] = {item.id.toString()};
        long result =db.delete(TABLE_rackdata, null, null);
        //Cursor res = db.rawQuery("delete * from "+TABLE_SECKOND, null);
        if(result == -1)
            return false;
        else
            return true;

    }


    public boolean insertitemslist(String rackcode, String gccode ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11,rackcode);
        contentValues.put(COL_12,gccode);
        long result = db.insert(TABLE_ITEMS,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public boolean clearalldata( ) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ITEMS,null ,null);
        db.delete(TABLE_SECKOND,null ,null);
        db.delete(TABLE_SUMMERY,null ,null);
        db.delete(TABLE_rackdata,null ,null);
        db.delete(TABLE_NAME,null ,null);

        long result = db.delete(TABLE_NAME,null ,null);
        if(result == -1)
            return false;
        else
            return true;
    }


}
