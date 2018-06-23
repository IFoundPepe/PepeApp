package com.pepedyne.pepe.servos;

public abstract class RotationServo extends StandardServo {

   private int mean;
   private int norm;

   public RotationServo(String name, int min, int max) {
      super(name, min, max);
      mean = (max + min) / 2;
      norm = (max - min) / 2;
   }

   public int getNorm() {
      return norm;
   }

   public int getMean()
   {
      return mean;
   }

}
