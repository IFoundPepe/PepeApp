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

   public PepeBluetoothConnectionManager getManager() {
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
      pepeManager.turnLeft();
      handler.sendData();
   }

   public void turnRight() {
      pepeManager.turnRight();
      handler.sendData();
   }

   public void resetTurn() {
      pepeManager.resetTurn();
      handler.sendData();
   }

   public void lookLeft() {
      pepeManager.lookLeft();
      handler.sendData();
   }

   public void lookRight() {
      pepeManager.lookRight();
      handler.sendData();
   }

   public void resetLook() {
      pepeManager.resetLook();
      handler.sendData();
   }

   public void flapLeftUp() {
      pepeManager.flapLeftUp();
      handler.sendData();
   }

   public void flapLeftDown() {
      pepeManager.flapLeftDown();
      handler.sendData();
   }

   public void flapRightUp() {
      pepeManager.flapRightUp();
      handler.sendData();
   }

   public void flapRightDown() {
      pepeManager.flapRightDown();
      handler.sendData();
   }

   public void blinkLeftUp() {
      pepeManager.blinkLeftUp();
      handler.sendData();
   }

   public void blinkLeftDown() {
      pepeManager.blinkLeftDown();
      handler.sendData();
   }

   public void blinkRightUp() {
      pepeManager.blinkRightUp();
      handler.sendData();
   }

   public void blinkRightDown() {
      pepeManager.blinkRightDown();
      handler.sendData();
   }

   public void focusBlinkLeft() {
      pepeManager.focusBlinkLeft();
      handler.sendData();
   }

   public void focusBlinkRight() {
      pepeManager.focusBlinkRight();
      handler.sendData();
   }

   public void focus() {
      pepeManager.focusBlinkLeft();
      pepeManager.focusBlinkRight();
      handler.sendData();
   }

   public void resetBlinkLeft() {
      pepeManager.resetBlinkLeft();
      handler.sendData();
   }

   public void resetBlinkRight() {
      pepeManager.resetBlinkRight();
      handler.sendData();
   }

   public void tailUp() {
      pepeManager.tailUp();
      handler.sendData();
   }

   public void tailDown() {
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
      pepeManager.silence();
      handler.sendData();
   }

   public void calculateLook() {
      pepeManager.calculateLook();
      handler.sendData();
   }

   public void calculateTurn() {
      pepeManager.calculateTurn();
      handler.sendData();
   }

   public void calculate() {
      pepeManager.calculateLook();
      pepeManager.calculateTurn();
      handler.sendData();
   }

   public void laserOn() {
      pepeManager.laserOn();
      handler.sendData();
   }

   public void laserOff() {
      pepeManager.laserOff();
      handler.sendData();
   }

   public void setLaser(int value) {
      pepeManager.setLaser(value);
      handler.sendData();
   }

   public void rightEyeOff() {
      pepeManager.eyeRightOff();
      handler.sendData();
   }

   public void rightEyeOn() {
      pepeManager.eyeRightOn();
      handler.sendData();
   }

   public void rightEyeSetColor(int value) {
      pepeManager.setEyeRight(value);
      handler.sendData();
   }

   public void leftEyeOff() {
      pepeManager.eyeLeftOff();
      handler.sendData();
   }

   public void leftEyeOn() {
      pepeManager.eyeLeftOn();
      handler.sendData();
   }

   public void leftEyeSetColor(int value) {
      pepeManager.setEyeLeft(value);
      handler.sendData();
   }

   public void keyToggle() {
      pepeManager.keyToggle();
      handler.sendData();
   }

   public void keyRight() {
      pepeManager.keyRight();
      handler.sendData();
   }

   public void keyLeft() {
      pepeManager.keyLeft();
      handler.sendData();
   }

   public void keyOff() {
      pepeManager.keyOff();
      handler.sendData();
   }

   public byte[] generateData() {
      return pepeManager.generateData();
   }

   public void sendData()
   {
      handler.sendData();
   }

   public boolean sendIt() {
      return pepeManager.sendIt();
   }

   public void setLook(double ang) {
      pepeManager.setAngle_value(ang);
   }

   public void setTurn(double ang) {
      pepeManager.setTurn_value(ang);
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
