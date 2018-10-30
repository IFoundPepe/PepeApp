package com.pepedyne.pepe.servos;

public class EyeColorServo extends StandardServo {

   private int prevColor = 1;
   public EyeColorServo(String name, int min, int max) {
      super(name, min, max);
   }

   public void eyeOff() {
      prevColor = this.current;
      this.current = 0;
   }

   public void eyeOn() {
      this.current = prevColor;
   }

   public void setColor(int value) {

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