package com.pepedyne.pepe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.pepedyne.pepe.controller.PepeControlActivity;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class PepeLookJoyStickView extends JoystickView {

   public PepeLookJoyStickView(Context context) {
      super(context);
      this.initialize();
   }

   public PepeLookJoyStickView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      this.initialize();
   }

   public PepeLookJoyStickView(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.initialize();
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }

   private void initialize() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();
      this.setAutoReCenterButton(false);

      this.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_UP)
         {
//            host.getDispatcher().setLook(0);
            performClick();
         }
         return false;
      });

      this.setOnMoveListener((angle, strength) -> {
         host.getDispatcher().setLook(angle);
         host.getDispatcher().calculateLook();
      });
   }
}
