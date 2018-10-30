package com.pepedyne.pepe.controller;

import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

import com.pepedyne.pepe.dispatch.PepeDispatcher;

public class JoyConStickHandler {

   public static boolean handleJoystickInput(MotionEvent event, PepeDispatcher dispatcher) {
//      debugMotionEvent(event);
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
            JoyConStickHandler.processJoystickInput(event, i, dispatcher);
         }

         // Process the current movement sample in the batch (position -1)
         JoyConStickHandler.processJoystickInput(event, -1, dispatcher);
      }
      return true;
   }

   private static void processJoystickInput(MotionEvent event, int historyPos, PepeDispatcher dispatcher) {
      Log.i("\tPEPE DEBUG", "JoyCon");
      // Calculate the horizontal distance to move by
      // using the input value from one of these physical controls:
      // the left control stick, hat axis, or the right control stick.
      float leftX = getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_X, historyPos);
      float leftY = getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_Y, historyPos);
      float rightX = getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_Z, historyPos);
      float rightY = getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_RZ, historyPos);
      // Update the ship object based on the new x and y values
      Log.i("PEPE_DEBUG", "************************ (x,y): " + leftX + ", " + leftY +
              ", " + rightX + ", " + rightY);

      dispatcher.turnTo(leftY);
      dispatcher.sendIt();
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
}
