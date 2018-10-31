package com.pepedyne.pepe.views;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.pepedyne.pepe.controller.PepeControlActivity;

public class FocusButton extends AppCompatButton {

   public FocusButton(Context context) {
      super(context);
      init();
   }

   public FocusButton(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public FocusButton(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   private void init() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_DOWN)
         {
         }
         else if (event.getAction() == MotionEvent.ACTION_UP)
         {
            v.performClick();
         }
         return true;
      });
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }
}
