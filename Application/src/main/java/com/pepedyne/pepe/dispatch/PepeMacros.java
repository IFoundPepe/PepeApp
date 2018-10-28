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
        dispatcher.getManager().flapRightUp();
        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        dispatcher.getManager().flapRightDown();
        dispatcher.getManager().flapLeftDown();
        dispatcher.getManager().resetBlinkLeft();
        dispatcher.getManager().resetBlinkRight();
        dispatcher.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
    }

    public void flapLeftOnce() {
        Log.d("PEPE DEBUG", "flap left once");
        pepeManager.flapLeftUp();
        handler.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        pepeManager.flapLeftDown();
        pepeManager.silence();
        handler.sendData();
    }

    public void flapRightOnce() {
        Log.d("PEPE DEBUG", "flap right once");
        pepeManager.flapRightUp();
        handler.sendData();
        android.os.SystemClock.sleep(flap_wait_milli);
        pepeManager.flapRightDown();
        pepeManager.silence();
        handler.sendData();
    }

    public void winkLeftOnce() {
        Log.d("PEPE DEBUG", "wink left once");
        pepeManager.blinkLeftDown();
        handler.sendData();
        android.os.SystemClock.sleep(blink_wait_milli);
        pepeManager.resetBlinkLeft();
        pepeManager.silence();
        handler.sendData();
    }

    public void winkRightOnce() {
        Log.d("PEPE DEBUG", "wink right once");
        pepeManager.blinkRightDown();
        handler.sendData();
        android.os.SystemClock.sleep(blink_wait_milli);
        pepeManager.resetBlinkRight();
        pepeManager.silence();
        handler.sendData();
    }

    public void lookAndFocus() {
        int roll = (int) Math.ceil(Math.random() * 3);
        if (isBetween(roll, 1, 1))
        {
            Log.d("PEPE DEBUG", "look n focus left");
            // Look left
            lookLeft();
            focus();
            // sendIt(); // Focus is a macro and already sends and unfocuses
            android.os.SystemClock.sleep(blink_wait_milli);
            resetLook();
            pepeManager.silence();

            handler.sendData();
        }
        else if (isBetween(roll, 2, 2))
        {
            Log.d("PEPE DEBUG", "look n focus right");
            // Look right
            lookRight();
            focus();
            // sendIt(); // Focus is a macro and already sends and unfocuses
            android.os.SystemClock.sleep(blink_wait_milli);
            resetLook();
            pepeManager.silence();

            handler.sendData();
        }
        else
        {
            Log.d("PEPE DEBUG", "look n focus middle");
            // No look
            focus();
        }
    }

    public void focus() {
        Log.d("PEPE DEBUG", "focus");
        blinkLeftDown();
        blinkRightDown();

        handler.sendData();
        android.os.SystemClock.sleep(blink_wait_milli);
        resetBlinkLeft();
        resetBlinkRight();
        pepeManager.silence();

        handler.sendData();
    }

    public void turnAndWink() {
        int roll = (int) Math.ceil(Math.random() * 3);
        if (isBetween(roll, 1, 1))
        {
            Log.d("PEPE DEBUG", "wink left");
            // Turn left
            turnLeft();
            blinkLeftDown();

            handler.sendData();
            android.os.SystemClock.sleep(blink_wait_milli);
            resetBlinkLeft();
            resetTurn();
            pepeManager.silence();

            handler.sendData();
        }
        else if (isBetween(roll, 2, 2))
        {
            Log.d("PEPE DEBUG", "wink right");
            // Turn right
            turnRight();
            blinkRightDown();

            handler.sendData();
            android.os.SystemClock.sleep(blink_wait_milli);
            resetBlinkRight();
            resetTurn();
            pepeManager.silence();

            handler.sendData();
        }
        else
        {
            Log.d("PEPE DEBUG", "double wink");
            // No look - double wink
            blinkLeftDown();

            handler.sendData();
            android.os.SystemClock.sleep(blink_wait_milli);
            resetBlinkLeft();

            handler.sendData();
            android.os.SystemClock.sleep(blink_wait_milli);
            blinkRightDown();
            handler.sendData();
            android.os.SystemClock.sleep(blink_wait_milli);
            resetBlinkRight();
            pepeManager.silence();
            handler.sendData();
        }
    }

    public void peacock() {
        Log.d("PEPE DEBUG", "peacock");
        // lift tail, rotate back and forth
        tailUp();
        handler.sendData();
        android.os.SystemClock.sleep(tail_wait_milli);
        turnLeft();
        handler.sendData();
        android.os.SystemClock.sleep(turn_wait_milli);
        turnRight();
        handler.sendData();
        android.os.SystemClock.sleep(soaring_turn_wait_milli); // Lock to lock turn
        resetTurn();
        pepeManager.silence();
        handler.sendData();
        // TODO: consider adding a double flap and possibly another "wiggle" before the reset
    }

    private boolean isBetween(int roll, int lower, int upper) {
        return lower <= roll && roll <= upper;
    }
}
