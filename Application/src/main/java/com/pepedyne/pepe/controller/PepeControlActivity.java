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
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothCallbackInf;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProvider;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProviderImpl;
import com.pepedyne.pepe.dispatch.PepeDispatcher;
import com.pepedyne.pepe.dispatch.SendDataHandler;

import java.util.HashMap;

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
   private HashMap<String, Integer> keyMapperLeftJoyCon;
   private HashMap<String, Integer> keyMapperRightJoyCon;

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
      this.initKeyMapper();
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


   private void debugKeyEvent(KeyEvent event) {
      Log.i("\tPEPE DEBUG","--------------------------");
      Log.d("PEPE DEBUG", "keyDown keyCode: " + event.getKeyCode());
      Log.i("\tPEPE DEBUG", "KeyEvent DeviceId: " + event.getDeviceId());
      Log.i("\tPEPE DEBUG", "KeyEvent Id: " + event.getDevice().getId());
      Log.i("\tPEPE DEBUG", "KeyEvent getFlags: " + event.getFlags());
      Log.i("\tPEPE DEBUG", "KeyEvent getDownTime: " + event.getDownTime());

      Log.i("\tPEPE DEBUG", "KeyEvent getCharacters: " + event.getCharacters());
      Log.i("\tPEPE DEBUG", "KeyEvent MetaState: " + event.getMetaState());
      Log.i("\tPEPE DEBUG", "KeyEvent getModifiers: " + event.getModifiers());
      Log.i("\tPEPE DEBUG", "KeyEvent getRepeatCount: " + event.getRepeatCount());

      Log.i("\tPEPE DEBUG", "KeyEvent getAction: " + event.getAction());
   }
   private void debugMotionEvent(MotionEvent event) {
      Log.i("\tPEPE DEBUG","--------------------------");
      Log.d("PEPE DEBUG", "MotionEvent Device: " + event.getDevice());
      Log.i("\tPEPE DEBUG", "MotionEvent DeviceId: " + event.getDeviceId());
      Log.i("\tPEPE DEBUG", "MotionEvent Id: " + event.getDevice().getId());
      Log.i("\tPEPE DEBUG", "MotionEvent getFlags: " + event.getFlags());
      Log.i("\tPEPE DEBUG", "MotionEvent getDownTime: " + event.getDownTime());

      Log.i("\tPEPE DEBUG", "MotionEvent getHistorySize: " + event.getHistorySize());
      Log.i("\tPEPE DEBUG", "MotionEvent getPointerCount: " + event.getPointerCount());
//      Log.i("\tPEPE DEBUG", "MotionEvent getPointerCount: " + event.getAxisValue(MotionEvent.AXIS_X, Motion));
      Log.i("\tPEPE DEBUG", "MotionEvent MetaState: " + event.getMetaState());
//
//      Log.i("\tPEPE DEBUG", "KeyEvent getAction: " + event.getAction());
   }

   private void initKeyMapper() {
      // ====Left JoyCon====
      keyMapperLeftJoyCon = new HashMap<>();
      // up arrow, Left JoyCon
      keyMapperLeftJoyCon.put("upArrowDown", 98); // 98
      keyMapperLeftJoyCon.put("upArrowUp", 23); // 23
      // down arrow, Left JoyCon
      keyMapperLeftJoyCon.put("downArrowDown", 97); // 97
      keyMapperLeftJoyCon.put("downArrowUp", 4); // 4
      // left arrow, Left JoyCon
      keyMapperLeftJoyCon.put("leftArrow", 96); // 96 (Up and Down)
      // right arrow, Left JoyCon
      keyMapperLeftJoyCon.put("rightArrow", 99); // 99 (Up and Down)
      // L button, Left JoyCon
      keyMapperLeftJoyCon.put("LbuttonDown", 107); // 107
      keyMapperLeftJoyCon.put("LbuttonUp", 23); // 23
      // ZL button, Left JoyCon
      keyMapperLeftJoyCon.put("ZLbutton", 1050); // 1050 (Up and Down)
      // minus button, Left JoyCon
      keyMapperLeftJoyCon.put("minusButton", 104); // 104 (Up and Down)
      // SL button, Right JoyCon
      keyMapperLeftJoyCon.put("SLbutton", 100); // 100 - Same as SL
      // SR button, Right JoyCon
      keyMapperLeftJoyCon.put("SRbutton", 101); // 101 - Same as SR

      // ====Right JoyCon====
      keyMapperRightJoyCon = new HashMap<>();
      // X Button, Right JoyCon
      keyMapperRightJoyCon.put("XbuttonDown", 97); // 97 - Same as down arrow press
      keyMapperRightJoyCon.put("XbuttonUp", 4); // 4 - Same as down arrow release
      // B Button, Right JoyCon
      keyMapperRightJoyCon.put("BbuttonDown", 98); // 98 - Same as R button press
      keyMapperRightJoyCon.put("BbuttonUp", 23); // 23 - Same as R button release
      // Y Button, Right JoyCon
      keyMapperRightJoyCon.put("Ybutton", 99); // 99 (Up and Down) - Same as right arrow
      // A Button, Right JoyCon
      keyMapperRightJoyCon.put("Abutton", 96); // 96 (Up and Down) - Same as left arrow
      // R Button, Right JoyCon
      keyMapperRightJoyCon.put("RbuttonDown", 107); // 107 - Same as B button press
      keyMapperRightJoyCon.put("RbuttonUp", 23); // 23 - Same as B button release
      // ZR Button, Right JoyCon
      keyMapperRightJoyCon.put("ZRbutton", 1050); // 1050 (Up and Down) - Same as ZL
      // plus button, Right JoyCon
      keyMapperRightJoyCon.put("plusButton", 105); // 105 (Up and Down)
      // SR button, Right JoyCon
      keyMapperRightJoyCon.put("SRbutton", 101); // 101 - Same as SR
      // SL button, Right JoyCon
      keyMapperRightJoyCon.put("SLbutton", 100); // 100 - Same as SL
   }

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
//   public boolean onKeyDown(int keyCode, KeyEvent event) {
//      this.debugKeyEvent(event);

      if (event.getRepeatCount() < 1)
      {
         // Determin which JoyCon was the source of the event
         if(event.getDevice().getProductId() == 8198) // Left JoyCon event
         {
//             Log.i("\tPEPE DEBUG", "JoyCon: LEFT");
            return executeLeftJoyConKeyEvent(event);
         }
         else if(event.getDevice().getProductId() == 8199) // Right JoyCon event
         {
//             Log.i("\tPEPE DEBUG", "JoyCon: RIGHT");
            return executeRightJoyConKeyEvent(event);
         }
      }
      return super.dispatchKeyEvent(event);
   }

   @Override
   public boolean dispatchGenericMotionEvent(MotionEvent event) {
//   public boolean onKeyDown(int keyCode, KeyEvent event) {
//      this.debugMotionEvent(event);
      if(onGenericMotionEvent(event))
      {
//          Log.i("\tPEPE DEBUG", "JoyCon: WE DID IT!!!");
          return true;
      }

      return super.dispatchGenericMotionEvent(event);
   }

   private boolean executeLeftJoyConKeyEvent(KeyEvent event) {
//======LEFT JOYCON KEY CODES======
//      upArrowDown
//      upArrowUp
//      downArrowDown
//      downArrowUp
//      leftArrow
//      rightArrow
//      LbuttonDown
//      LbuttonUp
//      ZLbutton
//      minusButton
//      SLbutton
//      SRbutton

      int keyCode = event.getKeyCode();
      if (keyCode == keyMapperLeftJoyCon.get("upArrowDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: upArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: upArrowDown - LEFT - Action Up");
            // soaring
            pepeDispatcher.soaring();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("downArrowDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: downArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: downArrowDown - LEFT - Action Up");
            // peacock
            pepeDispatcher.peacock();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("leftArrow"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: leftArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: leftArrowDown - LEFT - Action Up");
            // turn and wink
            pepeDispatcher.turnAndWink();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("rightArrow"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: rightArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: rightArrowDown - LEFT - Action Up");
            // look and focus
            pepeDispatcher.lookAndFocus();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("LbuttonDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: LbuttonDown - LEFT - Action Down");
            // TODO: We will not be able to "hold up" a flap with this design
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
//            // TODO: This may cause repetitive flaps. Do we invert logic?
            // flap Left wing
            pepeDispatcher.flapLeftOnce();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("ZLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZLbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZLbutton - LEFT - Action Up");
            // wink left eye
            pepeDispatcher.winkLeftOnce();
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("minusButton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: minusButton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: minusButton - LEFT - Action Up");
// TODO: Enable this after debugging
//            // TODO: Do we want to use + to start AI and - to disable AI?
//            // TODO: Maybe check state of pepeAI with getAIstate()
            pepeDispatcher.connectTweet(); // This is audible response that PepeAI is off
            pepeDispatcher.setAIState(false);
            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("SLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("SRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - LEFT - Action Up");
            // Do Nothing
         }
      }
      return true;
      //      return super.dispatchKeyEvent(event);
   }

   private boolean executeRightJoyConKeyEvent(KeyEvent event) {
//======RIGHT JOYCON KEY CODES======
//      XbuttonDown
//      XbuttonUp
//      BbuttonDown
//      BbuttonUp
//      Ybutton
//      Abutton
//      RbuttonDown
//      RbuttonUp
//      ZRbutton
//      plusButton
//      SRbutton
//      SLbutton

      int keyCode = event.getKeyCode();
      if (keyCode == keyMapperRightJoyCon.get("XbuttonDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: XbuttonDown - RIGHT - Action Down");
// TODO: Enable this after debugging
            // TODO: This may cause repetitive tweets. Do we invert logic?
//            pepeDispatcher.tweet();
//            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: XbuttonDown - RIGHT - Action Up");
// TODO: Enable this after debugging
//            pepeDispatcher.silence();
//            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("BbuttonDown"))
      {
         // TODO: What will this do?
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: BbuttonDown - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: BbuttonDown - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("Ybutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Ybutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Ybutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("Abutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Abutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Abutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("RbuttonDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: RbuttonDown - RIGHT - Action Down");
            // TODO: We will not be able to "hold up" a flap with this design
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: RbuttonDown - RIGHT - Action Up");
            // flap Right wing
            pepeDispatcher.flapRightOnce();
            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("ZRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - RIGHT - Action Up");
            pepeDispatcher.winkRightOnce();
            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("plusButton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: plusButton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: plusButton - RIGHT - Action Up");
            pepeDispatcher.connectTweet(); // This is audible response that PepeAI is on
            pepeDispatcher.runPepeAI();
            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("SRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SRbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SRbutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("SLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else
      {
         // TODO: Undefined keycode!!!!
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - RIGHT - Action Up");
            // Do Nothing
         }
      }
      return true;
//      return super.dispatchKeyEvent(event);
   }

   @Override
   public boolean onGenericMotionEvent(MotionEvent event) {
      // Check that the event came from a game controller
      if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
              InputDevice.SOURCE_JOYSTICK &&
              event.getAction() == MotionEvent.ACTION_MOVE) {

         // Process all historical movement samples in the batch
         final int historySize = event.getHistorySize();
         // If a Joystick
         Log.i("\tPEPE DEBUG", "JoyCon: InputDevice: " + event.getSource());

         // Determin which JoyCon was the source of the event
        Log.i("\tPEPE DEBUG", "JoyCon: historical size - " + historySize);

         // Process the movements starting from the
         // earliest historical position in the batch
         for (int i = 0; i < historySize; i++) {
            // Process the event at historical position i
            processJoystickInput(event, i);
         }

         // Process the current movement sample in the batch (position -1)
         processJoystickInput(event, -1);
         return true;
      }
      return super.onGenericMotionEvent(event);
   }

   private void processJoystickInput(MotionEvent event,
                                     int historyPos) {
//      Log.i("\tPEPE DEBUG", "JoyCon: processJoystickInput");
      InputDevice mInputDevice = event.getDevice();
// TODO: Map the motion to pepe actions
      if(event.getDevice().getProductId() == 8198) // Left JoyCon event
      {
         Log.i("\tPEPE DEBUG", "JoyCon: LEFT");
// NOTE: This is wrong...straight from Google...This is the vertical motion for JoyCon
         // Calculate the horizontal distance to move by
         // using the input value from one of these physical controls:
         // the left control stick, hat axis, or the right control stick.
         float x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
         if(x == -1){
            // Up
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): up");
            // Do Nothing??
         }else if(x == 0){
            // Center
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): center");
            // Do Nothing??
         }else if(x == 1) {
            // Down
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): down");
            // Do Nothing??
         }
// NOTE: This is wrong...straight from Google...This is the Horizontal motion for JoyCon
         // Calculate the vertical distance to move by
         // using the input value from one of these physical controls:
         // the left control stick, hat switch, or the right control stick.
         float y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
         if(y == -1){
            // Right
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): right");
            pepeDispatcher.turnLeft();
            pepeDispatcher.sendIt();
         }else if(y == 0){
            // Center
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): center");
            pepeDispatcher.resetTurn();
            pepeDispatcher.sendIt();
         }else if(y == 1) {
            // Left
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): left");
            pepeDispatcher.turnRight();
            pepeDispatcher.sendIt();
         }
      }
      else if(event.getDevice().getProductId() == 8199) // Right JoyCon event
      {
         Log.i("\tPEPE DEBUG", "JoyCon: RIGHT");
// NOTE: This is wrong...straight from Google...This is the vertical motion for JoyCon
         // Calculate the horizontal distance to move by
         // using the input value from one of these physical controls:
         // the left control stick, hat axis, or the right control stick.
         float x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
         if(x == -1){
            // Down
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): down");
            // Do Nothing??
         }else if(x == 0){
            // Center
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): center");
            // Do Nothing??
         }else if(x == 1) {
            // Up
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Vertical): up");
            // Do Nothing??
         }
// NOTE: This is wrong...straight from Google...This is the Horizontal motion for JoyCon
         // Calculate the vertical distance to move by
         // using the input value from one of these physical controls:
         // the left control stick, hat switch, or the right control stick.
         float y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
         if(y == -1){
            // left
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): left");
            pepeDispatcher.lookLeft();
            pepeDispatcher.sendIt();
         }else if(y == 0){
            // Center
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): center");
            pepeDispatcher.resetLook();
            pepeDispatcher.sendIt();
         }else if(y == 1) {
            // Right
            Log.i("\tPEPE DEBUG", "JoyCon: joystick(Horizontal): right");
            pepeDispatcher.lookRight();
            pepeDispatcher.sendIt();
         }
      }

      // TODO: Check other AXIS values below? I have spent A LOT of time doing this with not much luck
      // Begin google code block commented out
//      float x = getCenteredAxis(event, mInputDevice,
//              MotionEvent.AXIS_X, historyPos);
//      if (x == 0) {
//         x = getCenteredAxis(event, mInputDevice,
//                 MotionEvent.AXIS_HAT_X, historyPos);
//      }
//      if (x == 0) {
//         x = getCenteredAxis(event, mInputDevice,
//                 MotionEvent.AXIS_Z, historyPos);
//      }
//
//      // Calculate the vertical distance to move by
//      // using the input value from one of these physical controls:
//      // the left control stick, hat switch, or the right control stick.
//      float y = getCenteredAxis(event, mInputDevice,
//              MotionEvent.AXIS_Y, historyPos);
//      if (y == 0) {
//         y = getCenteredAxis(event, mInputDevice,
//                 MotionEvent.AXIS_HAT_Y, historyPos);
//      }
//      if (y == 0) {
//         y = getCenteredAxis(event, mInputDevice,
//                 MotionEvent.AXIS_RZ, historyPos);
//      }
//
//      // Update the ship object based on the new x and y values

   }

   private static float getCenteredAxis(MotionEvent event,
                                        InputDevice device, int axis, int historyPos) {
      final InputDevice.MotionRange range =
              device.getMotionRange(axis, event.getSource());

      // A joystick at rest does not always report an absolute position of
      // (0,0). Use the getFlat() method to determine the range of values
      // bounding the joystick axis center.
      if (range != null) {
         final float flat = range.getFlat();
         final float value =
                 historyPos < 0 ? event.getAxisValue(axis):
                         event.getHistoricalAxisValue(axis, historyPos);

         // Ignore axis values that are within the 'flat' region of the
         // joystick axis center.
         if (Math.abs(value) > flat) {
            return value;
         }
      }
      return 0;
   }

}
