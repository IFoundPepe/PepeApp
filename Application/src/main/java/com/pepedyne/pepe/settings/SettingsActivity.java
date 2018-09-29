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
         exercisesPref.setSummary((CharSequence) preferenceEntry.getValue());
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
      if (key.equals(this.getString(R.string.tweet_servo_min_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Tweet Min: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.tweet_servo_max_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Tweet Max: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.flap_servo_min_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Flap Min: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.flap_servo_max_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Flap Max: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.look_servo_min_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Look Min: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.look_servo_max_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Look Max: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.lean_servo_min_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Lean Min: " + sharedPreferences.getString(key, ""));
      }
      else if (key.equals(this.getString(R.string.lean_servo_max_key)))
      {
         // Set summary to be the user-description for the selected value
         Preference exercisesPref = settingsFragment.findPreference(key);
         exercisesPref.setSummary(sharedPreferences.getString(key, ""));
         System.out.println("Updated Lean Max: " + sharedPreferences.getString(key, ""));
      }
   }
}
