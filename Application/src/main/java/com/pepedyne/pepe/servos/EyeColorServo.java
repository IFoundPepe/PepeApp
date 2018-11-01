package com.pepedyne.pepe.servos;

public class EyeColorServo extends StandardServo {

   public EyeColorServo(String name, int min, int max) {
      super(name, min, max);
      this.current = max;
   }

   public void eyeOff() {
      this.current = 0;
   }

   public void eyeOn() {
      this.current = this.getLimit().getMax();
   }

   public void setColor(int value) {
      this.current = value;
   }
}