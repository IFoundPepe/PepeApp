package com.pepedyne.pepe.servos;

public class TweetServo extends StandardServo {

   public TweetServo(String name, int min, int max) {
      super(name, min, max);
   }

   public void tweet() {
      if(this.current <= this.getLimit().getMax())
      {
         this.current++;
      }
      else
      {
         this.current = 1;
      }
   }
   public void tweet(int value) {

      if(this.current < this.getLimit().getMax())
      {
         value = this.getLimit().getMax();
      }
      else if (this.current > this.getLimit().getMin()) {
         value = this.getLimit().getMin();
      }
      this.current = value;
   }

   public void tweetRand() {
      this.current = (int) Math.ceil(Math.random() * this.getLimit().getMax()) ;
   }

   public void silence() {
      this.current = 0;
   }

}
