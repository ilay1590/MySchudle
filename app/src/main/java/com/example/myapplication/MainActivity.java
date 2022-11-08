package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    // המסך הראשי, שבו מתנהל מרכז האפליקציה

    private TextView tvDisCurrentDate,tvDisCurrentMonths,tvSun,tvMon,tvTue,tvWed,tvThu,tvFri,tvSat;
    private LinearLayout sunContext,monContext,tueContext,wedContext,thuContext,friContext,satContext;
    private static TextView[] tvDays;
    public static LinearLayout[] daysContext;
    private static final String TAG = "MyActivity";
    private Toolbar toolbar;

    public static Week currentWeek = new Week();

    public static FirebaseAuth firebaseAuth;

    public static int maxNumOfEventsPerDay = 30;

    public static DatabaseReference dbReference;
    private FirebaseDatabase database;
    public static String UID;

    public static boolean isNotifiedBeforeExit = false;

    public static long timeToNotifyUser = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent();

        handleUser();
        reloadTextViews(currentWeek);
        reloadWeekEventsFromDatabase();
        PreferenceManager.setDefaultValues(this,R.xml.preference,false);
    }

    @Override
    protected void onPause() {
        if (!isNotifiedBeforeExit) {
            createNotificationChannel();
            notifyUserInTime();
            isNotifiedBeforeExit = true;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (!isNotifiedBeforeExit) {
            createNotificationChannel();
            notifyUserInTime();
            isNotifiedBeforeExit = true;
        }
        super.onDestroy();
    }

    public void createNotificationChannel(){
        // יוצר צאנל של ההתראות
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyUser",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notifications(long time){
        // מחשב את הזמן של ההתראות ומביא התראה בזמנה
        Intent intent = new Intent(MainActivity.this,UserNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);
    }


    public void notifyUserInTime(){
        dbReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    /*task = task.getResult().child()
                    String info = String.valueOf(task.getResult().);*/
                    for (DataSnapshot daysEvents: task.getResult().getChildren()) {
                        for (DataSnapshot eventFromDatabase: daysEvents.getChildren()){
                            DatabaseEvent databaseEvent = eventFromDatabase.getValue(DatabaseEvent.class);
                            Event event = databaseEvent.convertToEvent();
                            String myDate = event.getDateStringFormat() +" "+event.getStartHours()+":"+event.getStartMinutes();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            java.util.Date date = null;
                            try {
                                date = sdf.parse(myDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long millis = date.getTime();
                            timeToNotifyUser = millis;
                            if (timeToNotifyUser > System.currentTimeMillis()) {
                                notifications(timeToNotifyUser);
                            }
                            Log.i(TAG, "onComplete: "+"eventTime:"+timeToNotifyUser +" currentTime:"+System.currentTimeMillis());

                        }
                    }
                }
            }
        });
    }


    public void handleUser(){
        // מקבל את המשתמש ויוצר בשבילו מקום בזכרון של פיירבייס
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference("Users:").child(firebaseAuth.getUid());
        dbReference.keepSynced(true);
        UID = firebaseAuth.getUid();
    }

    public void reloadWeekEventsFromDatabase() {

        // מקבל מפיירבייס את כל האיוונטים הקיימים ומדפיס אותם על המסך

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                Event event;
                String name, time, description;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dayDate = dataSnapshot.getKey();
                    if (currentWeek.checkIfDayOnWeek(dayDate) != -1) {
                        int i = currentWeek.checkIfDayOnWeek(dayDate);
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.i(TAG, "reloadWeekEventsFromDatabase: "+dataSnapshot.getRef().toString());
                            DatabaseEvent databaseEvent = dataSnapshot1.getValue(DatabaseEvent.class);
                            event = databaseEvent.convertToEvent();
                            name = event.getName()+"\n";
                            time = event.getStartHours()+":"+event.getStartMinutes() +"\n"
                                    + event.getEndHours()+":"+event.getEndMinutes()+"\n";
                            description = event.getDescription()+"\n";
                            TextView tvEventname = (TextView) daysContext[i].getChildAt(count);
                            tvEventname.setText(name);
                            count++;
                            TextView tvEventTime = (TextView) daysContext[i].getChildAt(count);
                            tvEventTime.setText(time);
                            count++;
                            TextView tvEventDescription = (TextView) daysContext[i].getChildAt(count);
                            tvEventDescription.setText(description);
                            count++;
                            if (tvEventname.getText().length() != 0){
                                TextView tvLine = (TextView) daysContext[i].getChildAt(count);
                                tvLine.setTextSize(8);
                                tvLine.setText("-----------------------");
                            }
                            count++;
                            tvEventname.setClickable(true);
                            Event finalEvent = event;
                            tvEventname.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    eventPresentationDialog(finalEvent,dataSnapshot1);
                                }
                            });
                        }
                        count = 0;

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        dbReference.addValueEventListener(valueEventListener);
    }




    public void addEventToData(Event event) {

        //מוסיף אירוע חדש ושולח לפיירבייס
        DatabaseReference databaseReference;
        DatabaseEvent databaseEvent = new DatabaseEvent(event);
        databaseReference = dbReference.child(databaseEvent.getCalendarDate());
        databaseReference = databaseReference.child(databaseEvent.getStartHours() + ":" + databaseEvent.getStartMinutes()
                + "-" + databaseEvent.getEndHours() + ":" + databaseEvent.getEndMinutes()+" "+databaseEvent.getName());
        databaseReference.setValue(databaseEvent);
    }

    public void eventPresentationDialog(Event event, DataSnapshot dataSnapshot) {
        // מקבל את האירוע הנבחר ואת ה"מיקום" שלו בפיירבייס, ופותח דיאלוג מובנה שאיתו המשתמש יוכל לשנות \
        // למחוק \ לראות את האירוע בצורה טובה יותר בדיאלוג.
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.activity_event_presentation);

            final EditText etName = (EditText) dialog.findViewById(R.id.etNamePre);

            final EditText etDate = (EditText) dialog.findViewById(R.id.etDatePre);

            final EditText etStartTimeHours = (EditText) dialog.findViewById(R.id.etStartTimeHoursPre);
            final EditText etStartTimeMinutes = (EditText) dialog.findViewById(R.id.etStartTimeMinPre);
            final EditText etEndTimeHours = (EditText) dialog.findViewById(R.id.etEndTimeHoursPre);
            final EditText etEndTimeMin = (EditText) dialog.findViewById(R.id.etEndTimeMinPre);

            final EditText etDescription = (EditText) dialog.findViewById(R.id.etDescriptionPre);

            final Button btnSave = (Button) dialog.findViewById(R.id.btnSaveChanges);
            final ImageButton btnDelete = (ImageButton) dialog.findViewById(R.id.btnDelete);

            dialog.show();

            etName.setText(event.getName());
            etDate.setText(event.getDateStringFormat());
            etStartTimeHours.setText(event.getStartHours());
            etStartTimeMinutes.setText(event.getStartMinutes());
            etEndTimeHours.setText(event.getEndHours());
            etEndTimeMin.setText(event.getEndMinutes());
            etDescription.setText(event.getDescription());

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSnapshot.getRef().removeValue();
                    String newName = etName.getText().toString();
                    String newDate = etDate.getText().toString();
                    String newStartHours = etStartTimeHours.getText().toString();
                    String newStartMin = etStartTimeMinutes.getText().toString();
                    String newEndHours = etEndTimeHours.getText().toString();
                    String newEndMin = etEndTimeMin.getText().toString();
                    String newDescription = etDescription.getText().toString();
                    int startTimeHours = Integer.valueOf(newStartHours);
                    int startTimeMinutes = Integer.valueOf(newStartMin);
                    int endTimeHours = Integer.valueOf(newEndHours);
                    int endTimeMinutes = Integer.valueOf(newEndMin);

                    if (newEndHours.length() != 2) {
                        newEndHours = "0" + newEndHours;
                    }
                    if (newStartHours.length() != 2) {
                        newStartHours = "0" + newStartHours;
                    }
                    if (!checkValidHour(startTimeMinutes, startTimeHours, endTimeMinutes, endTimeHours)) {
                        Toast.makeText(getApplicationContext(), "Time input is not valid!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else if (newStartMin.length() != 2 || newEndMin.length() != 2) {
                        Toast.makeText(getApplicationContext(), "Time input is not valid!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else if (newName.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please input name", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {

                        addEventToData(new Event(newDate, newName, newDescription, newStartHours, newStartMin,
                                newEndHours, newEndMin));
                        dialog.dismiss();
                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataSnapshot.getRef().removeValue();
                    dialog.dismiss();
                }
             });

    }

    public void refreshScreen(){
        //מנקה את המסך ומדפיס מחדש לעת הצורך
        clearTextViews();
        reloadWeekEventsFromDatabase();
        resetColorsOfBackground();
        changeColorToCurrentDay();

    }
    public void resetColorsOfBackground(){
        Date date = new Date();
        for (int i = 0; i < 7; i++){
                int rgb = Color.rgb(255,255,255);
                daysContext[i].setBackgroundColor(rgb);
                tvDays[i].setBackgroundColor(rgb);
        }
    }

    public void changeColorToCurrentDay(){
        Date date = new Date();
        for (int i = 0; i < 7; i++){
            if (currentWeek.getDays()[i].getDateStringFormat().equals(date.getDateStringFormat())){
                Log.i(TAG, "changeColorToCurrentDay: "+currentWeek.getDays()[i].getDateStringFormat());
                Log.i(TAG, "changeColorToCurrentDay: "+date.getDateStringFormat());
                int rgb = Color.rgb(223,255,185);
                daysContext[i].setBackgroundColor(rgb);
                tvDays[i].setBackgroundColor(rgb);
            }
        }
    }

    public void moveToPreviousWeek(View view){
        // כאשר יילחץ הכפתור השמאלי פעולה זו תתבצע, יוצרת את השבוע הקודם לשבוע הנוכחי
        // ומדפיסה את כל אירועיו על המסך
        try {
            currentWeek = currentWeek.previousNextWeek();
            refreshScreen();
            for (int i = 0; i < tvDays.length; i++) {
                String name = currentWeek.getDays()[i].getDayName() + " " + currentWeek.getDays()[i].getDayNumOfMonth();
                tvDays[i].setText(name);
            }
            String month1 = currentWeek.getDays()[0].getMonthName();
            String month2 = currentWeek.getDays()[6].getMonthName();
            if (!month1.equals(month2)){
                tvDisCurrentMonths.setText(month1 + "-"+month2);
            }else {
                tvDisCurrentMonths.setText(month1);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"an Error accured, please try again",Toast.LENGTH_SHORT).show();
        }
    }

    public void moveToNextWeek(View view){
        // כאשר יילחץ הכפתור הימני פעולה זו תתבצע, יוצרת את השבוע הההבא לשבוע הנוכחי
        // ומדפיסה את כל אירועיו על המסך
        try {
            currentWeek = currentWeek.getNextWeek();
            refreshScreen();
            for (int i = 0; i < tvDays.length; i++) {
                String name = currentWeek.getDays()[i].getDayName() + " " + currentWeek.getDays()[i].getDayNumOfMonth();
                tvDays[i].setText(name);
            }
            String month1 = currentWeek.getDays()[0].getMonthName();
            String month2 = currentWeek.getDays()[6].getMonthName();
            if (!month1.equals(month2)){
                tvDisCurrentMonths.setText(month1 + "-"+month2);
            }else {
                tvDisCurrentMonths.setText(month1);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"an Error accured, please try again",Toast.LENGTH_SHORT).show();
        }


    }

    public void handleIntent(){
        //מקבל את האינטנט ממחלקת הכניסה
        Intent intent = getIntent();
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // יוצר את התפריט
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public void clearTextViews(){
        // מנקה את כל האירועים מהמסך
        for (int i = 0; i < daysContext.length; i++) {
            for (int j = 0; j < daysContext[i].getChildCount(); j++) {
                TextView textView = (TextView) daysContext[i].getChildAt(j);
                textView.setText("");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // פעולה המכווינה את כל פעולות התפריט
        int id = item.getItemId();

        if (id == R.id.settings){
            settings();
        }

        else if (id == R.id.events_list){
            eventsList();
        }
        else if (id == R.id.app_info){
            showAppInfo();
        }

        return true;
    }

    private void showAppInfo() {
        // פותח את מידע האפליקציה בטלפון
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "An error occur", Toast.LENGTH_SHORT).show();
        }
    }


    private void eventsList() {
        // הקריאה לפעולה תביא לתחילת המסך של כל האירועים
        Intent intent = new Intent(MainActivity.this,EventListActivity.class);
        startActivity(intent);
    }

    private void settings() {

        //קורא למסך ההגדרות
        startActivity(new Intent(this,SettingsActivity.class));

        applySetting();
    }

    public void addNewEvent(View view) {

        // קורא לדיאלוג האחראי על הוספת אירועים חדשים, בדיאלוג זה יש בדיקה של האם הקלט הוא
        // הגיוני ולאחר מכן ייצור איוונט, במקרה של ביטול האירוע לא יישמר
        try {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.activity_add_event);

            final EditText etName = (EditText) dialog.findViewById(R.id.etEventName);

            final EditText etDay = (EditText) dialog.findViewById(R.id.etPickDay);
            final EditText etMonth = (EditText) dialog.findViewById(R.id.etPickMonth);
            final EditText etYear = (EditText) dialog.findViewById(R.id.etPickYear);

            final EditText etStartTimeHours = (EditText) dialog.findViewById(R.id.etStartTimeHours);
            final EditText etStartTimeMin = (EditText) dialog.findViewById(R.id.etStartTimeMin);
            final EditText etEndTimeHours = (EditText) dialog.findViewById(R.id.etEndTimeHours);
            final EditText etEndTimeMin = (EditText) dialog.findViewById(R.id.etEndTimeMin);

            final EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);

            Button btnAdd = (Button) dialog.findViewById(R.id.btnAdd);
            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

            //event date
            String day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"";
            String month =(Calendar.getInstance().get(Calendar.MONTH)+1)+"";
            String year = Calendar.getInstance().get(Calendar.YEAR)+"";

            Log.i(TAG, "addNewEventDialog: "+day);


            dialog.show();

            etDay.setText(day);
            etMonth.setText(month);
            etYear.setText(year);

            etStartTimeHours.setText("00");
            etStartTimeMin.setText("00");
            etEndTimeHours.setText("00");
            etEndTimeMin.setText("00");

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // name, time and description

                    String name = etName.getText().toString();

                    String textStartTimeHours = etStartTimeHours.getText().toString();
                    String textStartTimeMin = etStartTimeMin.getText().toString();
                    String textEndTimeHours = etEndTimeHours.getText().toString();
                    String textEndTimeMin = etEndTimeMin.getText().toString();

                    if (containsLetters(textEndTimeHours+textStartTimeHours+textEndTimeMin+textStartTimeMin)) {
                        Toast.makeText(getApplicationContext(),"Time input is not valid!", Toast.LENGTH_LONG).show();
                    }

                    int startTimeHours = Integer.valueOf(textStartTimeHours);
                    int startTimeMinutes = Integer.valueOf(textStartTimeMin);
                    int endTimeHours = Integer.valueOf(textEndTimeHours);
                    int endTimeMinutes = Integer.valueOf(textEndTimeMin);

                    if (textEndTimeHours.length() != 2){
                        textEndTimeHours = "0"+textEndTimeHours;
                    }
                    if (textStartTimeHours.length() != 2){
                        textStartTimeHours = "0"+textStartTimeHours;
                    }
                    if (!checkValidHour(startTimeMinutes,startTimeHours,endTimeMinutes,endTimeHours)){
                        Toast.makeText(getApplicationContext(),"Time input is not valid!", Toast.LENGTH_LONG).show();
                    }else if (textStartTimeMin.length() != 2 || textEndTimeMin.length() != 2){
                        Toast.makeText(getApplicationContext(),"Time input is not valid!", Toast.LENGTH_LONG).show();
                    }else if (name.length() == 0){
                        Toast.makeText(getApplicationContext(),"Please input name", Toast.LENGTH_LONG).show();}
                    else {

                        String description = etDescription.getText().toString();

                        // adding to specific Day the event, looking for the day with his date,
                        //if day not exist creating one.

                        String day = etDay.getText().toString();
                        String month = etMonth.getText().toString();
                        String year = etYear.getText().toString();

                        Calendar date = Calendar.getInstance();
                        try {
                            date.set(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day), startTimeHours, startTimeMinutes);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"The date is not proper", Toast.LENGTH_LONG).show();
                        }
                        Event event = new Event(date, name, description, textStartTimeHours,textStartTimeMin,textEndTimeHours,textEndTimeMin);

                        addEventToData(event);

                        dialog.dismiss();

                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

        }catch (Exception e){
            Log.e(TAG, "Error in: addNewEventDialog: "+e.toString());
        }
    }

    public boolean checkValidHour(int startTimeMin, int startTimeHours, int endTimeMin, int endTimeHours){
        // בדיקה של האם השעה שהוכנסה אכן תקינה
        if (startTimeMin > 59 || startTimeMin < 0 || endTimeMin > 59 || endTimeMin < 0) {
            return false;
        }
        if (startTimeHours > 23 || startTimeHours < 0 || endTimeHours > 23 || endTimeHours < 0){
            return false;
        }
        if (endTimeHours < startTimeHours) {
            return false;
        }
        if (endTimeHours == startTimeHours){
            if (startTimeMin >= endTimeMin){
                return false;
            }
        }
        return true;
    }

    public void onBackPressed()
    // כאשר המשתמש לוחץ חזור דיאלוג של יציאה מהמשתמש נפתח, בלחיצה על כן המשתמש יחזור למסך הכניסה
    {
        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.activity_back_to_login);

        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.btn_no);

        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    public void reloadTextViews(Week week){

        // מקבל את השבוע ויוצר בשבילו את הכותרות המתאימות
        // גם מתאים את כל הצגות הטקסטים, הטולבר, וכו'
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvSun = findViewById(R.id.tvSun);
        tvMon = findViewById(R.id.tvMon);
        tvTue = findViewById(R.id.tvTue);
        tvWed = findViewById(R.id.tvWed);
        tvThu = findViewById(R.id.tvThu);
        tvFri = findViewById(R.id.tvFri);
        tvSat = findViewById(R.id.tvSat);
        tvDays = new TextView[7];
        tvDays[0] = tvSun;
        tvDays[1] = tvMon;
        tvDays[2] = tvTue;
        tvDays[3] = tvWed;
        tvDays[4] = tvThu;
        tvDays[5] = tvFri;
        tvDays[6] = tvSat;
        sunContext = findViewById(R.id.sunContext);
        monContext = findViewById(R.id.monContext);
        tueContext = findViewById(R.id.tueContext);
        wedContext = findViewById(R.id.wedContext);
        thuContext = findViewById(R.id.thuContext);
        friContext = findViewById(R.id.friContext);
        satContext = findViewById(R.id.satContext);
        daysContext = new LinearLayout[7];
        daysContext[0] = sunContext;
        daysContext[1] = monContext;
        daysContext[2] = tueContext;
        daysContext[3] = wedContext;
        daysContext[4] = thuContext;
        daysContext[5] = friContext;
        daysContext[6] = satContext;
        for (int i = 0; i < tvDays.length; i++) {
            String name = currentWeek.getDays()[i].getDayName() + " " + currentWeek.getDays()[i].getDayNumOfMonth();
            tvDays[i].setText(name);
        }

        applySetting();

        tvDisCurrentDate = findViewById(R.id.tvDisCurrentDate);
        tvDisCurrentDate.setText("Today: "+currentWeek.getCurrentDay().getDateStringFormat());
        tvDisCurrentMonths = findViewById(R.id.tvDisCurrentMonths);
        String month1 = week.getDays()[0].getMonthName();
        String month2 = week.getDays()[6].getMonthName();
        if (!month1.equals(month2)){
            tvDisCurrentMonths.setText(month1 + "-"+month2);
        }else {
            tvDisCurrentMonths.setText(month1);
        }
    }


    public static boolean containsLetters(String string) {
        // בודק אם יש אותיות בקלט
        if (string == null || string.isEmpty()) {
            return false;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isLetter(string.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void applySetting(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int nameSizeList = Integer.parseInt(String.valueOf(sharedPreferences.getString(UID+SettingsFragment.LIST_NAME_SIZE, "15")));
        int timeSizeList = Integer.parseInt(String.valueOf(sharedPreferences.getString(UID+SettingsFragment.LIST_TIME_SIZE, "12")));
        int descriptionSizeList = Integer.parseInt(String.valueOf(sharedPreferences.getString(UID+SettingsFragment.LIST_DESCRIPTION_SIZE, "12")));
        for (int i = 0; i < daysContext.length; i++) {
            for (int j = 0; j < 4*maxNumOfEventsPerDay; j++) {
                TextView textView = new TextView(this);
                if (j % 4 == 0 ){
                    textView.setTextSize(nameSizeList);
                    Log.i(TAG, "reloadTextViews: "+UID+SettingsFragment.LIST_NAME_SIZE);
                }
                else if (j % 4 == 1 ){
                    textView.setTextSize(timeSizeList);
                }
                else if (j % 4 == 2){
                    textView.setTextSize(descriptionSizeList);
                }
                daysContext[i].addView(textView);
            }
        }
        refreshScreen();
    }
}