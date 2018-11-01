package com.pepedyne.pepe.views;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.pepedyne.pepe.controller.PepeControlActivity;

public class FlapButton extends AppCompatButton {
   public FlapButton(Context context) {
      super(context);
      init();
   }

   public FlapButton(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public FlapButton(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   private void init() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_DOWN)
         {
            host.getDispatcher().getManager().flapLeftDown();
            host.getDispatcher().getManager().flapRightUp();
            host.getDispatcher().sendData();
         }
         else if (event.getAction() == MotionEvent.ACTION_UP)
         {
            v.performClick();
            host.getDispatcher().getManager().flapLeftUp();
            host.getDispatcher().getManager().flapRightDown();
            host.getDispatcher().sendData();
         }
         return true;
      });
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }
}
