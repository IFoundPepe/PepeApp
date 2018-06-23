package com.pepedyne.pepe.servos;

public interface ServoInterface {

   public String getName();
   public int getMin();
   public int getMax();
   public boolean isChanged();
   public void step();
   public int generateData();
   public void setCurrent(int value);
}
