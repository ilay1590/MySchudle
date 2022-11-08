package com.example.myapplication;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
    // מחלקה שנועדה לפשט ולייצר פעולות עזר למחלקה המובנית Calendar
    private Calendar calendar;

    public Date(Calendar calendar){
        this.calendar = calendar;
    }

    public Date() {
        //בנאי ריק שיוצר תאריך של היום בדיוק
        this.calendar = Calendar.getInstance();
    }

    public String getDateStringFormat(){
        // מחזירה את התאריך בצורה של שרשרת ב- "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = getCalendarDate().getTime();
        String date = sdf.format(d);
        return date;
    }
    public Calendar getCalendarDate(){
        return this.calendar;
    }
    public int getDayDate(){

        int day = Integer.valueOf(getDateStringFormat().substring(0,2));
        return day;

    }
    public int getYearDate(){

        int year = Integer.valueOf(getDateStringFormat().substring(6));
        return year;

    }
    public int getMonthDate(){
        int month = Integer.valueOf(getDateStringFormat().substring(3,5));
        return month;
    }
    public void setDate(Calendar date) {
        this.calendar = date;
    }

    public Calendar getSameCalendarDate()
    {
        // מחזירה את אותו התאריך אבל יוצרת אחד שונה
        Calendar calendar = (Calendar) getCalendarDate().clone();
        return calendar;
    }
    public int getDayNumOfWeek(){
        // מחזירה את מס' היום בשבוע, מתחיל ב0
        String name = this.getDayName();
        if (name.equals("Sun")) return 0;
        if (name.equals("Mon")) return 1;
        if (name.equals("Tue")) return 2;
        if (name.equals("Wed")) return 3;
        if (name.equals("Thu")) return 4;
        if (name.equals("Fri")) return 5;
        return 6;
    }
    public int getDayNumOfMonth(){
        // מחזירה את מס' היום בחודש
        int i = this.calendar.get(Calendar.DAY_OF_MONTH);
        return i;
    }

    public String getDayName(){
        // מחזירה את 3 אותיות הראשונות בלועזית של תחילת היום
        String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        String dayName = days[this.calendar.get(Calendar.DAY_OF_WEEK)-1];
        dayName = dayName.substring(0,3);
        return dayName;
    }
    public String getMonthName(){
        String monthName = new SimpleDateFormat("MMMM").format(calendar.getTime());
        return monthName;
    }
    public int getYear(){
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public Date getNextDayDate(){
       Calendar calendar = getSameCalendarDate();
       calendar.add(Calendar.DAY_OF_MONTH,1);
       return new Date(calendar);
    }
    public Date getPreviousDayDate(){
        Calendar calendar = getSameCalendarDate();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        return new Date(calendar);
    }

    public void setCalendar(Calendar calendar){
        this.calendar = calendar;
    }
}
