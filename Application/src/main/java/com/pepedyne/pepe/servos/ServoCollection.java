package com.pepedyne.pepe.servos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServoCollection {
   private List<Servo> servos;
   private Map<String, Servo> servoMap;

   public ServoCollection() {
      this.servos = new ArrayList<>();
      this.servoMap = new HashMap<>();
   }

   public void registerServo(Servo servo) {
      servos.add(servo);
      servoMap.put(servo.getName(), servo);
   }

   public Servo getServoByName(String name) {
      if (servoMap.containsKey(name))
      {
         return servoMap.get(name);
      }
      return null;
   }

}
