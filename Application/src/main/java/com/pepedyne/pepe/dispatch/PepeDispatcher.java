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

   public void leanForward() {
      pepeManager.leanForward();
      handler.sendData();
   }

   public void leanBack() {
      pepeManager.leanBack();
      handler.sendData();
   }

   public void flapUp() {
      pepeManager.flapUp();
      handler.sendData();
   }

   public void flapDown() {
      pepeManager.flapDown();
      handler.sendData();
   }

   public void setLook(int look) {
      pepeManager.setLook(look);
      handler.sendData();
   }

   public void setLean(int lean) {
      pepeManager.setLean(lean);
      handler.sendData();
   }

   public void connectTweet() {
      pepeManager.silence();
      handler.sendData();
   }

   public void calculate() {
      pepeManager.calculateLookAndLean();
      handler.sendData();

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

   public void runPepeAI() {
      final int look_roll_variance = 4; // 1-3, nothing; 4, look left; 5, look right
      final int flap_roll_variance = 5; // 1-3, nothing; 4, flap once; 5, flap twice
      final int flap_wait_milli = 350;

      // Look first if rolled
      int look_roll = (int) Math.ceil(Math.random() * look_roll_variance);
      Log.d("PEPE DEBUG", "lookroll == " + look_roll);
      switch (look_roll)
      {
         case 3:
            pepeManager.setLook(445); // Look middle right
            handler.sendData();
         case 4:
            pepeManager.setLook(235); // Look middle left
            handler.sendData();
         default:
            pepeManager.setLook(340); // Don't look
            handler.sendData();
      }

      // Tweet always
      pepeManager.tweetRand();
      // flap last if rolled
      int flap_roll = (int) Math.ceil(Math.random() * flap_roll_variance);

      Log.d("PEPE DEBUG", "flaproll == " + flap_roll);
      switch (flap_roll)
      {
         case 4:
            Log.d("PEPE DEBUG", "flap once");
            pepeManager.flapUp();
            handler.sendData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            handler.sendData();
            android.os.SystemClock.sleep(flap_wait_milli + (flap_wait_milli / 2));
            pepeManager.flapUp();
            handler.sendData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            handler.sendData();

         case 5:
            Log.d("PEPE DEBUG", "flap once");
            pepeManager.flapUp();
            handler.sendData();
            android.os.SystemClock.sleep(flap_wait_milli);
            pepeManager.flapDown();
            pepeManager.silence();
            handler.sendData();

         default:
            // Don't flap
      }
   }


}
