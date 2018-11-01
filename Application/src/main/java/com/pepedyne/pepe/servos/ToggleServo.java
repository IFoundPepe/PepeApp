package com.pepedyne.pepe.servos;

public class ToggleServo extends Servo {

   public ToggleServo(String name, int min, int max) {
      super(name, min, max);
      state = false;
      this.current = min;
   }

   private boolean state;

   @Override
   public void setCurrent(int value) {
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

   public void toggle() {
      state = !state;
      if (state)
      {
         this.current = this.getLimit().getMax();
      }
      else
      {
         this.current = this.getLimit().getMin();
      }

   }

}
