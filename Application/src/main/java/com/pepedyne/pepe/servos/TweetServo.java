package com.pepedyne.pepe.servos;

public class TweetServo extends StandardServo {

   public TweetServo(String name, int min, int max) {
      super(name, min, max);
   }
   private int prevTweet;

   public void tweet() {
      if (this.current == 0 ) {
         this.current = prevTweet;
      }

      if (this.current <= this.getLimit().getMax())
      {
         this.current++;
      }
      else
      {
         this.current = 1;
      }
//      prevTweet = current;
      System.out.println("CURRENT: " + current);
   }

   public void tweet(int value) {
      if (this.current < this.getLimit().getMax())
      {
         value = this.getLimit().getMax();
      }
      else if (this.current > this.getLimit().getMin())
      {
         value = this.getLimit().getMin();
      }
      this.current = value;
   }

   public void tweetRand() {
      this.current = (int) Math.ceil(Math.random() * this.getLimit().getMax());
   }

   public void silence() {
      prevTweet = this.current;
      this.current = 0;
      System.out.println("PREVIOUS: " + prevTweet);
   }

}
