package com.pepedyne.pepe.controller;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.limits.ServoLimit;
import com.pepedyne.pepe.servos.Servo;
import com.pepedyne.pepe.servos.ServoCollection;
import com.pepedyne.pepe.servos.StandardServo;
import com.pepedyne.pepe.servos.TweetServo;


/**
 * Created by Jeremy on 11/1/2017.
 */

public class PepeBluetoothConnectionManager {

   // Only want up to 90 degrees for the max lean
   private static final int STRENGTH_JOYSTICK_LEAN = 40;

   // Derived for the look
   private static final int STEPS = 6;

   private ServoCollection collection;
   private Servo flap;
   private Servo tweet;
   private Servo lean;
   private Servo look;

   private AppCompatActivity activity;

   public PepeBluetoothConnectionManager(Context context)
   {
      this.activity = (AppCompatActivity) context;

      sendIt = false;
      collection = new ServoCollection();
      System.out.println("R String: " + R.string.flap_servo_min_key);
      System.out.println("Get String: " + activity.getString(R.string.flap_servo_min_key));

      flap = new StandardServo("flap",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_servo_min_key), activity.getString(R.string.flap_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_servo_max_key), activity.getString(R.string.flap_servo_max_default))));
      collection.registerServo(flap);

      tweet = new TweetServo("tweet",
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_min_key), activity.getString(R.string.tweet_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_max_key), activity.getString(R.string.tweet_servo_max_default))));
      collection.registerServo(tweet);

      lean = new StandardServo("lean",
              Integer.parseInt(this.getPreference(activity.getString(R.string.lean_servo_min_key), activity.getString(R.string.lean_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.lean_servo_max_key), activity.getString(R.string.lean_servo_max_default))));
      collection.registerServo(lean);

      look = new StandardServo("look",
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_min_key), activity.getString(R.string.look_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_max_key), activity.getString(R.string.look_servo_max_default))));
      collection.registerServo(look);
   }

   private String getPreference(String key, String defaultValue)
   {
      return PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext()).getString(key, defaultValue);
   }

   public void reset()
   {
      this.setServoLimits("flap",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_servo_min_key), activity.getString(R.string.flap_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_servo_max_key), activity.getString(R.string.flap_servo_max_default))));
      this.setServoLimits("tweet",
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_min_key), activity.getString(R.string.tweet_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_max_key), activity.getString(R.string.tweet_servo_max_default))));
      this.setServoLimits("lean",
              Integer.parseInt(this.getPreference(activity.getString(R.string.lean_servo_min_key), activity.getString(R.string.lean_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.lean_servo_max_key), activity.getString(R.string.lean_servo_max_default))));
      this.setServoLimits("look",
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_min_key), activity.getString(R.string.look_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_max_key), activity.getString(R.string.look_servo_max_default))));
   }

   // App Joystick Values
   private double angle_value;
   private double strength_value;
   private String data = "";
   private boolean sendIt;

   private void setServoLimits(String servoKey, int min, int max)
   {
      System.out.println("Set Servo Limit: " + servoKey + ", Min: " + min + ", Max: " + max);
      collection.getServoByName(servoKey).setLimit(new ServoLimit(min, max));
   }

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

      int bin = Math.round((look - this.look.getLimit().getMin()) / (this.look.getLimit().getRange() / STEPS));
      look = (bin * (this.look.getLimit().getRange() / STEPS) ) + this.look.getLimit().getMin();
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

      int bin = Math.round((look - this.look.getLimit().getMin()) / (this.look.getLimit().getRange() / STEPS));
      look = (bin * (this.look.getLimit().getRange() / STEPS)) + this.look.getLimit().getMin();
      this.setLook(look);
   }


   public String generateData()
   {
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
      sendIt = false;
      if ( this.look.isChanged() ||
           this.lean.isChanged() ||
           this.flap.isChanged() ||
           this.tweet.isChanged() )
      {
         sendIt = true;
      }
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
