package com.pepedyne.pepe.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pepedyne.pepe.controller.PepeControlActivity;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class PepeJoyStickView extends JoystickView {

   public PepeJoyStickView(Context context) {
      super(context);
   }

   public PepeJoyStickView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public PepeJoyStickView(Context context, AttributeSet attrs) {
      super(context, attrs);
      this.initialize();
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }

   public void initialize() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener(new JoystickView.OnTouchListener() {

         @Override
         public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
               host.getPepeManager().setAngle_value(0);
               host.getPepeManager().setStrength_value(0);
               performClick();
            }
            return false;
         }

      });

      this.setOnMoveListener(new JoystickView.OnMoveListener() {
         @Override
         public void onMove(int angle, int strength) {
            host.getPepeManager().setAngle_value(angle);
            host.getPepeManager().setStrength_value(strength);
            host.getPepeManager().calculateLookAndLean();
         }
      });
   }
}
