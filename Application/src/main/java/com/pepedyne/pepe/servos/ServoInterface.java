package com.pepedyne.pepe.servos;

public interface ServoInterface {
   String getName();
   boolean isChanged();
   void step();
   int generateData();
   void setCurrent(int value);
}
