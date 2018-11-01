package com.pepedyne.pepe.controller;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.limits.ServoLimit;
import com.pepedyne.pepe.servos.EyeColorServo;
import com.pepedyne.pepe.servos.LaserServo;
import com.pepedyne.pepe.servos.Servo;
import com.pepedyne.pepe.servos.ServoCollection;
import com.pepedyne.pepe.servos.StandardServo;
import com.pepedyne.pepe.servos.ToggleServo;
import com.pepedyne.pepe.servos.TweetServo;

import java.nio.charset.Charset;
import java.util.Arrays;


/**
 * Created by Jeremy on 11/1/2017.
 */

public class PepeBluetoothConnectionManager {

   // Only want up to 90 degrees for the max lean
   private static final int STRENGTH_JOYSTICK_LEAN = 40;

   // Derived for the look
   private static final int STEPS = 15;

   private ServoCollection collection;
   private Servo flapLeft;
   private Servo flapRight;
   private Servo blinkLeft;
   private Servo blinkRight;
   private Servo tail;
   private Servo tweet;
   private Servo turn;
   private Servo look;
   private Servo key;
   private Servo eyeColorRight;
   private Servo eyeColorLeft;
   private Servo laser;

   private byte[] percentByte;

   private AppCompatActivity activity;

   public PepeBluetoothConnectionManager(Context context) {
      this.activity = (AppCompatActivity) context;


      String percent = "%";
      this.percentByte = percent.getBytes(Charset.forName("UTF-8"));

      sendIt = false;
      collection = new ServoCollection();
//      System.out.println("R String: " + R.string.flap_left_servo_min_key);
//      System.out.println("Get String: " + activity.getString(R.string.flap_left_servo_min_key));

      flapLeft = new StandardServo("flapLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_left_servo_min_key), activity.getString(R.string.flap_left_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_left_servo_max_key), activity.getString(R.string.flap_left_servo_max_default))));
      collection.registerServo(flapLeft);

      flapRight = new StandardServo("flapRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_right_servo_min_key), activity.getString(R.string.flap_right_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_right_servo_max_key), activity.getString(R.string.flap_right_servo_max_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_right_servo_min_key), activity.getString(R.string.flap_right_servo_min_default))));
      collection.registerServo(flapRight);

      blinkLeft = new StandardServo("blinkLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_left_servo_min_key), activity.getString(R.string.blink_left_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_left_servo_max_key), activity.getString(R.string.blink_left_servo_max_default))));
      collection.registerServo(blinkLeft);

      blinkRight = new StandardServo("blinkRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_right_servo_min_key), activity.getString(R.string.blink_right_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_right_servo_max_key), activity.getString(R.string.blink_right_servo_max_default))));
      collection.registerServo(blinkRight);

      tail = new StandardServo("tail",
              Integer.parseInt(this.getPreference(activity.getString(R.string.tail_servo_min_key), activity.getString(R.string.tail_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.tail_servo_max_key), activity.getString(R.string.tail_servo_max_default))));
      collection.registerServo(tail);

      tweet = new TweetServo("tweet",
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_min_key), activity.getString(R.string.tweet_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_max_key), activity.getString(R.string.tweet_servo_max_default))));
      collection.registerServo(tweet);

      turn = new StandardServo("turn",
              Integer.parseInt(this.getPreference(activity.getString(R.string.turn_servo_min_key), activity.getString(R.string.turn_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.turn_servo_max_key), activity.getString(R.string.turn_servo_max_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.turn_servo_max_key), activity.getString(R.string.turn_servo_max_default)))/2);
      collection.registerServo(turn);

      look = new StandardServo("look",
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_min_key), activity.getString(R.string.look_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_max_key), activity.getString(R.string.look_servo_max_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_max_key), activity.getString(R.string.look_servo_max_default)))/2);
      collection.registerServo(look);

      key = new ToggleServo("key",
              Integer.parseInt(this.getPreference(activity.getString(R.string.key_servo_min_key), activity.getString(R.string.key_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.key_servo_max_key), activity.getString(R.string.key_servo_max_default))));
      collection.registerServo(key);

      eyeColorLeft = new EyeColorServo("eyeColorLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_left_key), activity.getString(R.string.eye_color_left_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_left_servo_max_key), activity.getString(R.string.eye_color_left_default))));
      collection.registerServo(eyeColorLeft);

      eyeColorRight = new EyeColorServo("eyeColorRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_right_key), activity.getString(R.string.eye_color_right_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_right_key), activity.getString(R.string.eye_color_right_default))));
      collection.registerServo(eyeColorRight);

      laser = new LaserServo("laser",
              Integer.parseInt(this.getPreference(activity.getString(R.string.laser_servo_min_key), activity.getString(R.string.laser_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.laser_servo_max_key), activity.getString(R.string.laser_servo_max_default))));
      collection.registerServo(laser);

   }

   private String getPreference(String key, String defaultValue) {
      return PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext()).getString(key, defaultValue);
   }

   public void reset() {
      this.setServoLimits("flapLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_left_servo_min_key), activity.getString(R.string.flap_left_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_left_servo_max_key), activity.getString(R.string.flap_left_servo_max_default))));
      this.setServoLimits("flapRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_right_servo_min_key), activity.getString(R.string.flap_right_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.flap_right_servo_max_key), activity.getString(R.string.flap_right_servo_max_default))));
      this.setServoLimits("blinkLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_left_servo_min_key), activity.getString(R.string.blink_left_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_left_servo_max_key), activity.getString(R.string.blink_left_servo_max_default))));
      this.setServoLimits("blinkRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_right_servo_min_key), activity.getString(R.string.blink_right_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.blink_right_servo_max_key), activity.getString(R.string.blink_right_servo_max_default))));
      this.setServoLimits("tweet",
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_min_key), activity.getString(R.string.tweet_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.tweet_servo_max_key), activity.getString(R.string.tweet_servo_max_default))));
      this.setServoLimits("turn",
              Integer.parseInt(this.getPreference(activity.getString(R.string.turn_servo_min_key), activity.getString(R.string.turn_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.turn_servo_max_key), activity.getString(R.string.turn_servo_max_default))));
      this.setServoLimits("look",
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_min_key), activity.getString(R.string.look_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.look_servo_max_key), activity.getString(R.string.look_servo_max_default))));
      this.setServoLimits("key",
              Integer.parseInt(this.getPreference(activity.getString(R.string.key_servo_min_key), activity.getString(R.string.key_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.key_servo_max_key), activity.getString(R.string.key_servo_max_default))));
      this.setServoLimits("eyeColorRight",
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_right_key), activity.getString(R.string.eye_color_right_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_right_key), activity.getString(R.string.eye_color_right_default))));

      this.setServoLimits("eyeColorLeft",
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_left_key), activity.getString(R.string.eye_color_left_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.eye_color_left_key), activity.getString(R.string.eye_color_left_default))));

      this.setServoLimits("laser",
              Integer.parseInt(this.getPreference(activity.getString(R.string.laser_servo_min_key), activity.getString(R.string.laser_servo_min_default))),
              Integer.parseInt(this.getPreference(activity.getString(R.string.laser_servo_max_key), activity.getString(R.string.laser_servo_max_default))));
   }

   // App Joystick Values
   private double angle_value;
   private double turn_angle;
   private boolean sendIt;

   private void setServoLimits(String servoKey, int min, int max) {
      System.out.println("Set Servo Limit: " + servoKey + ", Min: " + min + ", Max: " + max);
      collection.getServoByName(servoKey).setLimit(new ServoLimit(min, max));
   }

   public void calculateTurn() {
      int turn = (int) turn_angle;
      if (turn_angle > 180)
      {
         turn = 360 - (int) turn_angle;
      }
      this.setTurn(turn);
   }

   public void calculateLook() {
      int look = (int) angle_value;
      if (angle_value > 180)
      {
         look = 360 - (int) angle_value;
      }
      this.setLook(look);
   }

   public void joystickLook(double distance_non_normalized) {
      final double controllerLowerLimit = -0.89;
      final double controllerUpperLimit = 1.0;
      double range = controllerUpperLimit + Math.abs(controllerLowerLimit);
      double distancePercentile = (distance_non_normalized + Math.abs(controllerLowerLimit)) / range;
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

   public static byte[] int2byte(int[]src) {
      int srcLength = src.length;
      byte[]dst = new byte[srcLength << 2];

      for (int i=0; i<srcLength; i++) {
         int x = src[i];
         int j = i << 2;
         dst[j++] = (byte) ((x >>> 0) & 0xff);
         dst[j++] = (byte) ((x >>> 8) & 0xff);
         dst[j++] = (byte) ((x >>> 16) & 0xff);
         dst[j++] = (byte) ((x >>> 24) & 0xff);
      }
      return dst;
   }

   public static byte[] short2byte(short[]src) {
      int srcLength = src.length;
      byte[]dst = new byte[srcLength << 2];

      for (int i=0; i<srcLength; i++) {
         int x = src[i];
         int j = i << 2;
         dst[j++] = (byte) ((x >>> 0) & 0xff);
         dst[j++] = (byte) ((x >>> 8) & 0xff);
//         dst[j++] = (byte) ((x >>> 16) & 0xff);
//         dst[j++] = (byte) ((x >>> 24) & 0xff);
      }
      return dst;
   }

   public byte[] generateData() {
      this.look.step();
      this.turn.step();
      this.flapLeft.step();
      this.flapRight.step();
      this.blinkLeft.step();
      this.blinkRight.step();
      this.tail.step();
      this.tweet.step();
      this.key.step();
      this.eyeColorRight.step();
      this.eyeColorLeft.step();
      this.laser.step();

      //Write to the Bluetooth service transmit characteristic
      //Data transmit interface:
      //      ! == look
      //      @ == turn
      // TODO: I renamed and added the below, where is the mapping to the symbols defined?
      //      $ == flapLeft
      //      $ == flapRight
      //      $ == blinkLeft
      //      $ == blinkRight
      //      $ == tail
      //      # == tweet (see what I did there ;) <--This is dumb. Don't be cute, be functional!

      System.out.println("DATA ---- ");
      System.out.println("************************Look: " + look.generateData());
      System.out.println("Turn: " + turn.generateData());
//      System.out.println("flapLeft: " + flapLeft.generateData());
//      System.out.println("flapRight: " + flapRight.generateData());
//      System.out.println("blinkLeft: " + blinkLeft.generateData());
//      System.out.println("blinkRight: " + blinkRight.generateData());
//      System.out.println("tail: " + tail.generateData());
//      System.out.println("key: " + key.generateData());
//      System.out.println("laser: " + laser.generateData());
      System.out.println("eyeColorRight: " + eyeColorRight.generateData());
      System.out.println("eyeColorLeft: " + eyeColorLeft.generateData());
//      System.out.println("tweet: " + tweet.generateData());
      int i = 0;
      byte [] packedData = new byte[12];
      packedData[i++] = (byte) look.generateData();
      packedData[i++] = (byte) turn.generateData();

//      packedData[i++] = (byte) ((flapLeft.generateData() >>> 0) & 0xff);
//      packedData[i++] = (byte) ((flapLeft.generateData() >>> 8) & 0xff);
      packedData[i++] = (byte) flapLeft.generateData();

//      packedData[i++] = (byte) ((flapRight.generateData() >>> 0) & 0xff);
//      packedData[i++] = (byte) ((flapRight.generateData() >>> 8) & 0xff);
      packedData[i++] = (byte) flapRight.generateData();

//      packedData[i++] = (short) flapLeft.generateData();
//      packedData[i++] = (short) flapRight.generateData();
      packedData[i++] = (byte) blinkLeft.generateData();
      packedData[i++] = (byte) blinkRight.generateData();
      packedData[i++] = (byte) tail.generateData();
      packedData[i++] = (byte) key.generateData();
      packedData[i++] = (byte) tweet.generateData();

      packedData[i++] = (byte) eyeColorRight.generateData();
      packedData[i++] = (byte) eyeColorLeft.generateData();
      packedData[i++] = (byte) laser.generateData();

      // Laser
      // RightEyeColor
      // LeftEyeColor
//      byte[] bytes = short2byte(packedData);
      byte [] bytes = packedData;

      byte[] combined = new byte[bytes.length + percentByte.length];

      for (int j = 0; j < combined.length; ++j)
      {
         combined[j] = j < bytes.length ? bytes[j] : percentByte[j - bytes.length];
      }
      return combined;
   }

   public boolean sendIt() {
      sendIt = false;
      if (this.look.isChanged() ||
              this.turn.isChanged() ||
              this.flapLeft.isChanged() ||
              this.flapRight.isChanged() ||
              this.blinkLeft.isChanged() ||
              this.blinkRight.isChanged() ||
              this.tail.isChanged() ||
              this.key.isChanged() ||
              this.eyeColorRight.isChanged() ||
              this.eyeColorLeft.isChanged() ||
              this.laser.isChanged() ||
              this.tweet.isChanged())
      {
         sendIt = true;
      }
      return sendIt;
   }

   public void keyToggle()
   {
      ((ToggleServo) this.key).toggle();
   }

   public void keyRight() {
      ((StandardServo) this.key).setMax();
   }

   public void keyLeft() {
      ((StandardServo) this.key).setMax();
   }

   public void keyOff() {
      ((StandardServo) this.key).setMin();
   }

   public void laserOn() {
      ((LaserServo) this.laser).on();
   }

   public void laserOff() {
      ((LaserServo) this.laser).off();
   }

   public void setLaser(int value) {
      ((LaserServo) this.laser).setStrength(value);
   }

   public void eyeRightOn() {
      ((EyeColorServo) this.eyeColorRight).eyeOn();
   }

   public void eyeRightOff() {
      ((EyeColorServo) this.eyeColorRight).eyeOff();
   }

   public void setEyeRight(int value) {
      ((EyeColorServo) this.eyeColorRight).setColor(value);
   }

   public void eyeLeftOn() {
      ((EyeColorServo) this.eyeColorLeft).eyeOn();
   }

   public void eyeLeftOff() {
      ((EyeColorServo) this.eyeColorLeft).eyeOff();
   }

   public void setEyeLeft(int value) {
      ((EyeColorServo) this.eyeColorLeft).setColor(value);
   }

   public void tweet() {
      ((TweetServo) this.tweet).tweet();
   }

   public void tweetRand() {
      ((TweetServo) this.tweet).tweetRand();
   }

   public void silence() {
      ((TweetServo) this.tweet).silence();
   }

   public void turnLeft() {
      ((StandardServo) this.turn).setMin();
   }

   public void turnRight() {
      ((StandardServo) this.turn).setMax();
   }

   public void resetTurn() {
      this.turn.setCurrent(this.turn.getLimit().getMean());
   }

   public void lookLeft() {
      ((StandardServo) this.look).setMin();
   }

   public void lookRight() {
      ((StandardServo) this.look).setMax();
   }

   public void resetLook() {
      this.look.setCurrent(this.look.getLimit().getMean());
   }

   public void flapLeftUp() {
      ((StandardServo) this.flapLeft).setMax();
   }

   public void flapLeftDown() {
      ((StandardServo) this.flapLeft).setMin();
   }

   public void flapRightUp() {
      ((StandardServo) this.flapRight).setMax();
   }

   public void flapRightDown() {
      ((StandardServo) this.flapRight).setMin();
   }

   public void blinkLeftUp() {
      ((StandardServo) this.blinkLeft).setMax();
   }

   public void blinkLeftDown() {
      ((StandardServo) this.blinkLeft).setMin();
   }

   public void blinkRightUp() {
      ((StandardServo) this.blinkRight).setMax();
   }

   public void blinkRightDown() {
      ((StandardServo) this.blinkRight).setMin();
   }

   public void resetBlinkLeft() {
      this.blinkLeft.setCurrent(this.blinkLeft.getLimit().getMean());
   }

   public void resetBlinkRight() {
      this.blinkRight.setCurrent(this.blinkRight.getLimit().getMean());
   }

   public void focusBlinkLeft() {
      this.blinkLeft.setCurrent((this.blinkLeft.getLimit().getMean()+this.blinkLeft.getLimit().getMin())/2);
   }

   public void focusBlinkRight() {
      this.blinkRight.setCurrent((this.blinkRight.getLimit().getMean()+this.blinkRight.getLimit().getMin())/2);
   }

   public void tailUp() {
      ((StandardServo) this.tail).setMax();
   }

   public void tailDown() {
      ((StandardServo) this.tail).setMin();
   }

   public void setLook(int look) {
      this.look.setCurrent(look);
   }

   public void setTurn(int turn) {
      this.turn.setCurrent(turn);
   }

   public void setTail(int tail) {
      this.tail.setCurrent(tail);
   }

   public void setFlapLeft(int flap) {
      this.flapLeft.setCurrent(flap);
   }

   public void setFlapRight(int flap) {
      this.flapRight.setCurrent(flap);
   }

   public void setBlinkLeft(int blink) {
      this.blinkLeft.setCurrent(blink);
   }

   public void setBlinkRight(int blink) {
      this.blinkRight.setCurrent(blink);
   }

   public void connectTweet() {
      ((TweetServo) this.tweet).silence();
   }

   public void setAngle_value(double angle_value) {
      this.angle_value = angle_value;
   }

   public void setTurn_value(double strength_value) {
      this.turn_angle = strength_value;
   }
}
