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
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothCallbackInf;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProvider;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProviderImpl;
import com.pepedyne.pepe.dispatch.PepeDispatcher;
import com.pepedyne.pepe.dispatch.SendDataHandler;

import java.util.ArrayList;
import java.util.List;

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
   private static final int PRODUCT_ID_LEFT_JOYCON = 8198;
   private static final int PRODUCT_ID_RIGHT_JOYCON = 8199;

   private TextView mDataField;
   private String mDeviceAddress;
   private BluetoothLeServiceProvider bluetoothLeServiceProvider;
   private PepeDispatcher pepeDispatcher;
   private Handler handler;
   private HandlerThread handlerThread;
   private View myView;

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
//      pepeMacros = new PepeMacros(pepeDispatcher);

      getSupportActionBar().setTitle(mDeviceName);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      findViewById(R.id.joystick);
      findViewById(R.id.flap);
      findViewById(R.id.tweet);
      findViewById(R.id.PepAI);
      findViewById(R.id.settingsButton);
      myView = findViewById(R.id.joycon);
//      setContentView(myView);

//      List<Integer> list = getGameControllerIds();
//      for ( int i = 0; i < list.size(); i++ ) {
//         Log.i("\tPEPE_DEBGU", "Controller: " + list.get(i));
//      }

      handlerThread = new HandlerThread("MyHandlerThread");
      handlerThread.start();
      Looper looper = handlerThread.getLooper();
      handler = new Handler(looper) {
         @Override
         public void handleMessage(Message msg) {
            // process incoming messages here
            // this will run in the thread, which instantiates it
            Log.d("PEPE DEBUG", "Data: " + msg.obj);

            bluetoothLeServiceProvider.send((byte[]) msg.obj);
         }
      };

      myView.requestFocus();
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
        byte[] packedData = pepeDispatcher.generateData();
         Message msg = new Message();
         msg.obj = packedData;
         handler.sendMessage(msg);
      }
      return false;
   }

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
      if (JoyConButtonHandler.executeJoyConButton(event, pepeDispatcher, myView))
      {
         return true;
      }
      return super.dispatchKeyEvent(event);
   }

   @Override
   public boolean dispatchGenericMotionEvent(MotionEvent event) {
      if (JoyConStickHandler.handleJoystickInput(event, pepeDispatcher))
      {
         return true;
      }
      return super.dispatchGenericMotionEvent(event);
   }

}
