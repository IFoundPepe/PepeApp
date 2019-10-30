package com.pepedyne.pepe.views;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.pepedyne.pepe.controller.PepeControlActivity;
import com.pepedyne.pepe.servos.EyeColorServo;

public class RedAlert extends AppCompatButton {
   public RedAlert(Context context) {
      super(context);
      init();
   }

   public RedAlert(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public RedAlert(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   private void init() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_DOWN)
         {
//            host.getDispatch();
            host.getDispatcher().getManager().blinkRightUp();
            host.getDispatcher().getManager().blinkLeftUp();
            host.getDispatcher().getManager().setEyeLeft(EyeColorServo.RED);
            host.getDispatcher().getManager().setEyeRight(EyeColorServo.RED);
//            host.getDispatcher().getManager().eyeLeftOn();
            host.getDispatcher().getManager().eyeRightOn();
            host.getDispatcher().sendData();
            host.getDispatcher().flapRightUp();
            host.getDispatcher().flapLeftUp();
         }
         else if (event.getAction() == MotionEvent.ACTION_UP)
         {
            v.performClick();
            host.getDispatcher().getManager().blinkRightDown();
            host.getDispatcher().getManager().blinkLeftDown();
            host.getDispatcher().getManager().eyeLeftOff();
            host.getDispatcher().getManager().eyeRightOff();
            host.getDispatcher().sendData();
            host.getDispatcher().flapRightDown();
            host.getDispatcher().flapLeftDown();
//            host.getDispatcher().flapRightUp();
         }
         return true;
      });
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }
}
