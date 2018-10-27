package com.pepedyne.pepe.dispatch;

import android.os.Handler;
import android.util.Log;

import com.pepedyne.pepe.controller.PepeBluetoothConnectionManager;

public class PepeDispatcher {

   private PepeBluetoothConnectionManager pepeManager;
   private SendDataHandler handler;
   private boolean pepeAI;
   private int mInterval = 10; // 0.5 seconds by default, can be changed later
   private Handler mHandler;
   final int PepAIIntervalVariance = 300;  //number of wake up intervals
   private int PepAIActionCounter = 0;
   // Roll 1 to 5, TODO: Increase the number of possibilities
   final int roll_variance = 100;
   // TODO: Is this value still adequate for M.I.K.E.P.?
   final int blink_wait_milli = 350;
   final int flap_wait_milli = 350;
   final int tail_wait_milli = 350;
   final int turn_wait_milli = 500;
   final int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation
   public PepeDispatcher(PepeBluetoothConnectionManager manager, SendDataHandler handler) {

      this.pepeManager = manager;
      this.handler = handler;
      mHandler = new Handler();
      pepeAI = false;
   }

   Runnable mStatusChecker = new Runnable() {
      @Override
      public void run() {
         try
         {
            handler.sendData(); //this function can change value of mInterval.
            PepAIActionCounter++;
            if (pepeAI && (PepAIActionCounter >= PepAIIntervalVariance))
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

   public void reset() {
      pepeManager.reset();
   }
   public void stop() {
      this.stopRepeatingTask();
   }

   void startRepeatingTask() {
      mStatusChecker.run();
   }

   void stopRepeatingTask() {
      mHandler.removeCallbacks(mStatusChecker);
   }

   public void tweet() {
      pepeManager.tweet();
   }

   public void tweetRand() {
      pepeManager.tweetRand();
   }

   public void silence() {
      pepeManager.silence();
   }

   public void turnLeft() {
      pepeManager.turnLeft();
   }

   public void turnRight() {
      pepeManager.turnRight();
   }

   public void resetTurn() {
      pepeManager.resetTurn();
   }

   public void lookLeft() {
      pepeManager.lookLeft();
   }

   public void lookRight() {
      pepeManager.lookRight();
   }

   public void resetLook() {
      pepeManager.resetLook();
   }

   public void flapLeftUp() {
      pepeManager.flapLeftUp();
   }

   public void flapLeftDown() {
      pepeManager.flapLeftDown();
   }

   public void flapRightUp() {
      pepeManager.flapRightUp();
   }

   public void flapRightDown() {
      pepeManager.flapRightDown();
   }

   public void blinkLeftUp() {
      pepeManager.blinkLeftUp();
   }

   public void blinkLeftDown() {
      pepeManager.blinkLeftDown();
   }

   public void blinkRightUp() {
      pepeManager.blinkRightUp();
   }

   public void blinkRightDown() {
      pepeManager.blinkRightDown();
   }

   public void resetBlinkLeft() {
      pepeManager.resetBlinkLeft();
   }

   public void resetBlinkRight() {
      pepeManager.resetBlinkRight();
   }

   public void tailUp() {
      pepeManager.tailUp();
   }

   public void tailDown() {
      pepeManager.tailDown();
   }

   public void setLook(int look) {
      pepeManager.setLook(look);
   }

   public void setTurn(int turn) {
      pepeManager.setTurn(turn);
   }

   public void setTail(int tail) {
      pepeManager.setTail(tail);
   }

   public void setFlapLeft(int flap) {
      pepeManager.setFlapLeft(flap);
   }
   public void setFlapRight(int flap) {
      pepeManager.setFlapRight(flap);
   }
   public void setBlinkLeft(int blink) {
      pepeManager.setBlinkLeft(blink);
   }
   public void setBlinkRight(int blink) {
      pepeManager.setBlinkRight(blink);
   }

   public void connectTweet() {
      pepeManager.silence();
   }

   public void calculate() {
      pepeManager.calculateLookAndTurn();
   }

   public String generateData() {
      return pepeManager.generateData();
   }

   public boolean sendIt() {
      return pepeManager.sendIt();
   }

   public void setMove(double str, double ang) {
      pepeManager.setStrength_value(str);
      pepeManager.setAngle_value(ang);
   }

   public void toggleAI() {
      pepeAI = !pepeAI;
      if (pepeAI) {
         this.startRepeatingTask();
      }
      else
      {
         this.stopRepeatingTask();
      }
   }

   public boolean getAIState() {
      return pepeAI;
   }

   public void setAIState(boolean state) {
      pepeAI = state;
   }

   public void runPepeAI() {
      int roll = (int) Math.ceil(Math.random() * roll_variance);
      Log.d("PEPE DEBUG", "roll == " + roll);

      // Tweet always
      Log.d("PEPE DEBUG", "always tweet");
      pepeManager.tweetRand();

      if (isBetween(roll, 1, 10)) {
         Log.d("PEPE DEBUG", "look left");
//         pepeManager.setLook(235); // Look middle left
         lookLeft();
         sendIt();
      } else if (isBetween(roll, 11, 20)) {
         Log.d("PEPE DEBUG", "look right");
//         pepeManager.setLook(445); // Look middle right
         lookRight();
         sendIt();
      } else if (isBetween(roll, 21, 35)) {
         Log.d("PEPE DEBUG", "flap twice");
         flapTwice();
//         sendIt(); // Macro function does not require sendIt()
      } else if (isBetween(roll, 36,55)) {
         Log.d("PEPE DEBUG", "flap once");
         flapOnce();
//         sendIt(); // Macro function does not require sendIt()
      }  else if (isBetween(roll, 56,60)) {
         Log.d("PEPE DEBUG", "SOARING!");
// TODO: THIS IS MADNESS!!! DON'T TRY THIS UNTIL YOU ARE SURE PEPE CAN HANDLE IT!!!!
//         soaring();
//          sendIt(); // Macro function does not require sendIt()
      } else if (isBetween(roll, 61,65)) {
         Log.d("PEPE DEBUG", "lookAndFocus");
         lookAndFocus();
//          sendIt(); // Macro function does not require sendIt()
      } else{
         // TODO: Do i reset all here?
         // pepeManager.resetLook();
         // handler.sendData();
      }
   }

   public void flapTwice() {
      pepeManager.blinkLeftDown();// Opposite with eyes like hes squinting
      pepeManager.blinkRightDown();
      pepeManager.flapLeftUp();
      pepeManager.flapRightUp();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli);
      pepeManager.resetBlinkLeft();
      pepeManager.resetBlinkRight();
      pepeManager.flapLeftDown();
      pepeManager.flapRightDown();
      pepeManager.silence();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli + (flap_wait_milli / 2));
      pepeManager.blinkLeftDown();// Opposite with eyes like hes squinting
      pepeManager.blinkRightDown();
      pepeManager.flapLeftUp();
      pepeManager.flapRightUp();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli);
      pepeManager.resetBlinkLeft();
      pepeManager.resetBlinkRight();
      pepeManager.flapLeftDown();
      pepeManager.flapRightDown();
      pepeManager.silence();
      handler.sendData();
   }

   public void flapOnce() {
      pepeManager.blinkLeftDown();// Opposite with eyes like hes squinting
      pepeManager.blinkRightDown();
      pepeManager.flapLeftUp();
      pepeManager.flapRightUp();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli);
      pepeManager.resetBlinkLeft();
      pepeManager.resetBlinkRight();
      pepeManager.flapLeftDown();
      pepeManager.flapRightDown();
      pepeManager.silence();
      handler.sendData();
   }

   public void flapLeftOnce() {
      pepeManager.flapLeftUp();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli);
      pepeManager.flapLeftDown();
      pepeManager.silence();
      handler.sendData();
   }

   public void flapRightOnce() {
      pepeManager.flapRightUp();
      handler.sendData();
      android.os.SystemClock.sleep(flap_wait_milli);
      pepeManager.flapRightDown();
      pepeManager.silence();
      handler.sendData();
   }

   public void winkLeftOnce() {
      pepeManager.blinkLeftDown();
      handler.sendData();
      android.os.SystemClock.sleep(blink_wait_milli);
      pepeManager.resetBlinkLeft();
      pepeManager.silence();
      handler.sendData();
   }

   public void winkRightOnce() {
      pepeManager.blinkRightDown();
      handler.sendData();
      android.os.SystemClock.sleep(blink_wait_milli);
      pepeManager.resetBlinkRight();
      pepeManager.silence();
      handler.sendData();
   }

   public void soaring() {
      //         pepeManager.flapLeftUp(); // Start with just left wing up
//         handler.sendData();
//         android.os.SystemClock.sleep(flap_wait_milli);
//         pepeManager.turnRight();
//         handler.sendData();
//         android.os.SystemClock.sleep(turn_wait_milli);// Finished 1st turn right
//         pepeManager.flapLeftDown();
//         pepeManager.flapRightUp();
//         handler.sendData();
//         android.os.SystemClock.sleep(flap_wait_milli);
//         pepeManager.turnLeft();
//         handler.sendData();
//         android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 1st turn left
//         pepeManager.flapRightDown();
//         pepeManager.flapLeftUp();
//         handler.sendData();
//         android.os.SystemClock.sleep(flap_wait_milli);
//         pepeManager.turnRight();
//         handler.sendData();
//         android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 2nd turn right
//         pepeManager.flapLeftDown();
//         pepeManager.flapRightUp();
//         handler.sendData();
//         android.os.SystemClock.sleep(flap_wait_milli);
//         pepeManager.turnLeft();
//         handler.sendData();
//         android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 2nd turn leftt
//         pepeManager.flapLeftDown();
//         pepeManager.flapRightUp();
//         handler.sendData();
//         android.os.SystemClock.sleep(flap_wait_milli);
//         pepeManager.turnLeft();
//         handler.sendData();
//         android.os.SystemClock.sleep(soaring_turn_wait_milli);
//         // TODO: set to center position with constant
//         pepeManager.setLook(340); // Recenter
//         handler.sendData();
//         pepeManager.flapRightDown(); // Put right wing back down
//         handler.sendData();
//         pepeManager.silence();
//         handler.sendData();
   }

   public void lookAndFocus() {
      int roll = (int) Math.ceil(Math.random() * 3);
      if (isBetween(roll, 1, 1)) {
         Log.d("PEPE DEBUG", "look n focus left");
         // Look left
         lookLeft();
         focus();
         // sendIt(); // Focus is a macro and already sends and unfocuses
         android.os.SystemClock.sleep(blink_wait_milli);
         resetLook();
         pepeManager.silence();
         sendIt();
      } else if (isBetween(roll, 2, 2)) {
         Log.d("PEPE DEBUG", "look n focus right");
         // Look right
         lookRight();
         focus();
         // sendIt(); // Focus is a macro and already sends and unfocuses
         android.os.SystemClock.sleep(blink_wait_milli);
         resetLook();
         pepeManager.silence();
         sendIt();
      }else {
         Log.d("PEPE DEBUG", "look n focus middle");
         // No look
         focus();
      }
   }

   public void focus() {
      Log.d("PEPE DEBUG", "focus");
      blinkLeftDown();
      blinkRightDown();
      sendIt();
      android.os.SystemClock.sleep(blink_wait_milli);
      resetBlinkLeft();
      resetBlinkRight();
      pepeManager.silence();
      sendIt();
   }

   public void turnAndWink() {
      int roll = (int) Math.ceil(Math.random() * 3);
      if (isBetween(roll, 1, 1)) {
         Log.d("PEPE DEBUG", "wink left");
         // Turn left
         turnLeft();
         blinkLeftDown();
         sendIt();
         android.os.SystemClock.sleep(blink_wait_milli);
         resetBlinkLeft();
         resetTurn();
         pepeManager.silence();
         sendIt();
      } else if (isBetween(roll, 2, 2)) {
         Log.d("PEPE DEBUG", "wink right");
         // Turn right
         turnRight();
         blinkRightDown();
         sendIt();
         android.os.SystemClock.sleep(blink_wait_milli);
         resetBlinkRight();
         resetTurn();
         pepeManager.silence();
         sendIt();
      }else {
         Log.d("PEPE DEBUG", "double wink");
         // No look - double wink
         blinkLeftDown();
         sendIt();
         android.os.SystemClock.sleep(blink_wait_milli);
         resetBlinkLeft();
         sendIt();
         android.os.SystemClock.sleep(blink_wait_milli);
         blinkRightDown();
         sendIt();
         android.os.SystemClock.sleep(blink_wait_milli);
         resetBlinkRight();
         pepeManager.silence();
         sendIt();
      }
   }

   public void peacock() {
      Log.d("PEPE DEBUG", "peacock");
      // lift tail, rotate back and forth
      tailUp();
      sendIt();
      android.os.SystemClock.sleep(tail_wait_milli);
      turnLeft();
      sendIt();
      android.os.SystemClock.sleep(turn_wait_milli);
      turnRight();
      sendIt();
      android.os.SystemClock.sleep(soaring_turn_wait_milli); // Lock to lock turn
      resetTurn();
      pepeManager.silence();
      sendIt();
      // TODO: consider adding a double flap and possibly another "wiggle" before the reset
   }

   private boolean isBetween(int roll, int lower, int upper) {
      return lower <= roll && roll <= upper;
   }

}
