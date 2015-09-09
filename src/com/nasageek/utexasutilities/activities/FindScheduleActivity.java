package com.nasageek.utexasutilities.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.huntj88.ClassListHolder;
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

public class FindScheduleActivity extends SherlockActivity {

    private parseTask fetch;
    private String tag = "FindScheduleA";
    private TextView searchBoxDepartment,searchBoxCourseNum;
    private int classAmount;
    ArrayList<ArrayList<UTClass>> classes = new ArrayList<>(); //holds all the classes in a two arraylist
    //the first dimension to represent all the types of classes: chem 101, CS 312, GEO 405.
    //the second dimension is the represent all the times that class is avaliable.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_schedule);
        searchBoxDepartment = (TextView) findViewById(R.id.search_box_department);
        searchBoxCourseNum = (TextView) findViewById(R.id.search_box_course_num);
        /*OkHttpClient client = new OkHttpClient();
        fetch = new parseTask(client);
        Utility.parallelExecute(fetch, false);*/
    }

    public void startSearch(View v)
    {
        OkHttpClient client = new OkHttpClient();
        fetch = new parseTask(client,searchBoxDepartment.getText().toString(),searchBoxCourseNum.getText().toString());
        Utility.parallelExecute(fetch, false);
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

    public void lop(String string)
    {
        ArrayList<UTClass> innerArray = new ArrayList<>();
        if(string.length()>39000) {
            string = string.substring(39000); //removes the first 39000 lines as not part of the class listings
            Scanner scan = new Scanner(string);
            String line = "";
            String days = "";
            String times="";
            UTClass utClass;
            boolean didDays=false;
            boolean didTime=false;
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
                if(didDays&&didTime)
                {

                    innerArray.add(new UTClass(days,times,"cs","blah",classAmount));
                    didDays=false;
                    didTime=false;
                    days = "";
                }
            }
            scan.close();
        }

        if(innerArray.size()>0) {
            classAmount++;
            classes.add(innerArray);
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
    }
}
