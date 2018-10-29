package com.pepedyne.pepe.dispatch;

import android.util.Log;

public class PepeAI implements PepeRunningTaskMacro {

    final int roll_variance = 100;

    @Override
    public void execute(PepeDispatcher dispatcher) {
        int roll = (int) Math.ceil(Math.random() * roll_variance);
        Log.d("PEPE DEBUG", "roll == " + roll);

        // Tweet always
        Log.d("PEPE DEBUG", "always tweet");
        dispatcher.tweetRand();

        if (isBetween(roll, 1, 10))
        {
            Log.d("PEPE DEBUG", "look left");
//         pepeManager.setLook(235); // Look middle left
            dispatcher.lookLeft();
        }
        else if (isBetween(roll, 11, 20))
        {
            Log.d("PEPE DEBUG", "look right");
//         pepeManager.setLook(445); // Look middle right
            dispatcher.lookRight();
        }
        else if (isBetween(roll, 21, 35))
        {
            Log.d("PEPE DEBUG", "flap twice");
//            dispatcher.flapTwice();
//         sendIt(); // Macro function does not require sendIt()
        }
        else if (isBetween(roll, 36, 55))
        {
            Log.d("PEPE DEBUG", "flap once");
//            dispatcher.flapOnce();
//         sendIt(); // Macro function does not require sendIt()
        }
        else if (isBetween(roll, 56, 60))
        {
            Log.d("PEPE DEBUG", "SOARING!");
// TODO: THIS IS MADNESS!!! DON'T TRY THIS UNTIL YOU ARE SURE PEPE CAN HANDLE IT!!!!
//            dispatcher.soaring();
//          sendIt(); // Macro function does not require sendIt()
        }
        else if (isBetween(roll, 61, 65))
        {
            Log.d("PEPE DEBUG", "lookAndFocus");
//            dispatcher.lookAndFocus();
//          sendIt(); // Macro function does not require sendIt()
        }
        else
        {
            // TODO: Do i reset all here?
            // pepeManager.resetLook();
            // handler.sendData();
        }
    }

    @Override
    public boolean isRepeatingTask() {
        return true;
    }

    private boolean isBetween(int roll, int lower, int upper) {
        return lower <= roll && roll <= upper;
    }
}
