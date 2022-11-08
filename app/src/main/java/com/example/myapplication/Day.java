package com.example.myapplication;

import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class Day extends com.example.myapplication.Date {
    //מחלקה היורשת ממחלקת התאריך שנועדה לייצג יום, וביום גם רשימה של
    // איוונטים המתרחשים ביום וגם תאריך
    private ArrayList<Event> events;

    public Day(Calendar calendar, ArrayList<Event> events) {
        super(calendar);
        this.events = events;
    }
    public Day(ArrayList<Event> events) {
        super(Calendar.getInstance());
        this.events = events;
    }

    public Day(Calendar calendarDate) {
        super(calendarDate);
        this.events = new ArrayList<>();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event){
        this.getEvents().add(event);
    }
    public Day dayAfter(){
        // מחזירה את היום שאחרי
        Calendar calendar = this.getSameCalendarDate();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Day d = new Day(calendar,new ArrayList<Event>());
        return d;
    }
    public Day dayBefore(){
        // מחזירה את היום שלפני
        Calendar calendar = this.getSameCalendarDate();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Day d = new Day(calendar,new ArrayList<Event>());
        return d;
    }
}
