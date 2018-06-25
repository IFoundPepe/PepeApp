package com.pepedyne.pepe.limits;

public class ServoLimit implements Limit {

   private int min;
   private int max;
   private int mean;
   private int norm;

   public ServoLimit(int min, int max) {
      this.min = min;
      this.max = max;
      this.mean = (min + max) / 2;
      this.norm = (max - min) / 2;
   }
   @Override
   public int getMin() {
      return min;
   }

   @Override
   public int getMax() {
      return max;
   }

   @Override
   public int getMean() {
      return mean;
   }

   @Override
   public int getNorm() {
      return norm;
   }
}
