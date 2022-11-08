package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragment{

    // מחלקה שמטרתה ליצור פרגמט שבו יהיו ההגדרות


    private static final String TAG = "SettingsFragment";

    public static String LIST_NAME_SIZE = "list_name_size";
    public static String LIST_TIME_SIZE = "list_time_size";
    public static String LIST_DESCRIPTION_SIZE = "list_description_size";
    public static String SWITCH_NOTIFICATIONS = "switch_notifications";

    public static boolean userNotify = true;

    private Preference nameSizeList;
    private Preference timeSizeList;
    private Preference descriptionSizeList;
    private SharedPreferences.OnSharedPreferenceChangeListener  preferenceChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        // ע"י שרד פרפנסס האפליקציה תזכור את ההגדרות
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        nameSizeList = findPreference(LIST_NAME_SIZE);
        timeSizeList = findPreference(LIST_TIME_SIZE);
        descriptionSizeList = findPreference(LIST_DESCRIPTION_SIZE);
        String UID = MainActivity.UID;
        nameSizeList.setKey(UID+nameSizeList.getKey());
        timeSizeList.setKey(UID+timeSizeList.getKey());
        descriptionSizeList.setKey(UID+descriptionSizeList.getKey());

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(nameSizeList.getKey())){
                    nameSizeList.setSummary(sharedPreferences.getString(key, "15"));
                }
                if (key.equals(timeSizeList.getKey())){
                    timeSizeList.setSummary(sharedPreferences.getString(key, "12"));
                }
                if (key.equals(descriptionSizeList.getKey())){
                    descriptionSizeList.setSummary(sharedPreferences.getString(key, "12"));
                }
            }
        };


    }

    @Override
    public void onResume() {
        super.onResume();
        //מתי שההגדרות נפתחות, צריך לשים בתיאור את הערך המתאים
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        nameSizeList.setSummary(getPreferenceScreen().getSharedPreferences().getString(nameSizeList.getKey(),"15"));
        timeSizeList.setSummary(getPreferenceScreen().getSharedPreferences().getString(timeSizeList.getKey(), "12"));
        descriptionSizeList.setSummary(getPreferenceScreen().getSharedPreferences().getString(descriptionSizeList.getKey(), "12"));

        nameSizeList.setDefaultValue(getPreferenceScreen().getSharedPreferences().getString(nameSizeList.getKey(),"15"));
        timeSizeList.setDefaultValue(getPreferenceScreen().getSharedPreferences().getString(timeSizeList.getKey(), "12"));
        descriptionSizeList.setDefaultValue(getPreferenceScreen().getSharedPreferences().getString(descriptionSizeList.getKey(), "12"));

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        Toast.makeText(getActivity().getApplicationContext(), "To apply the changes, restart the app.",
                Toast.LENGTH_SHORT).show();
    }


}