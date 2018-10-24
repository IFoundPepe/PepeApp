package com.pepedyne.pepe.controller;

import android.view.InputDevice;
import android.view.MotionEvent;

public class JoyConStickHandler {

   public static boolean handleJoystickInput(MotionEvent event) {
      // Process all historical movement samples in the batch
      final int historySize = event.getHistorySize();

      // Process the movements starting from the
      // earliest historical position in the batch
      for (int i = 0; i < historySize; i++) {
         // Process the event at historical position i
         JoyConStickHandler.processJoystickInput(event, i);
         for (int j = 0; j < event.getPointerCount(); j++) {
            System.out.println("Historical (x,y): " + event.getHistoricalX(j,i) + ", " +
            event.getHistoricalY(j,i));
         }
      }

      // Process the current movement sample in the batch (position -1)
      JoyConStickHandler.processJoystickInput(event, -1);
      return true;
   }

   public static void processJoystickInput(MotionEvent event,
                                     int historyPos) {

      // TODO: Investigate if this can be used in conjunction with the productID
      // TODO: to do the look/turn handling
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
      System.out.println("Print getRawX : " + event.getRawX());
      System.out.println("Print getAxisValue : " + event.getAxisValue(1));
      System.out.println("Print getOrientation : " + event.getOrientation());
      System.out.println("Print getTouchMajor : " + event.getTouchMajor());
      System.out.println("Print getTouchMajor : " + event.toString());
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
