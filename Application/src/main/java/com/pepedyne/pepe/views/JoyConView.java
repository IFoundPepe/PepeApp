package com.pepedyne.pepe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.pepedyne.pepe.controller.PepeControlActivity;
import com.pepedyne.pepe.dispatch.PepeDispatcher;

public class JoyConView extends View {
//public class JoyConView extends View implements InputManager.InputDeviceListener{
   // Input manager for Pepe control via controller
   //   private InputManagerCompat mInputManager;

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
//      mInputManager = InputManagerCompat.Factory.getInputManager(getContext());
//      mInputManager.registerInputDeviceListener(this, null);
   }

   private void debugDevice(InputDevice device) {
      Log.i("PEPE DEBUG", "Device Name: " + device.getName());
      Log.i("PEPE DEBUG", "ProductId: " + device.getProductId());
      Log.i("PEPE DEBUG", "Controller Number: " + device.getControllerNumber());
      Log.i("PEPE DEBUG", "Sources: " + device.getSources());
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

   private void processJoystickInput(MotionEvent event,
                                     int historyPos) {

      InputDevice mInputDevice = event.getDevice();

      // Calculate the horizontal distance to move by
      // using the input value from one of these physical controls:
      // the left control stick, hat axis, or the right control stick.
      float x = getCenteredAxis(event, mInputDevice,
              MotionEvent.AXIS_X, historyPos);
      if (x == 0) {
         x = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_HAT_X, historyPos);
      }
      if (x == 0) {
         x = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_Z, historyPos);
      }

      // Calculate the vertical distance to move by
      // using the input value from one of these physical controls:
      // the left control stick, hat switch, or the right control stick.
      float y = getCenteredAxis(event, mInputDevice,
              MotionEvent.AXIS_Y, historyPos);
      if (y == 0) {
         y = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_HAT_Y, historyPos);
      }
      if (y == 0) {
         y = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_RZ, historyPos);
      }

      // Update the ship object based on the new x and y values
      System.out.println("************************ (x,y): " + x + ", " + y );
   }

//   @Override
//   public boolean onGenericMotionEvent(MotionEvent event) {
//      Log.d("PEPE DEBUG", "Generic motion event occured!");
//      mInputManager.onGenericMotionEvent(event);
//
//      int eventSource = event.getSource();
//      int pointerIndex = event.getPointerCount() - 1; // since we only care about the last location of the motion
//      Log.d("PEPE EVENT", "to string: " + event.toString());
//      Log.d("PEPE EVENT", "actionToString: " + event.actionToString(event.getAction()));
//      int id = event.getDeviceId();
//      if (-1 != id)
//      {
//
//         int pointerCount = event.getPointerCount();
//         System.out.printf("At time %d:", event.getEventTime());
//         for (int p = 0; p < pointerCount; p++)
//         {
//            // Copy/pasta from sendUpdatedPositionData
//            Log.d("PEPE DEBUG", "Controller data");
//            pepeManager.joystickLook(event.getY(p)); // -0.88 to 1.0
//            pepeManager.silence();
//            if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == 1.0)
//            {
//               pepeManager.leanBack();
//               pepeManager.silence();
//            }
//
//            if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == -1.0)
//            {
//               pepeManager.leanForward();
//               pepeManager.silence();
//            }
//         }
//      }
//      return super.onGenericMotionEvent(event);
//   }

   @Override
   public boolean onGenericMotionEvent(MotionEvent event) {
      System.out.println("GENERIC MOTION EVENT");
      // Check that the event came from a game controller
      if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
              InputDevice.SOURCE_JOYSTICK &&
              event.getAction() == MotionEvent.ACTION_MOVE) {

         // Process all historical movement samples in the batch
         final int historySize = event.getHistorySize();

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

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
//   public boolean onKeyDown(int keyCode, KeyEvent event) {
      this.debugKeyEvent(event);

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
            pepeDispatcher.flapDown();
            pepeDispatcher.silence();
            return true;
         case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
            return true;
         default:
            return super.onKeyUp(keyCode, event);
      }
   }

//   @Override
//   public void onInputDeviceAdded(int deviceId) {
//
//   }
//
//   @Override
//   public void onInputDeviceRemoved(int deviceId) {
//
//   }
//
//   @Override
//   public void onInputDeviceChanged(int deviceId) {
//
//   }
}
