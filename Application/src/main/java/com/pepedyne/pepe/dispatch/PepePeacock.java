package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepePeacock implements PepeRunningTaskMacro{

    // TODO: Is this value still adequate for M.I.K.E.P.?
    final int tail_wait_milli = 350;
    final int turn_wait_milli = 500;
    final int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation

    @Override
    public void execute(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "peacock");
        // lift tail, rotate back and forth
        dispatcher.tailUp();
        android.os.SystemClock.sleep(tail_wait_milli);
        dispatcher.turnLeft();
        android.os.SystemClock.sleep(turn_wait_milli);
        dispatcher.turnRight();
        android.os.SystemClock.sleep(soaring_turn_wait_milli); // Lock to lock turn
        dispatcher.getManager().resetTurn();
        dispatcher.silence();
        // TODO: consider adding a double flap and possibly another "wiggle" before the reset
    }

    @Override
    public boolean isRepeatingTask() {
        return false;
    }

    private boolean isBetween(int roll, int lower, int upper) {
        return lower <= roll && roll <= upper;
    }
}
