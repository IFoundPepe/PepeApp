package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepeTurnAndWink implements PepeRunningTaskMacro{
    // Roll 1 to 5, TODO: Increase the number of possibilities
    // TODO: Is this value still adequate for M.I.K.E.P.?
    final static int blink_wait_milli = 350;
    final static int flap_wait_milli = 350;
    final static int tail_wait_milli = 350;
    final static int turn_wait_milli = 500;
    final static int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation

    //    @Override
    public void execute(PepeDispatcher dispatcher) {
        int roll = (int) Math.ceil(Math.random() * 3);
        if (isBetween(roll, 1, 1))
        {
            Log.d("PEPE DEBUG", "wink left");
            // Turn left
            dispatcher.getManager().turnLeft();
            dispatcher.blinkLeftDown();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.getManager().resetBlinkLeft();
            dispatcher.getManager().resetTurn();
            dispatcher.silence();
        }
        else if (isBetween(roll, 2, 2))
        {
            Log.d("PEPE DEBUG", "wink right");
            // Turn right
            dispatcher.getManager().turnRight();
            dispatcher.blinkRightDown();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.getManager().resetBlinkRight();
            dispatcher.getManager().resetTurn();
            dispatcher.silence();
        }
        else
        {
            Log.d("PEPE DEBUG", "double wink");
            // No look - double wink
            dispatcher.blinkLeftDown();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.resetBlinkLeft();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.blinkRightDown();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.getManager().resetBlinkRight();
            dispatcher.silence();
        }

    }

    @Override
    public boolean isRepeatingTask() {
        return false;
    }

    private boolean isBetween(int roll, int lower, int upper) {
        return lower <= roll && roll <= upper;
    }
}
