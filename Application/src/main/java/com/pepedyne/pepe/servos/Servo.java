package com.pepedyne.pepe.servos;

import com.pepedyne.pepe.limits.Limit;
import com.pepedyne.pepe.limits.ServoLimit;

public abstract class Servo implements ServoInterface {
   private String name;
   protected int current;
   protected int previous;
   private Limit limit;

   public Servo(String name, int min, int max) {
      this.name = name;
      current = max;
      previous = min;
      this.limit = new ServoLimit(min, max);
   }

   public String getName() {
      return this.name;
   }

   public Limit getLimit() {
      return limit;
   }

   public void setLimit(Limit limit) {
      this.limit = limit;
   }

   public boolean isChanged() {
      if (previous != current)
      {
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
