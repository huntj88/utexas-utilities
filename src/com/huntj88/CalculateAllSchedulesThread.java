package com.huntj88;

import android.util.Log;

import com.nasageek.utexasutilities.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by James on 9/9/2015.
 */
public class CalculateAllSchedulesThread extends AsyncTask<Boolean, String, Integer>
{

    ArrayList<ArrayList<UTClass>> classes;
    //ArrayList<int[][]> finalClassSet = new ArrayList<>();

    ArrayList<int[][]> temp1 = new ArrayList<>();
    ArrayList<int[][]> temp2 = new ArrayList<>();

    public CalculateAllSchedulesThread(ArrayList<ArrayList<UTClass>> classes)
    {
        this.classes=classes;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Boolean... params) {


        if(classes.size()!=0) {
            for (int x = 0; x < classes.get(0).size(); x++) {
                temp1.add(classes.get(0).get(x).getArraySchedule());
            }
            int n = 1;
            boolean firstArrayFinal = true;
            for (int numClasses = 1; numClasses < classes.size(); numClasses += 1) {
                if (n % 2 == 1) {
                    methodAlternateOne(n);
                    n++;
                    firstArrayFinal = false;
                } else {
                    methodAlternateTwo(n);
                    n++;
                    firstArrayFinal = true;
                }
            }

            if (!firstArrayFinal)
                output(temp2);
            else
                output(temp1);

        }
        else
        {
            Log.e("class thread","NO CLASSES");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer h)
    {

    }

    public void output(ArrayList<int[][]> finalArray)
    {
        finalArray.removeAll(Collections.singleton(null));

        /*for(int z=0;z<finalArray.size();z++) {
            for (int y = 0; y < 24; y++) {
                String string = "";
                for (int x = 0; x < 5; x++) {
                    string += "[" + finalArray.get(z)[x][y] + "]";
                }
                Log.d("schedule", string);
            }
            Log.d("schedule", "------------");
        }*/

        Log.d("size",finalArray.size()+"");
    }


    public void methodAlternateOne(int n)
    {
        temp2= new ArrayList<>();
        temp1.removeAll(Collections.singleton(null));

        for(int x=0;x<temp1.size();x++) {
            for (int i = 0; i < classes.get(n).size(); i++) {

                int[][] check = combine(temp1.get(x), classes.get(n).get(i).getArraySchedule());
                if(!containsCheck(temp2,check)) {
                    temp2.add(check);
                }
            }
        }
        Log.d("size",temp2.size()+"");
    }

    public void methodAlternateTwo(int n)
    {
        temp1=new ArrayList<>();
        temp2.removeAll(Collections.singleton(null));

        for(int x=0;x<temp2.size();x++) {
            for (int i = 0; i < classes.get(2).size(); i++) {

                int[][] check = combine(temp2.get(x), classes.get(2).get(i).getArraySchedule());
                if(!containsCheck(temp1, check)) {
                    temp1.add(check);
                }
            }
        }
        Log.d("size",temp1.size()+"");
    }

    public boolean containsCheck(ArrayList<int[][]> a,int[][] b)
    {
        for(int i=0;i<a.size();i++)
        {
            //if(a.get(i).equals(b))
            if(Arrays.deepEquals(a.get(i), b))
            return true;
        }

        return false;
    }


    public int[][] combine(int[][] a, int[][] b)
    {
        int[][] returnArray = new int[a.length][a[0].length];

        for(int y=0;y<a[0].length;y++)
        {
            for(int x=0;x<a.length;x++)
            {
                if(a[x][y]!=0&&b[x][y]==0)
                {
                    returnArray[x][y]=a[x][y];
                }
                else if(a[x][y]==0&&b[x][y]!=0)
                {
                    returnArray[x][y]=b[x][y];
                }
                else if(a[x][y]!=0&&b[x][y]!=0)
                {
                    return null;
                }
            }
        }

        /*for(int y=0;y<24;y++)
        {
            String string="";
            for(int x=0;x<5;x++)
            {
                string+="["+returnArray[x][y]+"]";
            }
            string+="      ";
            for(int x=0;x<5;x++)
            {
                string+="["+a[x][y]+"]";
            }
            string+="      ";
            for(int x=0;x<5;x++)
            {
                string+="["+b[x][y]+"]";
            }
            Log.d("schedule",string);
        }*/

        return returnArray;
    }

}