package com.pepedyne.pepe.controller;

import android.util.Log;
import android.view.KeyEvent;

import com.pepedyne.pepe.dispatch.PepeDispatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JoyConButtonHandler {
   private static final Map<String, Integer> keyMapperLeftJoyCon;
   private static final Map<String, Integer> keyMapperRightJoyCon;

   static {
      Map<String, Integer> aMap = new HashMap<>();
      Map<String, Integer> bMap = new HashMap<>();

      // ====Left JoyCon====
      // up arrow, Left JoyCon
      aMap.put("upArrowDown", 98); // 98
      aMap.put("upArrowUp", 23); // 23
      // down arrow, Left JoyCon
      aMap.put("downArrowDown", 97); // 97
      aMap.put("downArrowUp", 4); // 4
      // left arrow, Left JoyCon
      aMap.put("leftArrow", 96); // 96 (Up and Down)
      // right arrow, Left JoyCon
      aMap.put("rightArrow", 99); // 99 (Up and Down)
      // L button, Left JoyCon
      aMap.put("LbuttonDown", 107); // 107
      aMap.put("LbuttonUp", 23); // 23
      // ZL button, Left JoyCon
      aMap.put("ZLbutton", 1050); // 1050 (Up and Down)
      // minus button, Left JoyCon
      aMap.put("minusButton", 104); // 104 (Up and Down)
      // SL button, Right JoyCon
      aMap.put("SLbutton", 100); // 100 - Same as SL
      // SR button, Right JoyCon
      aMap.put("SRbutton", 101); // 101 - Same as SR

      // ====Right JoyCon====
      // X Button, Right JoyCon
      bMap.put("XbuttonDown", 97); // 97 - Same as down arrow press
      bMap.put("XbuttonUp", 4); // 4 - Same as down arrow release
      // B Button, Right JoyCon
      bMap.put("BbuttonDown", 98); // 98 - Same as R button press
      bMap.put("BbuttonUp", 23); // 23 - Same as R button release
      // Y Button, Right JoyCon
      bMap.put("Ybutton", 99); // 99 (Up and Down) - Same as right arrow
      // A Button, Right JoyCon
      bMap.put("Abutton", 96); // 96 (Up and Down) - Same as left arrow
      // R Button, Right JoyCon
      bMap.put("RbuttonDown", 107); // 107 - Same as B button press
      bMap.put("RbuttonUp", 23); // 23 - Same as B button release
      // ZR Button, Right JoyCon
      bMap.put("ZRbutton", 1050); // 1050 (Up and Down) - Same as ZL
      // plus button, Right JoyCon
      bMap.put("plusButton", 105); // 105 (Up and Down)
      // SR button, Right JoyCon
      bMap.put("SRbutton", 101); // 101 - Same as SR
      // SL button, Right JoyCon
      bMap.put("SLbutton", 100); // 100 - Same as SL

      keyMapperLeftJoyCon = Collections.unmodifiableMap(aMap);
      keyMapperRightJoyCon = Collections.unmodifiableMap(bMap);
   }

   public static void debugKeyEvent(KeyEvent event) {
      Log.i("\tPEPE DEBUG","--------------------------");
      Log.d("PEPE DEBUG", "keyDown keyCode: " + event.getKeyCode());
      Log.i("\tPEPE DEBUG", "KeyEvent DeviceId: " + event.getDeviceId());
      Log.i("\tPEPE DEBUG", "KeyEvent Id: " + event.getDevice().getId());
      Log.i("\tPEPE DEBUG", "KeyEvent getFlags: " + event.getFlags());
      Log.i("\tPEPE DEBUG", "KeyEvent getDownTime: " + event.getDownTime());

      Log.i("\tPEPE DEBUG", "KeyEvent getCharacters: " + event.getCharacters());
      Log.i("\tPEPE DEBUG", "KeyEvent MetaState: " + event.getMetaState());
      Log.i("\tPEPE DEBUG", "KeyEvent getModifiers: " + event.getModifiers());
      Log.i("\tPEPE DEBUG", "KeyEvent getRepeatCount: " + event.getRepeatCount());

      Log.i("\tPEPE DEBUG", "KeyEvent getAction: " + event.getAction());
   }

   public static boolean  executeLeftJoyConKeyEvent(KeyEvent event, PepeDispatcher dispatcher) {
//======LEFT JOYCON KEY CODES======
//      upArrowDown
//      upArrowUp
//      downArrowDown
//      downArrowUp
//      leftArrow
//      rightArrow
//      LbuttonDown
//      LbuttonUp
//      ZLbutton
//      minusButton
//      SLbutton
//      SRbutton

      int keyCode = event.getKeyCode();
      if (keyCode == keyMapperLeftJoyCon.get("upArrowDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: upArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: upArrowDown - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("downArrowDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: downArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: downArrowDown - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("leftArrow"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: leftArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: leftArrowDown - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("rightArrow"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: rightArrowDown - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: rightArrowDown - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("LbuttonDown"))
      {
         // TODO: What do we want this to do? - Should this be for the wings?
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: LbuttonDown - LEFT - Action Down");
// TODO: Enable after button debug
//            // TODO: Make sure if flaps the correct wing
//            // TODO: This may cause repetitive flaps. Do we invert logic?
//            pepeDispatcher.flapUp(); // Leftt Flap Up
//            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
// TODO: Enable after button debug
            Log.i("\tPEPE DEBUG", "JoyCon: LbuttonDown - LEFT - Action Up");
//            pepeDispatcher.flapDown(); // Left Flap Down
//            return true;
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("ZLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZLbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZLbutton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("minusButton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: minusButton - LEFT - Action Down");
// TODO: Enable this after debugging
//            // TODO: Do we want to use + to start AI and - to disable AI?
//            // TODO: Maybe check state of pepeAI with getAIstate()
//            pepeDispatcher.runPepeAI();
//            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: minusButton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("SLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperLeftJoyCon.get("SRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - LEFT - Action Up");
            // Do Nothing
         }
      }
      else
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - LEFT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - LEFT - Action Up");
            // Do Nothing
         }
      }
      return true;
      //      return super.dispatchKeyEvent(event);
   }

   public static boolean executeRightJoyConKeyEvent(KeyEvent event, PepeDispatcher dispatcher) {
//======RIGHT JOYCON KEY CODES======
//      XbuttonDown
//      XbuttonUp
//      BbuttonDown
//      BbuttonUp
//      Ybutton
//      Abutton
//      RbuttonDown
//      RbuttonUp
//      ZRbutton
//      plusButton
//      SRbutton
//      SLbutton

      int keyCode = event.getKeyCode();
      if (keyCode == keyMapperRightJoyCon.get("XbuttonDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: XbuttonDown - RIGHT - Action Down");
            // TODO: Enable this after debugging
            // TODO: This may cause repetitive tweets. Do we invert logic?
            //            pepeDispatcher.tweet();
            //            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: XbuttonDown - RIGHT - Action Up");
// TODO: Enable this after debugging
//            pepeDispatcher.silence();
//            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("BbuttonDown"))
      {
         // TODO: What will this do?
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: BbuttonDown - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: BbuttonDown - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("Ybutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Ybutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Ybutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("Abutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Abutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Abutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("RbuttonDown"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: RbuttonDown - RIGHT - Action Down");
// TODO: Enable after button debug
//            // TODO: Make sure if flaps the correct wing
//            // TODO: This may cause repetitive flaps. Do we invert logic?
//            pepeDispatcher.flapUp(); // Right Flap Up
//            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: RbuttonDown - RIGHT - Action Up");
// TODO: Enable after button debug
//            pepeDispatcher.flapDown(); // Right Flap Down
//            return true;
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("ZRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: ZRbutton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("plusButton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: plusButton - RIGHT - Action Down");
            // TODO: Do we want to use + to start AI and - to disable AI?
            // TODO: Maybe check state of pepeAI with getAIstate()
            dispatcher.runPepeAI();
            return true;
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: plusButton - RIGHT - Action Up");
            // Do Nothing
         }
      }
      else if (keyCode == keyMapperRightJoyCon.get("SRbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SRbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SRbutton - RIGHT - Action Up");
            // Do Nothing
         }      }
      else if (keyCode == keyMapperRightJoyCon.get("SLbutton"))
      {
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: SLbutton - RIGHT - Action Up");
            // Do Nothing
         }      }
      else
      {
         // TODO: Undefined keycode!!!!
         if (event.getAction() == KeyEvent.ACTION_DOWN)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - RIGHT - Action Down");
            // Do Nothing
         }
         else if (event.getAction() == KeyEvent.ACTION_UP)
         {
            Log.i("\tPEPE DEBUG", "JoyCon: Undefined - RIGHT - Action Up");
            // Do Nothing
         }
      }
      return true;
   }
}
