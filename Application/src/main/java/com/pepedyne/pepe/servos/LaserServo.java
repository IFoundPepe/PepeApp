package com.pepedyne.pepe.servos;

public class LaserServo extends StandardServo {
   private int prevStrength = 1;

   public LaserServo(String name, int min, int max) {
      super(name, min, max);
   }

   public void off() {
      prevStrength = this.current;
      this.current = 0;
   }

   public void on() {
      this.current = prevStrength;
   }

   public void setStrength(int value) {

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
}
