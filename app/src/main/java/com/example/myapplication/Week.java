package com.example.myapplication;

import android.util.Log;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class Week {

    // מחלקה שמטרתה ליצור שבוע, שבו יהיו הימים עם פעולות מובנות

    private Day[] week;

    public Week(Day day){
        // יוצר את השבוע לפי יום אחד שהוא מקבל בשבוע
        week = new Day[7];
        int daysBefore = 0,daysAfter = 6;
        // -1
        daysBefore += (day.getDayNumOfWeek());
        daysAfter -= (day.getDayNumOfWeek());
        week = fillWeekByDays(day,daysBefore);
    }

    public int checkIfDayOnWeek(String date){
        // בודק אם יום מסויים נמצא בשבוע ומחזיר את מספרו
        // -1 כאשר לא נמצא
        for (int i = 0; i < week.length; i++) {
            if (week[i].getDateStringFormat().equals(date))
                return i;
        }
        return -1;
    }
    public Week(){
        // יוצר את השבוע של התאריך הנוכחי
        Calendar calendar = Calendar.getInstance();
        Day day = new Day(calendar);
        int daysBefore = 0,daysAfter = 6;
        daysBefore += (day.getDayNumOfWeek());
        daysAfter -= (day.getDayNumOfWeek());
        week = fillWeekByDays(day,daysBefore);

    }
    public Day getCurrentDay(){
        // מקבל את היום הנוכחי
        Calendar calendar = Calendar.getInstance();
        int num = calendar.get(Calendar.DAY_OF_WEEK);
        return this.getDays()[num-1];
    }

    public static Day[] fillWeekByDays(Day d,int daysBefore) {
        // מחלקת עזר לבנאי, שיוצרת את השבוע ע"י בדיקה של כמה ימים נמצאים לפני ואחרי
        Day[] week = new Day[7];
        for (int i = 0; i < daysBefore; i++) {
            Calendar calendar = d.getSameCalendarDate();
            calendar.add(Calendar.DAY_OF_MONTH, i-(daysBefore));
            week[i] = new Day(calendar);
        }
        week[daysBefore] = d;
        for (int j = daysBefore+1;  j < week.length ; j++) {
            Calendar calendar = d.getSameCalendarDate();
            calendar.add(Calendar.DAY_OF_MONTH, j-daysBefore);
            week[j] = new Day(calendar);
        }
        return week;
    }
    public Week getNextWeek(){
        // מחזיר את השבוע הבא
        Calendar calendar = week[0].getSameCalendarDate();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Day nextWeekDay = new Day(calendar);
        return new Week(nextWeekDay);
    }
    public Week previousNextWeek(){
        // מחזיר את השבוע הקודם
        Calendar calendar = week[0].getSameCalendarDate();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Day nextWeekDay = new Day(calendar);
        return new Week(nextWeekDay);
    }

    public Day[] getDays() {
        return week;
    }
}
