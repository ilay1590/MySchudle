package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    // מחלקה שאחראית על מסך רשימת האירועים, למחלקה זאת יש רשימה ששם מוצגים האירועים

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Intent intent = getIntent();
        listView = findViewById(R.id.lvEventsList);
        reloadEventsFromDatabase();
    }

    @Override
    public void onBackPressed() {
        //כאשר המשתמש ילחץ על חזור, המסך ייסים את פעילותו על מנת לחסוך במשאבים
        super.onBackPressed();
        finish();
    }

    public void reloadEventsFromDatabase() {
        // טוען ומציג את כל האירועים מהפיירבייס, ובנוסף שם את האפשרות שבכל פעם
        // שהמתמש ילחץ על אירוע, האירוע ייפתח בדיאלוג
        ArrayList<Event> eventArrayList = null;

        ArrayList<DataSnapshot> databaseReferenceArrayList = new ArrayList<>();
        MainActivity.dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event;
                DatabaseEvent databaseEvent;

                ArrayList<Event> eventArrayList = new ArrayList<Event>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        databaseEvent = dataSnapshot1.getValue(DatabaseEvent.class);
                        event = databaseEvent.convertToEvent();
                        eventArrayList.add(new Event(event));
                        databaseReferenceArrayList.add(dataSnapshot1);
                    }
                }
                ArrayList<String> eventsNames = new ArrayList<String>();
                for (int i = 0; i < eventArrayList.size(); i++) {
                    eventsNames.add(eventArrayList.get(i).getName()+ "  "+eventArrayList.get(i).getDateStringFormat());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(EventListActivity.this, android.R.layout.simple_list_item_1,eventsNames);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Event event = eventArrayList.get(position);
                        eventPresentationDialog(event,databaseReferenceArrayList.get(position));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void eventPresentationDialog(Event event, DataSnapshot dataSnapshot) {
        // מקבל את האירוע הנבחר ואת ה"מיקום" שלו בפיירבייס, ופותח דיאלוג מובנה שאיתו המשתמש יוכל לשנות \
        // למחוק \ לראות את האירוע בצורה טובה יותר בדיאלוג.
        final Dialog dialog = new Dialog(EventListActivity.this);
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


    public boolean checkValidHour(int startTimeMin, int startTimeHours, int endTimeMin, int endTimeHours){
        // בודק האם השעה שהוכנסה היא תקינה
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

    public void addEventToData(Event event) {
        // מוסיף את האירוע למאגר הנתונים בפיירבייס
        DatabaseReference databaseReference;
        DatabaseEvent databaseEvent = new DatabaseEvent(event);
        databaseReference = MainActivity.dbReference.child(databaseEvent.getCalendarDate());
        databaseReference = databaseReference.child(databaseEvent.getStartHours() + ":" + databaseEvent.getStartMinutes()
                + "-" + databaseEvent.getEndHours() + ":" + databaseEvent.getEndMinutes()+" "+databaseEvent.getName());
        databaseReference.setValue(databaseEvent);
    }


}