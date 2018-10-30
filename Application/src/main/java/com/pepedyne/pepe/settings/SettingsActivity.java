package com.pepedyne.pepe.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bluetoothlegatt.R;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

   // PreferenceFragment (extends)
   private SettingsFragment settingsFragment;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      settingsFragment = new SettingsFragment();
      // Display the fragment as the main content.
      getFragmentManager().beginTransaction()
              .replace(android.R.id.content, settingsFragment).commit();
   }

   @Override
   public void onStart() {
      super.onStart();
      Map<String, ?> preferencesMap = settingsFragment.getPreferenceManager().getSharedPreferences().getAll();
      // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
      for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet())
      {
         Preference exercisesPref = settingsFragment.findPreference(preferenceEntry.getKey());
         if (exercisesPref != null)
         {
            exercisesPref.setSummary((CharSequence) preferenceEntry.getValue());
         }
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      settingsFragment.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
   }

   @Override
   public void onPause() {
      settingsFragment.getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
      super.onPause();
   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      Preference exercisesPref = settingsFragment.findPreference(key);
      exercisesPref.setSummary(sharedPreferences.getString(key, ""));

   }
}
