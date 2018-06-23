package com.pepedyne.pepe.servos;

public abstract class Servo implements ServoInterface {
   private String name;
   protected int current;
   protected int previous;
   private int min;
   private int max;

   public Servo(String name, int min, int max){
      this.name = name;
      this.min = min;
      this.max = max;
      current = max;
      previous = min;
   }

   public String getName() {
      return this.name;
   }

   public int getMin() {
      return min;
   }

   public int getMax() {
      return max;
   }

   public boolean isChanged() {
      if (previous != current) {
         return true;
      }
      return false;
   }

   public void step() {
      previous = current;
   }

   public int generateData() {
      return current;
   }

   public abstract void setCurrent(int value);
}
