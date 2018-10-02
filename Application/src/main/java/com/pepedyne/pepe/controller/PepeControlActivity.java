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
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothCallbackInf;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProvider;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProviderImpl;
import com.pepedyne.pepe.dispatch.PepeDispatcher;
import com.pepedyne.pepe.dispatch.SendDataHandler;
import com.pepedyne.pepe.views.JoyConView;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class PepeControlActivity extends AppCompatActivity implements SendDataHandler, BluetoothCallbackInf {
   private final static String TAG = PepeControlActivity.class.getSimpleName();

   // Derived Values
   public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
   public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

   private TextView mDataField;
   private String mDeviceAddress;
   private BluetoothLeServiceProvider bluetoothLeServiceProvider;
   private PepeDispatcher pepeDispatcher;
   private Handler handler;
   private HandlerThread handlerThread;

   public PepeDispatcher getDispatcher() {
      return pepeDispatcher;
   }

   @SuppressLint("ClickableViewAccessibility")
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.pepe_control);

      final Intent intent = getIntent();
      String mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
      mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

      bluetoothLeServiceProvider = new BluetoothLeServiceProviderImpl(this);
      bluetoothLeServiceProvider.registerCallback(this);
      bluetoothLeServiceProvider.onCreate(this);

      pepeDispatcher = new PepeDispatcher(new PepeBluetoothConnectionManager(this), this);

      getSupportActionBar().setTitle(mDeviceName);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      findViewById(R.id.joystick);
      findViewById(R.id.flap);
      findViewById(R.id.tweet);
      findViewById(R.id.PepAI);
      findViewById(R.id.settingsButton);

      handlerThread = new HandlerThread("MyHandlerThread");
      handlerThread.start();
      Looper looper = handlerThread.getLooper();
      handler = new Handler(looper){
         @Override
         public void handleMessage(Message msg) {
            // process incoming messages here
            // this will run in the thread, which instantiates it
            Log.d("PEPE DEBUG", "Data: " + msg.obj.toString());

            bluetoothLeServiceProvider.send(msg.obj.toString());
         }
      };

//      JoyConView joycon = findViewById(R.id.joycon);
//      setContentView(joycon);
//      joycon.setFocusable(true);
//      joycon.setFocusableInTouchMode(true);
   }

   @Override
   protected void onResume() {
      super.onResume();
      pepeDispatcher.reset();
      bluetoothLeServiceProvider.onResume(this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      bluetoothLeServiceProvider.onPause(this);
      // TODO - MAYBE NOT
      pepeDispatcher.stop();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      bluetoothLeServiceProvider.onDestroy(this);
      pepeDispatcher.stop();
      handlerThread.quit();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.gatt_services, menu);
      menu.findItem(R.id.menu_connect).setVisible(true);
      menu.findItem(R.id.menu_disconnect).setVisible(false);
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
      pepeDispatcher.connectTweet();
   }

   @Override
   public void clearUserInterface() {
   }

   @Override
   public void updateConnectionState(final int resourceId) {
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

   @Override
   public boolean sendData() {
      if ((bluetoothLeServiceProvider != null) && pepeDispatcher.sendIt())
      {
         String data = pepeDispatcher.generateData();
         Message msg = new Message();
         msg.obj = data;
         handler.sendMessage(msg);
      }
      return false;
   }


//   private void debugKeyEvent(KeyEvent event) {
//      Log.i("\tPEPE DEBUG","--------------------------");
//      Log.d("PEPE DEBUG", "keyDown keyCode: " + event.getKeyCode());
//      Log.i("\tPEPE DEBUG", "KeyEvent DeviceId: " + event.getDeviceId());
//      Log.i("\tPEPE DEBUG", "KeyEvent Id: " + event.getDevice().getId());
//      Log.i("\tPEPE DEBUG", "KeyEvent getFlags: " + event.getFlags());
//      Log.i("\tPEPE DEBUG", "KeyEvent getDownTime: " + event.getDownTime());
//
//      Log.i("\tPEPE DEBUG", "KeyEvent getCharacters: " + event.getCharacters());
//      Log.i("\tPEPE DEBUG", "KeyEvent MetaState: " + event.getMetaState());
//      Log.i("\tPEPE DEBUG", "KeyEvent getModifiers: " + event.getModifiers());
//      Log.i("\tPEPE DEBUG", "KeyEvent getRepeatCount: " + event.getRepeatCount());
//
//      Log.i("\tPEPE DEBUG", "KeyEvent getAction: " + event.getAction());
//   }

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
//   public boolean onKeyDown(int keyCode, KeyEvent event) {
//      this.debugKeyEvent(event);

      // 99/67(silence), 96/23(tweet), 100/62(flap)
      switch (event.getKeyCode())
      {
         case KeyEvent.KEYCODE_BUTTON_A: // press X: 96 - do nothing?
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() < 1)
            {
               Log.d("PEPE DEBUG", "tweet");
               pepeDispatcher.tweet();
            }
            return true;
         case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
         case KeyEvent.KEYCODE_BUTTON_B: // press JoyCon X - 97
            return true;
         case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99 - do nothing?
            return true;
         case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
            Log.d("PEPE DEBUG", "flap up");
            pepeDispatcher.flapUp();
            pepeDispatcher.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.dispatchKeyEvent(event);
      }
   }
}
