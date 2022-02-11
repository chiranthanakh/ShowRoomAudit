/*
 * Created by Krishnamurthy T
 * Copyright (c) 2019 .  V V Technologies All rights reserved.
 * Last modified 21/1/19 2:27 PM
 *
 */

package com.proteam.showroomaudit.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Utilities {

    /*Constants*/
    public static int DIALOG_TIMEOUT =60000;

    public static Snackbar snackbar;
    private static Toast mToast;
    private static String valueStreamPreference="VALUESTREAMPREFERENCE";
    private static String ipAddressPreference="IPADDRESSPREFERENCE";

/*
* To showSnackBar message
* -----------------------------------------------------------------------------------------------------------------
* */
    public static void showSnackBar(Context context, String message) {
        Activity activity = (Activity) context;
        if (snackbar != null) {
            snackbar.dismiss();
        }
        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.BLACK);
       // TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        //tv.setTextColor(Color.WHITE);
        //tv.setTextSize(16);
        snackbar.show();
    }

    /*
    * --------------------------------------------------------------------------------------------------------------
    * */


/*
* To show Toast message
* -----------------------------------------------------------------------------------------------------------------
* */
    public static void showToast(Context context,String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /*
    * ------------------------------------------------------------------------------------------------------------
    * */


/*For getting BaseURL
* -------------------------------------------------------------------------------------------------------------------
 * */
    public static String getBaseURL(Context context)
    {
       // SharedPreferences sharedPreferences=context.getSharedPreferences(ipAddressPreference,Context.MODE_PRIVATE);
        //String ipaddress=sharedPreferences.getString("IPADDRESS",null);

        String ipaddress=getIPAddress(context);
        if(ipaddress!=null)
        {
            return "https://pda.proteam.co.in/en/api/";

        }
        else
        {
            return null;
        }


    }

    /*
    * ------------------------------------------------------------------------------------------------------------------
    * */

/*For saving IP Address
* -------------------------------------------------------------------------------------------------------------------
* */
    public static void saveIPAddressPreference(Context context,String ipAddress)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(ipAddressPreference,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

       /* String ipaddress=sharedPreferences.getString("IPADDRESS",null);
        if(ipaddress!=null)*/
        if(sharedPreferences.contains("IPADDRESS"))
        {
            editor.clear();
            editor.apply();
        }
        editor.putString("IPADDRESS",ipAddress);
        editor.apply();
    }


    public static String getIPAddress(Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(ipAddressPreference,Context.MODE_PRIVATE);
        return sharedPreferences.getString("IPADDRESS",null);

    }
    /*
 * -------------------------------------------------------------------------------------------------------------------
     * */


    /*
    To saveLogInPreference
 * -------------------------------------------------------------------------------------------------------------------
     * */

    public static void saveValueStreamPreference(Context context,boolean save,String valueStream)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(valueStreamPreference,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        if(save)
        {
            editor.putString("VALUESTREAM",valueStream);
            editor.apply();
        }
        else {
            editor.clear();
            editor.apply();
        }

    }

    public static String getValueStream(Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(valueStreamPreference,Context.MODE_PRIVATE);
        return sharedPreferences.getString("VALUESTREAM",null);
    }

    /*
* -------------------------------------------------------------------------------------------------------------------
     * */


    /*
    To check whether the app is connected to Internet or not
 * -------------------------------------------------------------------------------------------------------------------
     * */

    public static boolean isConnectedToInternet(Context con){
        ConnectivityManager connectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /*
    * ------------------------------------------------------------------------------------------------------------------
    * */

    //For View(s)
    public static void toggleVisibility(boolean status,View... views) {
        for (View v : views) {
            //if status is true make view visible, else visibility gone
            if(status)
            {
                v.setVisibility(View.VISIBLE);

            }
            else {
                v.setVisibility(View.GONE);
            }
            /*if (v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }*/
        }
    }

    //For ViewGroup(s)
    public static void toggleVisibility(boolean status,ViewGroup... views) {
        for (View v : views) {
            //if status is true make view visible, else visibility gone
            if(status)
            {
                v.setVisibility(View.VISIBLE);

            }
            else {
                v.setVisibility(View.GONE);
            }
            /*if (v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
            } else {
                v.setVisibility(View.VISIBLE);
            }*/
        }
    }

}
