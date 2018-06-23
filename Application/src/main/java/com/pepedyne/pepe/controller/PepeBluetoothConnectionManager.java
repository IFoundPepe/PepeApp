package com.pepedyne.pepe.controller;

import com.pepedyne.pepe.servos.RotationServo;
import com.pepedyne.pepe.servos.Servo;
import com.pepedyne.pepe.servos.ServoCollection;
import com.pepedyne.pepe.servos.StandardServo;
import com.pepedyne.pepe.servos.TweetServo;

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

   private static final int MIN_SERVO_FLAP = 320;
   private static final int MAX_SERVO_FLAP = 560;

   // Values for tweeting
   private static final int NUM_FILES = 9;

   // Derived for the look
   private static final int STEPS = 6;
   private static final int STEP_SIZE = (MAX_SERVO_LOOK - MIN_SERVO_LOOK) / STEPS;

   private ServoCollection collection;

   public PepeBluetoothConnectionManager()
   {
      sendIt = false;
      collection = new ServoCollection();

      Servo flap = new StandardServo("flap", MIN_SERVO_FLAP, MAX_SERVO_FLAP);
      collection.registerServo(flap);

      Servo tweet = new TweetServo("tweet", 0, NUM_FILES);
      collection.registerServo(tweet);

      Servo lean = new TweetServo("lean", MIN_SERVO_LEAN, MAX_SERVO_LEAN);
      collection.registerServo(lean);

      Servo look = new TweetServo("look", MIN_SERVO_LOOK, MAX_SERVO_LOOK);
      collection.registerServo(look);
   }

   // App Joystick Values
   private double angle_value;
   private double strength_value;
   private String data = "";
   private boolean sendIt;

   public void calculateLookAndLean()
   {
      double distance = Math.cos(angle_value * (Math.PI/180) + Math.PI);
      double value = ((RotationServo) collection.getServoByName("look")).getNorm() * distance;
      int look = (int) (((RotationServo) collection.getServoByName("look")).getMean() + value);
      int lean = 0;

      if ( (angle_value == 0) && (strength_value == 0) )
      {
         look = ((RotationServo) collection.getServoByName("look")).getMean();
      }

      if ( (angle_value > 45) && (angle_value < 135) )
      {
         // Forward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN  ) {
            lean = collection.getServoByName("lean").getMin();
         }
      }
      else if ( ( angle_value > 225) && (angle_value < 325) )
      {
         // Backward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN ) {
            lean = collection.getServoByName("lean").getMax();
         }
      }

      int bin = Math.round((look - collection.getServoByName("look").getMin()) / STEP_SIZE);
      look = (bin * STEP_SIZE) + collection.getServoByName("look").getMin();
      this.setLook(look);
      this.setLean(lean);
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

      double value = ((RotationServo) collection.getServoByName("look")).getNorm() * distance;
      int look = (int) (((RotationServo) collection.getServoByName("look")).getMean() + value);

      int bin = Math.round((look - collection.getServoByName("look").getMin()) / STEP_SIZE);
      look = (bin * STEP_SIZE) + collection.getServoByName("look").getMin();
      this.setLook(look);
   }


   public String generateData()
   {
      sendIt = false;
      if ( collection.getServoByName("look").isChanged() ||
              collection.getServoByName("lean").isChanged() ||
              collection.getServoByName("flap").isChanged() ||
              collection.getServoByName("tweet").isChanged() )
      {
         sendIt = true;
      }

      collection.getServoByName("look").step();
      collection.getServoByName("lean").step();
      collection.getServoByName("flap").step();
      collection.getServoByName("tweet").step();

      //Write to the Bluetooth service transmit characteristic
      //Data transmit interface:
      //      ! == look
      //      @ == lean
      //      $ == flap
      //      # == tweet (see what I did there ;)
      data = collection.getServoByName("look").generateData() + "|" +
              collection.getServoByName("lean").generateData() + "|" +
              collection.getServoByName("flap").generateData() + "|" +
              collection.getServoByName("tweet").generateData() + "%";
      return data;
   }

   public boolean sendIt()
   {
      return sendIt;
   }

   public void setLook(int look) {
      collection.getServoByName("look").setCurrent(look);;
   }

   public void setLean(int lean) {
      collection.getServoByName("lean").setCurrent(lean);;
   }

   public void leanForward()
   {
      ((StandardServo) collection.getServoByName("lean")).setMin();
   }

   public void leanBack()
   {
      ((StandardServo) collection.getServoByName("lean")).setMax();
   }


   public void flapUp()
   {
      ((StandardServo) collection.getServoByName("flap")).setMax();
   }

   public void flapDown()
   {
      ((StandardServo) collection.getServoByName("flap")).setMin();
   }

   public void connectTweet()
   {
      ((TweetServo) collection.getServoByName("tweet")).silence();
   }

   public void tweet()
   {
      ((TweetServo) collection.getServoByName("tweet")).tweet();
   }

   public void tweetRand()
   {
      ((TweetServo) collection.getServoByName("tweet")).tweetRand();
   }

   public void silence()
   {
      ((TweetServo) collection.getServoByName("tweet")).silence();
   }

   public void setAngle_value(double angle_value) {
      this.angle_value = angle_value;
   }

   public void setStrength_value(double strength_value) {
      this.strength_value = strength_value;
   }


}
