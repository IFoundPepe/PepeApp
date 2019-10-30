package com.pepedyne.pepe.servos;

public class EyeColorServo extends StandardServo {

   public static int BLACK = 0;
   public static int AQUA = 1;
   public static int RED = 2;
   public static int WHITE = 3;
   public static int BLUE = 4;
   public static int GREEN = 5;
   public static int YELLOW = 6;
   public static int MAGENTA = 7;
   public static int PURPLE = 8;
   public static int ORANGE = 9;

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