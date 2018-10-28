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
   // TODO: Is this value still adequate for M.I.K.E.P.?
   final int blink_wait_milli = 350;
   final int flap_wait_milli = 350;
   final int tail_wait_milli = 350;
   final int turn_wait_milli = 500;
   final int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation

   private PepeRunningTaskMacro macro;

   PepeBluetoothConnectionManager getManager() {
      return pepeManager;
   }

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
               macro.execute(PepeDispatcher.this);
//               runPepeAI();
               PepAIActionCounter = 0;
            }
         }
         finally
         {
            // 100% guarantee that this always happens, even if
            // your update method throws an exception
            if (macro.isRepeatingTask()) {
               mHandler.postDelayed(mStatusChecker, mInterval);
            }
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
       Log.d("PEPE DEBUG", "tweet");
      pepeManager.tweet();
      handler.sendData();
   }

   public void tweetRand() {
      pepeManager.tweetRand();
      handler.sendData();
   }

   public void silence() {
      pepeManager.silence();
      handler.sendData();
   }

   public void turnLeft() {
       Log.d("PEPE DEBUG", "turn left");
      pepeManager.turnLeft();
      handler.sendData();
   }

   public void turnRight() {
       Log.d("PEPE DEBUG", "turn right");
      pepeManager.turnRight();
      handler.sendData();
   }

   public void resetTurn() {
      Log.d("PEPE DEBUG", "turn reset");
      pepeManager.resetTurn();
      handler.sendData();
   }

   public void lookLeft() {
       Log.d("PEPE DEBUG", "look left");
      pepeManager.lookLeft();
      handler.sendData();
   }

   public void lookRight() {
       Log.d("PEPE DEBUG", "look right");
      pepeManager.lookRight();
      handler.sendData();
   }

   public void resetLook() {
      Log.d("PEPE DEBUG", "look reset");
      pepeManager.resetLook();
      handler.sendData();
   }

   public void flapLeftUp() {
       Log.d("PEPE DEBUG", "flap left up");
      pepeManager.flapLeftUp();
      handler.sendData();
   }

   public void flapLeftDown() {
       Log.d("PEPE DEBUG", "flap left down");
      pepeManager.flapLeftDown();
      handler.sendData();
   }

   public void flapRightUp() {
       Log.d("PEPE DEBUG", "flap right up");
      pepeManager.flapRightUp();
      handler.sendData();
   }

   public void flapRightDown() {
       Log.d("PEPE DEBUG", "flap right down");
      pepeManager.flapRightDown();
      handler.sendData();
   }

   public void blinkLeftUp() {
       Log.d("PEPE DEBUG", "blink left up");
      pepeManager.blinkLeftUp();
      handler.sendData();
   }

   public void blinkLeftDown() {
       Log.d("PEPE DEBUG", "blink left down");
      pepeManager.blinkLeftDown();
      handler.sendData();
   }

   public void blinkRightUp() {
      pepeManager.blinkRightUp();
       Log.d("PEPE DEBUG", "blink left up");
      handler.sendData();
   }

   public void blinkRightDown() {
       Log.d("PEPE DEBUG", "blink right down");
      pepeManager.blinkRightDown();
      handler.sendData();
   }

   public void resetBlinkLeft() {
      Log.d("PEPE DEBUG", "blink left reset");
      pepeManager.resetBlinkLeft();
      handler.sendData();
   }

   public void resetBlinkRight() {
      Log.d("PEPE DEBUG", "blink right reset");
      pepeManager.resetBlinkRight();
      handler.sendData();
   }

   public void tailUp() {
       Log.d("PEPE DEBUG", "tail up");
      pepeManager.tailUp();
      handler.sendData();
   }

   public void tailDown() {
       Log.d("PEPE DEBUG", "tail down");
      pepeManager.tailDown();
      handler.sendData();
   }

   public void setLook(int look) {
      pepeManager.setLook(look);
      handler.sendData();
   }

   public void setTurn(int turn) {
      pepeManager.setTurn(turn);
      handler.sendData();
   }

   public void setTail(int tail) {
      pepeManager.setTail(tail);
      handler.sendData();
   }

   public void setFlapLeft(int flap) {
      pepeManager.setFlapLeft(flap);
      handler.sendData();
   }

   public void setFlapRight(int flap) {
      pepeManager.setFlapRight(flap);
      handler.sendData();
   }

   public void setBlinkLeft(int blink) {
      pepeManager.setBlinkLeft(blink);
      handler.sendData();
   }

   public void setBlinkRight(int blink) {
      pepeManager.setBlinkRight(blink);
      handler.sendData();
   }

   public void connectTweet() {
      Log.d("PEPE DEBUG", "connect tweet");
      pepeManager.silence();
      handler.sendData();
   }

   public void calculate() {
      pepeManager.calculateLookAndTurn();
      handler.sendData();
   }

   public String generateData() {
      return pepeManager.generateData();
   }

   public void sendData()
   {
      handler.sendData();
   }

   public boolean sendIt() {
      return pepeManager.sendIt();
   }

   public void setMove(double str, double ang) {
      pepeManager.setStrength_value(str);
      pepeManager.setAngle_value(ang);
   }

   public void runOneTimeMacro() {
      this.startRepeatingTask();
   }

   public void toggleRepeatingMacro() {
      pepeAI = !pepeAI;
      if (pepeAI)
      {
         this.startRepeatingTask();
      }
      else
      {
         this.stopRepeatingTask();
      }
   }

   public boolean getRepeatingTaskState() {
      return pepeAI;
   }

   public void setRepeatingTaskState(boolean state) {
      pepeAI = state;
   }

   public void setMacro(PepeRunningTaskMacro macro) {
      this.macro = macro;
   }

}
