package com.pepedyne.pepe.views;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pepedyne.pepe.controller.PepeControlActivity;

public class TweetButton extends AppCompatButton {
   public TweetButton(Context context) {
      super(context);
      init();
   }

   public TweetButton(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      init();
   }
   public TweetButton(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      init();
   }

   public void init() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener((v, event) -> {
         switch (event.getAction())
         {
            case MotionEvent.ACTION_DOWN:
               host.getPepeManager().tweet();
               return true;
            case MotionEvent.ACTION_UP:
               v.performClick();
               host.getPepeManager().silence();
               return true;
         }
         return false;
      });
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }
}
