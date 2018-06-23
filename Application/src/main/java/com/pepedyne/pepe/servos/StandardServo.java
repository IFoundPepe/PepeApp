package com.pepedyne.pepe.servos;

public class StandardServo extends Servo {

   public StandardServo(String name, int min, int max) {
      super(name, min, max);
   }

   @Override
   public void setCurrent(int current) {
      this.current = current;
      if ( current < this.getMin() )
      {
         this.current = this.getMin();
      }
      else if ( current > this.getMax() )
      {
         this.current = this.getMax();
      }
   }

   public void setMax()
   {
      this.setCurrent(this.getMax());
   }

   public void setMin() {
      this.setCurrent(this.getMin());
   }

}
