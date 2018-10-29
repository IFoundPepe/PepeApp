package com.pepedyne.pepe.bluetoothlegatt;

import android.app.Activity;
import android.widget.ExpandableListView;

public interface BluetoothLeServiceProvider {
   void onCreate(Activity context);

   void onResume(Activity activity);

   void onPause(Activity activity);

   void onDestroy(Activity activity);

   boolean isConnected();

   void connect(String address);

   void disconnect();

   ExpandableListView.OnChildClickListener getListener();

   void registerCallback(BluetoothCallbackInf callback);

   void send(byte[] data);
}
