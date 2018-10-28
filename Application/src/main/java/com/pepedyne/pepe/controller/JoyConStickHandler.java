package com.pepedyne.pepe.controller;

import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

public class JoyConStickHandler {

   public static boolean handleJoystickInput(MotionEvent event) {
      debugMotionEvent(event);
      // Process all historical movement samples in the batch
      if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
              InputDevice.SOURCE_JOYSTICK &&
              event.getAction() == MotionEvent.ACTION_MOVE)
      {
         final int historySize = event.getHistorySize();

         // Process the movements starting from the
         // earliest historical position in the batch1
         for (int i = 0; i < historySize; i++)
         {
            // Process the event at historical position i
            JoyConStickHandler.processJoystickInput(event, i);
            for (int j = 0; j < event.getPointerCount(); j++)
            {
               // There are never historical events
               System.out.println("****** Processing Historical Event");
            }
         }

         float deltaX = event.getAxisValue(MotionEvent.AXIS_X);
         float deltaY = event.getAxisValue(MotionEvent.AXIS_Y);
         System.out.println("(x,y): " + deltaX + ", " + deltaY);
         // Process the current movement sample in the batch (position -1)
         JoyConStickHandler.processJoystickInput(event, -1);
      }
      return true;
   }

   private static void processJoystickInput(MotionEvent event,
                                            int historyPos) {

      // TODO: Investigate if this can be used in conjunction with the productID
      // TODO: to do the look/turn handling
      InputDevice mInputDevice = event.getDevice();

      // Calculate the horizontal distance to move by
      // using the input value from one of these physical controls:
      // the left control stick, hat axis, or the right control stick.
      float x = getCenteredAxis(event, mInputDevice,
              MotionEvent.AXIS_X, historyPos);
      if (x == 0)
      {
         x = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_HAT_X, historyPos);
      }
      if (x == 0)
      {
         x = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_Z, historyPos);
      }

      // Calculate the vertical distance to move by
      // using the input value from one of these physical controls:
      // the left control stick, hat switch, or the right control stick.
      float y = getCenteredAxis(event, mInputDevice,
              MotionEvent.AXIS_Y, historyPos);
      if (y == 0)
      {
         y = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_HAT_Y, historyPos);
      }
      if (y == 0)
      {
         y = getCenteredAxis(event, mInputDevice,
                 MotionEvent.AXIS_RZ, historyPos);
      }

      // Update the ship object based on the new x and y values
      Log.i("PEPE_DEBUG", "************************ (x,y): " + x + ", " + y);
   }

   private static float getCenteredAxis(MotionEvent event,
                                        InputDevice device, int axis, int historyPos) {
      final InputDevice.MotionRange range =
              device.getMotionRange(axis, event.getSource());

      // A joystick at rest does not always report an absolute position of
      // (0,0). Use the getFlat() method to determine the range of values
      // bounding the joystick axis center.
      if (range != null)
      {
         final float flat = range.getFlat();
         final float value =
                 historyPos < 0 ? event.getAxisValue(axis) :
                         event.getHistoricalAxisValue(axis, historyPos);

         // Ignore axis values that are within the 'flat' region of the
         // joystick axis center.
         if (Math.abs(value) > flat)
         {
            return value;
         }
      }
      return 0;
   }

   private static void debugMotionEvent(MotionEvent event) {
      Log.d("\tPEPE_DEBUG", "--------------------------");
      Log.d("\tPEPE_DEBUG", " MotionEvent Device: " + event.getDevice());
      Log.d("\tPEPE_DEBUG", " MotionEvent DeviceId: " + event.getDeviceId());
      Log.d("\tPEPE_DEBUG", " MotionEvent Id: " + event.getDevice().getId());
      Log.d("\tPEPE_DEBUG", " MotionEvent getFlags: " + event.getFlags());
      Log.d("\tPEPE_DEBUG", " MotionEvent getDownTime: " + event.getDownTime());
      Log.d("\tPEPE_DEBUG", " MotionEvent getHistorySize: " + event.getHistorySize());
      Log.d("\tPEPE_DEBUG", " MotionEvent getPointerCount: " + event.getPointerCount());
//      Log.d ("\tPEPE DEBUG MotionEvent getPointerCount: " + event.getAxisValue(MotionEvent.AXIS_X, Motion));
      Log.d("\tPEPE_DEBUG", " MotionEvent MetaState: " + event.getMetaState());
//      Log.d ("\tPEPE DEBUG KeyEvent getAction: " + event.getAction());
   }
}
