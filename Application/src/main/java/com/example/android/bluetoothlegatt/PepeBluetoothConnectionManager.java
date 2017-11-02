package com.example.android.bluetoothlegatt;

/**
 * Created by Jeremy on 11/1/2017.
 */

public class PepeBluetoothConnectionManager {

   private static final int MAX_SERVO_LOOK = 550;
   private static final int MIN_SERVO_LOOK = 130;

   // Only want up to 90 degrees for the max lean
   private static final int MAX_SERVO_LEAN = 300;
   private static final int MIN_SERVO_LEAN = 130;
   private static final int STRENGTH_JOYSTICK_LEAN = 40;

   private static final int MAX_SERVO_FLAP = 320;
   private static final int MIN_SERVO_FLAP = 560;

   // Derived for the look
   private static final int DEFAULT_LOOK = ((MAX_SERVO_LOOK + MIN_SERVO_LOOK) / 2 );
   private static final int STEPS = 6;
   private static final int STEP_SIZE = (MAX_SERVO_LOOK - MIN_SERVO_LOOK) / STEPS;
   private static final int NORM = (MAX_SERVO_LOOK - MIN_SERVO_LOOK)/2;
   private static final int MEAN_LOOK = (MAX_SERVO_LOOK + MIN_SERVO_LOOK)/2;

   // Derived for the lean
   private static final int MEAN_LEAN = (MAX_SERVO_LEAN + MIN_SERVO_LEAN) / 2;

   // Values for tweeting
   private static final int NUM_FILES = 9;

   private int look = DEFAULT_LOOK;
   private int previousLook = DEFAULT_LOOK;
   private int lean = MAX_SERVO_LEAN;
   private int previousLean = MAX_SERVO_LEAN;
   private int flap = MAX_SERVO_FLAP;
   private int previousFlap = MAX_SERVO_FLAP;
   private int tweet = NUM_FILES;
   private int previousTweet = 0;


   public PepeBluetoothConnectionManager()
   {
      sendIt = false;
   }
   private int tweetCount = 0;

   // App Joystick Values
   private double angle_value;
   private double strength_value;
   private String data;
   private boolean sendIt;

   public void calculateLookAndLean()
   {
      //        threshold = MAX_SERVO_LOOK;
      double distance = Math.cos(angle_value * (Math.PI/180));
      double value = NORM * distance;
      look = (int) (MEAN_LOOK + value);

      if ( (angle_value == 0) && (strength_value == 0) )
      {
         look = DEFAULT_LOOK;
      }

      if ( (angle_value > 45) && (angle_value < 135) )
      {
         // Forward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN  ) {
            lean = MIN_SERVO_LEAN;
         }
      }
      else if ( ( angle_value > 225) && (angle_value < 325) )
      {
         // Backward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN ) {
            lean = MAX_SERVO_LEAN;
         }

      }

      int bin = Math.round((look - MIN_SERVO_LOOK) / STEP_SIZE);
      look = (bin * STEP_SIZE) + MIN_SERVO_LOOK;
   }

   public void joystickLook(double distance_non_normalized )
   {
      final double controllerLowerLimit = -0.89;
      final double controllerUpperLimit = 1.0;
      double range = controllerUpperLimit + Math.abs(controllerLowerLimit);
      double distancePercentile = (distance_non_normalized + Math.abs(controllerLowerLimit))/range;
      double distance = (distancePercentile * 2) - 1; // -1.0 to 1.0 value
      if (distancePercentile > 0.4 && distancePercentile < 0.6)
      {
         distance = 0.0; // Close enough, reset to middle
      }

      double value = NORM * distance;
      look = (int) (MEAN_LOOK + value);

      int bin = Math.round((look - MIN_SERVO_LOOK) / STEP_SIZE);
      look = (bin * STEP_SIZE) + MIN_SERVO_LOOK;
   }


   public String generateData()
   {
      if ( look < MIN_SERVO_LOOK)
      {
         look = MIN_SERVO_LOOK;
      }

      if ( lean > MEAN_LEAN )
      {
         lean = MAX_SERVO_LEAN;
      }
      else // if ( lean < MEAN_LEAN )
      {
         lean = MIN_SERVO_LEAN;
      }

      sendIt = false;
      if ( (previousLook != look) ||
              (previousLean != lean) ||
              (previousFlap != flap) ||
              (previousTweet != tweet))
      {
         sendIt = true;
      }

      previousLook = look;
      previousLean = lean;
      previousFlap = flap;
      previousTweet = tweet;

      //Write to the Bluetooth service transmit characteristic
      //Data transmit interface:
      //      ! == look
      //      @ == lean
      //      $ == flap
      //      # == tweet (see what I did there ;)
      data = look + "|" +
              lean + "|" +
              flap + "|" +
              tweet + "%";
      return data;
   }

   public boolean sendIt()
   {
      return sendIt;
   }

   public void setLook(int look) {
      this.look = look;
   }

   public void leanForward()
   {
      this.lean = MIN_SERVO_LEAN;
   }

   public void leanBack()
   {
      this.lean = MAX_SERVO_LEAN;
   }


   public void flapUp()
   {
      this.flap = MAX_SERVO_FLAP;
   }

   public void flapDown()
   {
      this.flap = MIN_SERVO_FLAP;
   }

   public void connectTweet()
   {
      tweet = NUM_FILES;
   }

   public void tweet()
   {
      //tweet = (int) Math.ceil(Math.random() * NUM_FILES) ;
      if(tweetCount <= NUM_FILES)
      {
         tweetCount++;
      }
      else
      {
         tweetCount = 1;
      }
      tweet = tweetCount;
   }

   public void silence()
   {
      tweet = 0;
   }

   public void setAngle_value(double angle_value) {
      this.angle_value = angle_value;
   }

   public void setStrength_value(double strength_value) {
      this.strength_value = strength_value;
   }


}
