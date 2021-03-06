package com.nasageek.utexasutilities.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.huntj88.CalculateAllSchedulesThread;
import com.huntj88.ClassListHolder;
import com.huntj88.ScheduleHolderFragment;
import com.huntj88.SelectClassesFragment;
import com.huntj88.UTClass;
import com.nasageek.utexasutilities.AsyncTask;
import com.nasageek.utexasutilities.R;
import com.nasageek.utexasutilities.Utility;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FindScheduleActivity extends FragmentActivity implements SelectClassesFragment.SelectClassesListener{

    private parseTask fetch;
    private CalculateAllSchedulesThread calculate;
    private String tag = "FindScheduleA";
    private int classAmount=1;
    SelectClassesFragment one = new SelectClassesFragment();
    ArrayList<ArrayList<UTClass>> classes = new ArrayList<>(); //holds all the classes in a two arraylist
    //the first dimension to represent all the types of classes: chem 101, CS 312, GEO 405.
    //the second dimension is the represent all the times that class is avaliable.

    Toast toast;
    boolean showToast=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_schedule);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.placeholder, one);
        ft.commit();
    }

    @Override
    public void selectedAClass(String department, String course) {
        OkHttpClient client = new OkHttpClient();
        fetch = new parseTask(client,department,course);
        Utility.parallelExecute(fetch, false);
    }

    public void nextButton(View v)
    {
        calculate = new CalculateAllSchedulesThread(classes);
        Utility.parallelExecute(calculate, false);
    }
    public void log(String string)
    {
        for(int i=0; i<string.length(); i+=1000)
        {
            if(i>=39000) {
                if (i + 1000 < string.length())
                    Log.d(tag + " " + i, string.substring(i, i + 1000));
                else
                    Log.d(tag + " " + i, string.substring(i, string.length()));
            }
        }
    }

    public void removeClass(View v)
    {
        if(classes.size()>=1) {
            classes.remove(classes.size() - 1);
            classAmount--;
            one.removedClass(classAmount);
        }
    }

    public void lop(String string)
    {
        ArrayList<UTClass> innerArray = new ArrayList<>();
        if(string.length()>39200) {
            string = string.substring(39200); //removes the first 39200 lines as not part of the class listings
            Scanner scan = new Scanner(string);
            String line = "";
            String days = "";
            String times="";
            boolean didDays=false;
            boolean didTime=false;
            boolean open = false;
            boolean found =false;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                if (line.contains("Days")) {
                    for (int i = 0; i < line.length(); i++) {
                        if (i == 41)
                            days += " ";

                        if (Character.isUpperCase(line.charAt(i)) && line.charAt(i) != 'D') {
                            days += line.charAt(i);
                        }
                    }
                    //Log.d("tag", days);
                    didDays=true;
                }

                if (line.contains("Hour")) {
                    int amount = 0;
                    //Log.d("hour",line);
                    times = line.substring(38, 59);
                    for (int x = 0; x < 2; x++) {
                        if (times.contains("<")) {
                            times = times.substring(0, times.length() - 1);
                            amount++;
                        }
                    }
                    if(line.length()>100) {
                        times += "    ";
                        times += line.substring(97 - amount, 118 - amount);

                        for (int x = 0; x < 2; x++) {
                            if (times.contains("<")) {
                                times = times.substring(0, times.length() - 1);
                            }
                        }
                    }
                    //Log.d("tag", times);
                    didTime=true;
                }
                if(line.contains("open"))
                {
                    open=true;
                    found=true;
                }
                if(line.contains("closed")||line.contains("cancelled"))
                {
                    found=true;
                }
                if(didDays&&didTime&&open&&found)
                {

                    innerArray.add(new UTClass(days,times,"cs","blah",classAmount));
                    didDays=false;
                    didTime=false;
                    days = "";
                    open=false;
                }
                else if(didDays&&didTime&&found)
                {
                    //toast = Toast.makeText(this, "no open classes", Toast.LENGTH_LONG);
                    didDays=false;
                    didTime=false;
                    days = "";
                    open=false;
                }
            }
            scan.close();
        }

        if(innerArray.size()>0) {
            classAmount++;
            classes.add(innerArray);

        }
        else
        {
            showToast=true;
        }
        Log.d("num type class",classes.size()+"");
    }

    private class parseTask extends AsyncTask<Boolean, String, Integer>
    {

        private OkHttpClient client;
        private String errorMsg;
        private String searchDepartment,searchCourse;

        public parseTask(OkHttpClient client,String searchDepartment,String searchCourse) {
            this.client = client;
            this.searchDepartment=searchDepartment;
            this.searchCourse=searchCourse;
        }

        @Override
        protected void onPreExecute() {

            searchDepartment=searchDepartment.toUpperCase();
            for(int i=0;i<searchDepartment.length();i++)
            {
                if(searchDepartment.charAt(i)==' ')
                {
                    searchDepartment = searchDepartment.substring(0,i)+'+'+searchDepartment.substring(i+1);
                }
            }
        }

        @Override
        protected Integer doInBackground(Boolean... params) {

            String reqUrl = "https://utdirect.utexas.edu/apps/registrar/course_schedule/20159/results/?search_type_main=COURSE&fos_cn="+searchDepartment+"&course_number="+searchCourse;
            Request request = new Request.Builder()
                    .url(reqUrl)
                    .build();
            String pagedata = "";

            try {
                Response response = client.newCall(request).execute();
                pagedata = response.body().string();
            } catch (IOException e) {
                errorMsg = "UTilities could not fetch your class listing";
                e.printStackTrace();
                cancel(true);
                return -1;
            }

            // now parse the Class Listing data

            // did we hit the login screen?
            if (pagedata.contains("<title>UT EID Login</title>")) {

                Log.d(tag,"crap");
            }
            else
            {
                lop(pagedata);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer h)
        {
            if(!showToast)
                one.test(classAmount - 1, searchDepartment + " " + searchCourse);
            else {
                toast = Toast.makeText(getApplicationContext(), "no open classes", Toast.LENGTH_LONG);
                toast.show();
                showToast=false;
            }
            //one.typedClasses[classAmount-1].setText(searchDepartment+" "+searchCourse);
        }
    }
}
