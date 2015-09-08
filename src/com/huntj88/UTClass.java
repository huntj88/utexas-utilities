package com.huntj88;

import java.sql.Time;

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

    public TimeDayPair[] getTimeDayPairs()
    {
        return null;
    }
}
