package com.example.myapplication;

public class DatabaseEvent{

    // מחלקת עזר על מנת לשמור ולכתוב אירועים לפיירבייס

    private String startHours,startMinutes,endHours,endMinutes;
    private String name;
    private String description;
    private String calendarDate;

    public DatabaseEvent(Event event){
        // בנאי מעתיק
        this.startHours = event.getStartHours();
        this.endHours = event.getEndHours();
        this.startMinutes = event.getStartMinutes();
        this.endMinutes = event.getEndMinutes();
        this.name = event.getName();
        this.description = event.getDescription();
        this.calendarDate = event.getDateStringFormat();
    }

    public DatabaseEvent(){
        //בנאי ריק

    }

    public Event convertToEvent(){
        // מחזירה את האירוע בצורתו המקורית, לאחר שחזר \ שנכתב לפיירבייס

        Event event = new Event(calendarDate,name,description,startHours,startMinutes,endHours,endMinutes);
        return event;
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

    public String eventInfo(){
        // מחזיר תצורה של כל מידע האיוונט בשרשרת
        String info =
                "Name: "+this.getName() + ", Time: " + this.getEndHours()+":"+this.getEndMinutes()+"-"+this.getStartHours()
                        +":"+this.getStartMinutes()+" ,Date:"+this.getCalendarDate()+ " ,Description:" + this.getDescription();
        return info;
    }


    public String getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
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

