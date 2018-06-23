package com.pepedyne.pepe.servos;

public class TweetServo extends StandardServo {

   public TweetServo(String name, int min, int max) {
      super(name, min, max);
   }

   public void tweet() {
      if(current <= this.getMax())
      {
         current++;
      }
      else
      {
         current = 1;
      }
   }
   public void tweetRand() {
      current = (int) Math.ceil(Math.random() * this.getMax()) ;
   }

   public void silence() {
      current = 0;
   }

}
