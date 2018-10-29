package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepeLookAndFocus implements PepeRunningTaskMacro{

    final int roll_variance = 100;

    // TODO: Is this value still adequate for M.I.K.E.P.?
    final int blink_wait_milli = 350;
    final int flap_wait_milli = 350;
    final int tail_wait_milli = 350;
    final int turn_wait_milli = 500;
    final int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation

    @Override
    public void execute(PepeDispatcher dispatcher) {
        int roll = (int) Math.ceil(Math.random() * 3);
        if (isBetween(roll, 1, 1))
        {
            Log.d("PEPE DEBUG", "look n focus left");
            // Look left
            dispatcher.getManager().lookLeft();
            dispatcher.focus();
            // sendIt(); // Focus is a macro and already sends and unfocuses
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.getManager().resetLook();
            dispatcher.silence();
        }
        else if (isBetween(roll, 2, 2))
        {
            Log.d("PEPE DEBUG", "look n focus right");
            // Look right
            dispatcher.getManager().lookRight();
            dispatcher.focus();
            android.os.SystemClock.sleep(blink_wait_milli);
            dispatcher.getManager().resetLook();
            dispatcher.silence();
        }
        else
        {
            Log.d("PEPE DEBUG", "look n focus middle");
            // No look
            dispatcher.focus();
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
