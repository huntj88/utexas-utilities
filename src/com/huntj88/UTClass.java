package com.huntj88;

import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by James on 9/8/2015.
 */
public class UTClass {

    private String days;
    private String time;
    private String className;
    private String professor;
    private int id;

    public UTClass(String days,String time, String className, String professor,int id)
    {
        this.days=days;
        this.time=time;
        this.className=className;
        this.professor=professor;
        this.id=id;
    }

    public ArrayList<TimeDayPair> getTimeDayPairs()
    {
        //Log.d("UTClass","days: "+days);
        ArrayList<TimeDayPair> array = new ArrayList<>();
        int timeSpot = 0;                                           //which part of the time string to parse from
        for(int i=0;i<days.length();i++)
        {
            char next = days.charAt(i);
            //Log.d("UTClass",next+"");
            switch (next)
            {
                case ' ':
                    timeSpot+=2;
                    break;
                case 'M':
                    array.add(buildPair(timeSpot,1));
                    break;
                case 'W':
                    array.add(buildPair(timeSpot,3));
                    break;
                case 'F':
                    array.add(buildPair(timeSpot,5));
                    break;
                case 'T':

                    if(days.length()>i+1&&days.charAt(i+1)=='H')    //thursday
                    {
                        array.add(buildPair(timeSpot, 4));
                        i++;
                    }
                    else                                            //tuesday
                        array.add(buildPair(timeSpot,2));

                    break;
                default:

                    break;
            }
        }

        return null;
    }

    public TimeDayPair buildPair(int timeSpot, int day)
    {
        int time1=0;
        int time1End=0;
        int time2=0;
        int time2End=0;
        int index = time.indexOf(":");
        int index1 = time.indexOf(":", index + 1);
        int index2 = time.indexOf(":", index1 + 1);
        int index3 = time.indexOf(":", index2 + 1);
        String temp;

        if(timeSpot==0) {
            time1 = Integer.parseInt(time.substring(0, index));
            time1End = Integer.parseInt(time.substring(index + 1, index + 3));

            temp=time.substring(index1 - 2, index1);
            if(temp.contains(" ")||temp.contains("-"))
                temp=temp.substring(1);
            time2 = Integer.parseInt(temp);
            //time2 = Integer.parseInt(time.substring(index1-2, index1));
            time2End = Integer.parseInt(time.substring(index1+1, index1+3));

            if(time.substring(0,index1-2).contains("p.m.")&&time1!=12)
            {
                time1+=12;
            }
            if(time.length()>24) {
                if (time.substring(index1 + 1, index2 - 2).contains("p.m.") && time2 != 12) {
                    time2 += 12;
                }
            }
            else
            {
                if (time.substring(index1 + 1).contains("p.m.") && time2 != 12) {
                    time2 += 12;
                }
            }

        }
        else if(timeSpot==2)
        {
            temp=time.substring(index2-2, index2);
            if(temp.contains(" ")||temp.contains("-"))
                temp=temp.substring(1);
            time1 = Integer.parseInt(temp);
            time1End = Integer.parseInt(time.substring(index2 + 1, index2 + 3));

            temp=time.substring(index3-2, index3);
            if(temp.contains(" ")||temp.contains("-"))
                temp=temp.substring(1);
            time2 = Integer.parseInt(temp);
            time2End = Integer.parseInt(time.substring(index3+1, index3+3));

            if(time.substring(index2+1,index3-2).contains("p.m.")&&time1!=12)
            {
                time1+=12;
            }
            if(time.substring(index3+1).contains("p.m.")&&time2!=12)
            {
                time2+=12;
            }
        }
        //Log.d("UTClass",timeSpot+" "+day+" "+time1+" "+time1End+" "+time2+" "+time2End);
        return new TimeDayPair(day,getTimeInArray(time1,time1End),getTimeInArray(time2,time2End));
    }

    public int getTimeInArray(int first,int second)
    {

        int timeInArray = first - 8;
        timeInArray=timeInArray*2;
        if(second!=0)
            timeInArray++;
        Log.d("UTClass",""+timeInArray);
        return timeInArray;
    }
}
