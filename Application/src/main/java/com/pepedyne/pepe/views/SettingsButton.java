package com.pepedyne.pepe.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pepedyne.pepe.controller.PepeControlActivity;
import com.pepedyne.pepe.settings.SettingsActivity;

public class SettingsButton extends View {
   public SettingsButton(Context context) {
      super(context);
      init();
   }

   public SettingsButton(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public SettingsButton(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   public SettingsButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init();
   }

   public void init() {
      final PepeControlActivity host = (PepeControlActivity) this.getContext();

      this.setOnTouchListener((v, event) -> {
         if (event.getAction() == MotionEvent.ACTION_UP)
         {
            final Intent intent1 = new Intent(host.getApplicationContext(), SettingsActivity.class);
            host.startActivity(intent1);
            performClick();
         }
         return true;
      });
   }

   @Override
   public boolean performClick() {
      return super.performClick();
   }
}
