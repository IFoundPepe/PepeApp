package com.pepedyne.pepe.views;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.pepedyne.pepe.controller.JoyConButtonHandler;
import com.pepedyne.pepe.controller.JoyConStickHandler;
import com.pepedyne.pepe.controller.PepeControlActivity;
import com.pepedyne.pepe.dispatch.PepeDispatcher;

import java.util.ArrayList;
import java.util.List;

public class JoyConView extends View implements InputManager.InputDeviceListener {
//public class JoyConView extends View implements InputManager.InputDeviceListener{
   // Input manager for Pepe control via controller
   //   private InputManagerCompat mInputManager;

   static int count = 0;
   private static final int PRODUCT_ID_LEFT_JOYCON = 8198;
   private static final int PRODUCT_ID_RIGHT_JOYCON = 8199;
   private PepeDispatcher pepeDispatcher;
   public JoyConView(Context context) {
      super(context);
      init();
   }

   public JoyConView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public JoyConView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   public JoyConView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init();
   }

   public void init() {
      setFocusable(true);
//      setFocusableInTouchMode(true);
      final PepeControlActivity host = (PepeControlActivity) this.getContext();
      pepeDispatcher = host.getDispatcher();
      List<Integer> list = getGameControllerIds();
      for ( int i = 0; i < list.size(); i++ ) {
         Log.i("\tPEPE_DEBGU", "Controller: " + list.get(i));
      }
//      mInputManager = InputManagerCompat.Factory.getInputManager(getContext());
//      mInputManager.registerInputDeviceListener(this, null);
      this.requestFocus();
      this.requestFocusFromTouch();

   }

   @Override
   public boolean dispatchGenericMotionEvent(MotionEvent event) {
      if(onGenericMotionEvent(event))
      {
         return true;
      }
      return super.dispatchGenericMotionEvent(event);
   }

   @Override
   public boolean onGenericMotionEvent(MotionEvent event) {
      // Check that the event came from a game controller
      if (JoyConStickHandler.handleJoystickInput(event))
      {
         return true;
      }
      return super.onGenericMotionEvent(event);
   }

//   @Override
//   public boolean onKeyUp(int i, KeyEvent event){
//      JoyConButtonHandler.debugKeyEvent(event);
//      return JoyConButtonHandler.executeRightJoyConKeyEvent(event, pepeDispatcher, this);
//   }
//
//   @Override
//   public boolean onKeyDown(int i, KeyEvent event){
//      JoyConButtonHandler.debugKeyEvent(event);
//      return JoyConButtonHandler.executeRightJoyConKeyEvent(event, pepeDispatcher, this);
//   }

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
//   public boolean onKeyDown(int keyCode, KeyEvent event) {
      Log.d("PEPE_DEBUG", "-------------------------------------" + count);
      count++;
      if (event.getRepeatCount() < 1)
      {
         JoyConButtonHandler.debugKeyEvent(event);
         // Determine which JoyCon was the source of the event
         if (event.getDevice().getProductId() == PRODUCT_ID_LEFT_JOYCON) // Left JoyCon event
         {
            return JoyConButtonHandler.executeLeftJoyConKeyEvent(event, pepeDispatcher, this);
         }
         else if (event.getDevice().getProductId() == PRODUCT_ID_RIGHT_JOYCON) // Right JoyCon event
         {
            return JoyConButtonHandler.executeRightJoyConKeyEvent(event, pepeDispatcher, this);
         }
      }
      return super.dispatchKeyEvent(event);
   }


   @Override
   public void onInputDeviceAdded(int deviceId) {
      System.out.println("Input Added");

   }

   @Override
   public void onInputDeviceRemoved(int deviceId) {
      System.out.println("Input Removed");

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
            pepeDispatcher.flapLeftUp();
            pepeDispatcher.flapRightUp();
            pepeDispatcher.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.dispatchKeyEvent(event);
      }
   }

   @Override
   public boolean onKeyUp(int keyCode, KeyEvent event) {
      Log.d("PEPE DEBUG", "keyUp keyCode: " + keyCode);
      // 99/67(silence), 96/23(tweet), 100/62(flap)
      switch (keyCode)
      {
         case KeyEvent.KEYCODE_BUTTON_A: // press X: 96
            Log.d("PEPE DEBUG", "tweet");
            pepeDispatcher.tweet();
            return true;
         case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99
//            if (PepAIState)
//            {
//               Log.d("PEPE DEBUG", "PepAI stopped");
//               PepAIState = false;
//            }
//            else
//            {
//               Log.d("PEPE DEBUG", "PepAI started");
//               PepAIState = true;
//            }
            return true;
         case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
            return true;
         case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
            Log.d("PEPE DEBUG", "flap down");
            pepeDispatcher.flapLeftDown();
            pepeDispatcher.flapRightDown();
            pepeDispatcher.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.onKeyUp(keyCode, event);
      }
   }

   public ArrayList<Integer> getGameControllerIds() {
      ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
      int[] deviceIds = InputDevice.getDeviceIds();
      for (int deviceId : deviceIds) {
         InputDevice dev = InputDevice.getDevice(deviceId);
         int sources = dev.getSources();

         // Verify that the device has gamepad buttons, control sticks, or both.
         if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                 || ((sources & InputDevice.SOURCE_JOYSTICK)
                 == InputDevice.SOURCE_JOYSTICK)) {
            // This device is a game controller. Store its device ID.
            if (!gameControllerDeviceIds.contains(deviceId)) {
               gameControllerDeviceIds.add(deviceId);
            }
         }
      }
      return gameControllerDeviceIds;
   }
}
