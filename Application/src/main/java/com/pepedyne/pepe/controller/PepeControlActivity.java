/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepedyne.pepe.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothCallbackInf;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeService;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProvider;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProviderImpl;
import com.pepedyne.pepe.controller.InputManagerCompat.InputDeviceListener;
import com.pepedyne.pepe.settings.SettingsActivity;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class PepeControlActivity extends Activity implements InputDeviceListener, BluetoothCallbackInf {
   private final static String TAG = PepeControlActivity.class.getSimpleName();

   // Input manager for Pepe control via controller
   private InputManagerCompat mInputManager;

   // Derived Values
   public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
   public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

   private JoystickView joystick;
   private TextView mDataField;
   private String mDeviceName;
   private String mDeviceAddress;
   private BluetoothLeServiceProvider bluetoothLeServiceProvider;
   private boolean mConnected = false;
   private PepeBluetoothConnectionManager pepeManager;

   // only sends data after this number of data points collected
   // intent is to reduce jitter

   private int mInterval = 10; // 0.5 seconds by default, can be changed later
   private Handler mHandler;

   // matain if PepAI is active
   private boolean PepAIState = false;
   final int PepAIIntervalVariance = 300;  //number of wake up intervals
   private int PepAIActionCounter = 0;

   @SuppressLint("ClickableViewAccessibility")
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.pepe_control);

      final Intent intent = getIntent();
      mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
      mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

      bluetoothLeServiceProvider = new BluetoothLeServiceProviderImpl(this);
      bluetoothLeServiceProvider.registerCallback(this);
      bluetoothLeServiceProvider.onCreate(this);

      pepeManager = new PepeBluetoothConnectionManager(this);

      getActionBar().setTitle(mDeviceName);
      getActionBar().setDisplayHomeAsUpEnabled(true);

      mHandler = new Handler();
      startRepeatingTask();

      joystick = (JoystickView) findViewById(R.id.joystick);

      joystick.setOnTouchListener(new JoystickView.OnTouchListener() {

         @Override
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
               pepeManager.setAngle_value(0);
               pepeManager.setStrength_value(0);
            }
            return false;
         }

      });
      joystick.setOnMoveListener(new JoystickView.OnMoveListener() {

         @Override
         public void onMove(int angle, int strength) {
            // do whatever you want
            pepeManager.setAngle_value(angle);
            pepeManager.setStrength_value(strength);
            pepeManager.calculateLookAndLean();
         }
      });

      final Button flapButton = (Button) findViewById(R.id.flap);
      flapButton.setOnTouchListener(new OnTouchListener() {
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
               pepeManager.flapUp();
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
               v.performClick();
               pepeManager.flapDown();
            }
            return false;
         }
      });

      final Button tweetButton = (Button) findViewById(R.id.tweet);
      tweetButton.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction())
            {
               case MotionEvent.ACTION_DOWN:
                  pepeManager.tweet();
                  return true;
               case MotionEvent.ACTION_UP:
                  v.performClick();
                  pepeManager.silence();
                  return true;
            }
            return false;
         }
      });

      // PepAI
      final Button pepaiButton = (Button) findViewById(R.id.PepAI);
      pepaiButton.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
               if (PepAIState)
               {
                  Log.d("PEPE DEBUG", "PepAI stopped");
                  PepAIState = false;
               }
               else
               {
                  Log.d("PEPE DEBUG", "PepAI started");
                  PepAIState = true;
               }
            }
            return true;
         }
      });

      final Button editSettings = (Button) findViewById(R.id.settingsButton);
      editSettings.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
              if (event.getAction() == MotionEvent.ACTION_UP)
              {
                 final Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                 startActivity(intent);
              }
              return true;
           }
        });

      mInputManager = InputManagerCompat.Factory.getInputManager(this);
      mInputManager.registerInputDeviceListener(this, null);
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      Log.d("PEPE DEBUG", "keyCode: " + keyCode);
      // 99/67(silence), 96/23(tweet), 100/62(flap)
      switch (keyCode)
      {
         case KeyEvent.KEYCODE_BUTTON_A: // press X: 96 - do nothing?
            return true;
         case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99 - do nothing?
            return true;
         case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
            Log.d("PEPE DEBUG", "flap up");
            pepeManager.flapUp();
            pepeManager.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.onKeyUp(keyCode, event);
      }
   }

   @Override
   public boolean onKeyUp(int keyCode, KeyEvent event) {
      Log.d("PEPE DEBUG", "keyCode: " + keyCode);
      // 99/67(silence), 96/23(tweet), 100/62(flap)
      switch (keyCode)
      {
         case KeyEvent.KEYCODE_BUTTON_A: // press X: 96
            Log.d("PEPE DEBUG", "tweet");
            pepeManager.tweet();
            return true;
         case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99
            if (PepAIState)
            {
               Log.d("PEPE DEBUG", "PepAI stopped");
               PepAIState = false;
            }
            else
            {
               Log.d("PEPE DEBUG", "PepAI started");
               PepAIState = true;
            }
            return true;
         case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
            Log.d("PEPE DEBUG", "flap down");
            pepeManager.flapDown();
            pepeManager.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.onKeyUp(keyCode, event);
      }
   }

   Runnable mStatusChecker = new Runnable() {
      @Override
      public void run() {
         try
         {
            sendUpdatedPositionData(); //this function can change value of mInterval.
            PepAIActionCounter++;
            if (PepAIState && (PepAIActionCounter >= PepAIIntervalVariance))
            {
               runPepeAI();
               PepAIActionCounter = 0;
            }
         }
         finally
         {
            // 100% guarantee that this always happens, even if
            // your update method throws an exception
            mHandler.postDelayed(mStatusChecker, mInterval);
         }
      }
   };

   void startRepeatingTask() {
      mStatusChecker.run();
   }

   void stopRepeatingTask() {
      mHandler.removeCallbacks(mStatusChecker);
   }

   private void runPepeAI() {
      final int look_roll_variance = 4; // 1-3, nothing; 4, look left; 5, look right
      final int flap_roll_variance = 5; // 1-3, nothing; 4, flap once; 5, flap twice
      final int flap_wait_milli = 350;

      // Look first if rolled
      int look_roll = (int) Math.ceil(Math.random() * look_roll_variance);
      Log.d("PEPE DEBUG", "lookroll == " + look_roll);
      switch (look_roll)
      {
         case 3:
            pepeManager.setLook(445); // Look middle right
            sendAIPositionData();
         case 4:
            pepeManager.setLook(235); // Look middle left
            sendAIPositionData();
         default:
            pepeManager.setLook(340); // Don't look
            sendAIPositionData();
      }

      // Tweet always
      pepeManager.tweetRand();
      // flap last if rolled
      int flap_roll = (int) Math.ceil(Math.random() * flap_roll_variance);

      Log.d("PEPE DEBUG", "flaproll == " + flap_roll);
      switch (flap_roll)
      {
         case 4:
            Log.d("PEPE DEBUG", "flap once");
            pepeManager.flapUp();
            sendAIPositionData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            sendAIPositionData();
            android.os.SystemClock.sleep(flap_wait_milli + (flap_wait_milli / 2));
            pepeManager.flapUp();
            sendAIPositionData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            sendAIPositionData();

         case 5:
            Log.d("PEPE DEBUG", "flap once");
            pepeManager.flapUp();
            sendAIPositionData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            sendAIPositionData();

         default:
            // Don't flap
      }
   }

   @Override
   protected void onResume() {
      super.onResume();
      bluetoothLeServiceProvider.onResume(this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      bluetoothLeServiceProvider.onPause(this);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      bluetoothLeServiceProvider.onDestroy(this);
      stopRepeatingTask();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.gatt_services, menu);
      if (mConnected)
      {
         menu.findItem(R.id.menu_connect).setVisible(false);
         menu.findItem(R.id.menu_disconnect).setVisible(true);
      }
      else
      {
         menu.findItem(R.id.menu_connect).setVisible(true);
         menu.findItem(R.id.menu_disconnect).setVisible(false);
      }
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId())
      {
         case R.id.menu_connect:
            bluetoothLeServiceProvider.connect(mDeviceAddress);
            return true;
         case R.id.menu_disconnect:
            bluetoothLeServiceProvider.disconnect();
            return true;
         case android.R.id.home:
            onBackPressed();
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onConnect() {
      pepeManager.connectTweet();
   }

   @Override
   public void clearUserInterface() {
      //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
      //mDataField.setText(R.string.no_data);
   }

   @Override
   public void updateConnectionState(final int resourceId) {
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            // TODO: Commented out due to doesn't exist
//                mConnectionState.setText(resourceId);
         }
      });
   }

   @Override
   public void displayData(String data) {
      if (data != null)
      {
         mDataField.setText(data);
      }
   }

   @Override
   public void setServiceList(SimpleExpandableListAdapter adapter) {

   }

   private static IntentFilter makeGattUpdateIntentFilter() {
      final IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
      intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
      return intentFilter;
   }

   private boolean sendUpdatedPositionData() {

      String data = pepeManager.generateData();
      if ((bluetoothLeServiceProvider != null) && pepeManager.sendIt())
      {
         Log.d("PEPE DEBUG", "Data: " + data);
         bluetoothLeServiceProvider.send(data);
      }

      return false;
   }

   private void sendAIPositionData() {
      String data = pepeManager.generateData();
      if ((bluetoothLeServiceProvider != null) && pepeManager.sendIt())
      {
         Log.d("PEPE DEBUG", "Data: " + data);
         bluetoothLeServiceProvider.send(data);
      }
   }

   @Override
   public boolean onGenericMotionEvent(MotionEvent event) {
      Log.d("PEPE DEBUG", "Generic motion event occured!");
      mInputManager.onGenericMotionEvent(event);

      int eventSource = event.getSource();
      int pointerIndex = event.getPointerCount() - 1; // since we only care about the last location of the motion
      Log.d("PEPE EVENT", "to string: " + event.toString());
      Log.d("PEPE EVENT", "actionToString: " + event.actionToString(event.getAction()));
      int id = event.getDeviceId();
      if (-1 != id)
      {

         int pointerCount = event.getPointerCount();
         System.out.printf("At time %d:", event.getEventTime());
         for (int p = 0; p < pointerCount; p++)
         {
            // Copy/pasta from sendUpdatedPositionData
            Log.d("PEPE DEBUG", "Controller data");
            pepeManager.joystickLook(event.getY(p)); // -0.88 to 1.0
            pepeManager.silence();
            if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == 1.0)
            {
               pepeManager.leanBack();
               pepeManager.silence();
            }

            if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == -1.0)
            {
               pepeManager.leanForward();
               pepeManager.silence();
            }
         }
      }
      return super.onGenericMotionEvent(event);
   }

   @Override
   public void onInputDeviceAdded(int deviceId) {
      Log.d("PEPE DEBUG", "Input device " + deviceId + " has been added...");
   }

   @Override
   public void onInputDeviceChanged(int deviceId) {
      Log.d("PEPE DEBUG", "Input device " + deviceId + " has been changed...");
   }

   @Override
   public void onInputDeviceRemoved(int deviceId) {
      Log.d("PEPE DEBUG", "Input device " + deviceId + " has been removed...");
   }
}
