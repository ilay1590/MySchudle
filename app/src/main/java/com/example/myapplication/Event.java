package com.example.myapplication;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event extends Date implements Serializable{

    // מחלקה של האירוע, שמטרתה ליצור את האיורע ופריטיו, מכילה שעה התחלתית וסופית וגם דקה התחלתית וסופית
    //   גם עם שם תאריך ופירוט האירוע, היורשת מהמחלקה Serializable כדי לאפשר שליחת נתונים
    // בIntent
    private String startHours,startMinutes,endHours,endMinutes;
    private String name;
    private String description;


    public Event(Calendar calendar,String name,String description,String startHours,String
            startMinutes,String endHours,String endMinutes){
        super(calendar);
        this.name = name;
        this.description = description;
        this.startHours = startHours;
        this.startMinutes = startMinutes;
        this.endHours = endHours;
        this.endMinutes = endMinutes;
    }
    public Event(Event event){
        // בנאי מעתיק
        super(event.getCalendarDate());
        this.name = event.getName();
        this.description = event.getDescription();
        this.startMinutes = event.getStartMinutes();
        this.endMinutes = event.getEndMinutes();
        this.startHours = event.getStartHours();
        this.endHours = event.getEndHours();
        //todo clone object
    }
    public Event(){
        super();
    }

    public Event(String date,String name,String description,String startHours,String
            startMinutes,String endHours,String endMinutes){

        this.name = name;
        int year = Integer.valueOf(date.substring(0,4));
        int day = Integer.valueOf(date.substring(8));
        int month = Integer.valueOf(date.substring(5,7));
        Calendar c = Calendar.getInstance();
        this.setCalendar(c);
        c.set(year, month - 1, day, Integer.valueOf(startHours), Integer.valueOf(startMinutes));
        this.description = description;
        this.startHours = startHours;
        this.startMinutes = startMinutes;
        this.endHours = endHours;
        this.endMinutes = endMinutes;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartHours() {
        return startHours;
    }

    public void setStartHours(String startHours) {
        this.startHours = startHours;
    }

    public String getStartMinutes() {
        return startMinutes;
    }

    public void setStartMinutes(String startMinutes) {
        this.startMinutes = startMinutes;
    }

    public String getEndMinutes() {
        return endMinutes;
    }

    public void setEndMinutes(String endMinutes) {
        this.endMinutes = endMinutes;
    }

    public String getEndHours() {
        return endHours;
    }


    public void setEndHours(String endHours) {
        this.endHours = endHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
