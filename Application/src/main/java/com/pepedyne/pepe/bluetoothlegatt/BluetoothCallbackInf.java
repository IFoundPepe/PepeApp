package com.pepedyne.pepe.bluetoothlegatt;

import android.widget.SimpleExpandableListAdapter;

public interface BluetoothCallbackInf {
   void onConnect();
   void updateConnectionState(final int resourceId);
   void clearUserInterface();
   void displayData(String data);
   void setServiceList(SimpleExpandableListAdapter adapter);
}
