package com.pepedyne.pepe.controller;

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
   private Servo flap;
   private Servo tweet;
   private Servo lean;
   private Servo look;

   public PepeBluetoothConnectionManager()
   {
      sendIt = false;
      collection = new ServoCollection();

      flap = new StandardServo("flap", MIN_SERVO_FLAP, MAX_SERVO_FLAP);
      collection.registerServo(flap);

      tweet = new TweetServo("tweet", 0, NUM_FILES);
      collection.registerServo(tweet);

      lean = new StandardServo("lean", MIN_SERVO_LEAN, MAX_SERVO_LEAN);
      collection.registerServo(lean);

      look = new StandardServo("look", MIN_SERVO_LOOK, MAX_SERVO_LOOK);
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
      double value = collection.getServoByName("look").getLimit().getNorm() * distance;
      int look = (int) (collection.getServoByName("look").getLimit().getMean() + value);
      int lean = 0;

      if ( (angle_value == 0) && (strength_value == 0) )
      {
         look = this.look.getLimit().getMean();
      }

      if ( (angle_value > 45) && (angle_value < 135) )
      {
         // Forward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN  ) {
            lean = this.lean.getLimit().getMin();
         }
      }
      else if ( ( angle_value > 225) && (angle_value < 325) )
      {
         // Backward Tilt
         if ( strength_value > STRENGTH_JOYSTICK_LEAN ) {
            lean = this.lean.getLimit().getMax();
         }
      }

      int bin = Math.round((look - this.look.getLimit().getMin()) / STEP_SIZE);
      look = (bin * STEP_SIZE) + this.look.getLimit().getMin();
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

      double value = this.look.getLimit().getNorm() * distance;
      int look = (int) (this.look.getLimit().getMean() + value);

      int bin = Math.round((look - this.look.getLimit().getMin()) / STEP_SIZE);
      look = (bin * STEP_SIZE) + this.look.getLimit().getMin();
      this.setLook(look);
   }


   public String generateData()
   {
      sendIt = false;
      if ( this.look.isChanged() ||
           this.lean.isChanged() ||
           this.flap.isChanged() ||
           this.tweet.isChanged() )
      {
         sendIt = true;
      }

      this.look.step();
      this.lean.step();
      this.flap.step();
      this.tweet.step();

      //Write to the Bluetooth service transmit characteristic
      //Data transmit interface:
      //      ! == look
      //      @ == lean
      //      $ == flap
      //      # == tweet (see what I did there ;)
      data = this.look.generateData() + "|" +
              this.lean.generateData() + "|" +
              this.flap.generateData() + "|" +
              this.tweet.generateData() + "%";
      return data;
   }

   public boolean sendIt()
   {
      return sendIt;
   }

   public void setLook(int look) {
      this.look.setCurrent(look);;
   }

   public void setLean(int lean) {
      this.lean.setCurrent(lean);;
   }

   public void leanForward()
   {
      ((StandardServo) this.lean).setMin();
   }

   public void leanBack()
   {
      ((StandardServo) this.lean).setMax();
   }


   public void flapUp()
   {
      ((StandardServo) this.flap).setMax();
   }

   public void flapDown()
   {
      ((StandardServo) this.flap).setMin();
   }

   public void connectTweet()
   {
      ((TweetServo) this.tweet).silence();
   }

   public void tweet()
   {
      ((TweetServo) this.tweet).tweet();
   }

   public void tweetRand()
   {
      ((TweetServo) this.tweet).tweetRand();
   }

   public void silence()
   {
      ((TweetServo) this.tweet).silence();
   }

   public void setAngle_value(double angle_value) {
      this.angle_value = angle_value;
   }

   public void setStrength_value(double strength_value) {
      this.strength_value = strength_value;
   }


}
