package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepeMacros {
    // TODO Add Helper functions for main macros


    // Roll 1 to 5, TODO: Increase the number of possibilities
    // TODO: Is this value still adequate for M.I.K.E.P.?
    final static int blink_wait_milli = 350;
    final static int flap_wait_milli = 350;
    final static int tail_wait_milli = 350;
    final static int turn_wait_milli = 500;
    final static int soaring_turn_wait_milli = 2 * turn_wait_milli; // lock to lock rotation


    public static void flapTwice(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "flap twice");

        for ( int i = 0; i < 2; i++ ){
            PepeMacros.flapOnce(dispatcher);
        }
    }

    public static void flapOnce(PepeDispatcher dispatcher) {
        dispatcher.getManager().blinkLeftDown();// Opposite with eyes like hes squinting
        dispatcher.getManager().blinkRightDown();
        dispatcher.getManager().flapLeftUp();
        dispatcher.flapRightUp();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.getManager().flapRightDown();
        dispatcher.getManager().flapLeftDown();
        dispatcher.getManager().resetBlinkLeft();
        dispatcher.resetBlinkRight();
    }

    public static void flapLeftOnce(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "flap left once");
        dispatcher.getManager().flapLeftUp();
        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.getManager().flapLeftDown();
        dispatcher.silence();
    }

    public void flapRightOnce(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "flap right once");
        dispatcher.flapRightUp();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.getManager().flapRightDown();
        dispatcher.silence();
    }

    public void winkLeftOnce(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "wink left once");
        dispatcher.blinkLeftDown();
        android.os.SystemClock.sleep(blink_wait_milli);
        dispatcher.getManager().resetBlinkLeft();
        dispatcher.silence();
    }

    public void winkRightOnce(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "wink right once");
        dispatcher.blinkRightDown();
        android.os.SystemClock.sleep(blink_wait_milli);
        dispatcher.getManager().resetBlinkRight();
        dispatcher.silence();
    }

    public void focus(PepeDispatcher dispatcher) {
        Log.d("PEPE DEBUG", "focus");
        dispatcher.getManager().focusBlinkLeft();
        dispatcher.focusBlinkRight();
        android.os.SystemClock.sleep(blink_wait_milli);
        dispatcher.getManager().resetBlinkLeft();
        dispatcher.resetBlinkRight();
    }

    private boolean isBetween(int roll, int lower, int upper) {
        return lower <= roll && roll <= upper;
    }
}
