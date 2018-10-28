package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepeSoaring implements PepeRunningTaskMacro {

    // TODO Copy this for big macros

    // Roll 1 to 5, TODO: Increase the number of possibilities
    // TODO: Is this value still adequate for M.I.K.E.P.?
    final int blink_wait_milli = 350;
    final int flap_wait_milli = 350;
    final int tail_wait_milli = 350;
    final int turn_wait_milli = 500;
    final int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation

    
    @Override
    public void execute(PepeDispatcher dispatcher) {

        // TODO Only need to call "sendData" if you call "getManager"
        // This is how you queue actions
        Log.d("PEPE DEBUG", "SOARING!");
        dispatcher.flapLeftUp(); // Start with just left wing
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.turnRight();
        android.os.SystemClock.sleep(turn_wait_milli);// Finished 1st turn right
        dispatcher.getManager().flapLeftDown();
        dispatcher.getManager().flapRightUp();
        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.turnLeft();

        android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 1st turn left
        dispatcher.getManager().flapRightDown();
        dispatcher.flapLeftUp(); // This does call send data

        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.turnRight();
        android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 2nd turn right
        dispatcher.getManager().flapLeftDown();
        dispatcher.getManager().flapRightUp();
        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.turnLeft();
        android.os.SystemClock.sleep(soaring_turn_wait_milli);// Finished 2nd turn leftt
        dispatcher.getManager().flapLeftDown();
        dispatcher.getManager().flapRightUp();

        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.turnLeft();

        android.os.SystemClock.sleep(soaring_turn_wait_milli);
        // TODO: set to center position with constant
        dispatcher.setLook(340); // Recenter
        dispatcher.flapRightDown(); // Put right wing back down
        dispatcher.silence();

    }
    


    @Override
    public boolean isRepeatingTask() {
        return false;
    }
}
