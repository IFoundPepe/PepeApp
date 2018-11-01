package com.pepedyne.pepe.servos;

public class StandardServo extends Servo {

   public StandardServo(String name, int min, int max) {
      super(name, min, max);
   }

   public StandardServo(String name, int min, int max, int cur) {
      super(name, min, max, cur);
   }

   @Override
   public void setCurrent(int current) {
      this.current = current;
      if (current < this.getLimit().getMin())
      {
         this.current = this.getLimit().getMin();
      }
      else if (current > this.getLimit().getMax())
      {
         this.current = this.getLimit().getMax();
      }
   }

   public void setMax() {
      this.setCurrent(this.getLimit().getMax());
   }

   public void setMin() {
      this.setCurrent(this.getLimit().getMin());
   }

   public void setMean() {
      this.setCurrent(this.getLimit().getMean());
   }

}
