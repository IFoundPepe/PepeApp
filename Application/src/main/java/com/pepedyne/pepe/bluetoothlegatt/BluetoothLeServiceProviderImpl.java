package com.pepedyne.pepe.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.example.android.bluetoothlegatt.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;

public class BluetoothLeServiceProviderImpl implements BluetoothLeServiceProvider {

   public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
   public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
   private BluetoothCallbackInf callback;
   private BluetoothLeService mBluetoothLeService;
   private boolean mConnected = false;
   private BluetoothGattCharacteristic mNotifyCharacteristic;
   private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
           new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

   public void registerCallback(BluetoothCallbackInf callback) {
      this.callback = callback;
   }

   private BroadcastReceiver mGattUpdateReceiver;
   private ExpandableListView.OnChildClickListener servicesListClickListner;
   private ServiceConnection mServiceConnection;

   public BluetoothLeServiceProviderImpl(Activity activity)
   {
      this.initBroadcastReceiver(activity);
      this.initOnChildClickListener();
      this.initServiceConnection(activity);
   }

   @Override
   public ExpandableListView.OnChildClickListener getListener() {
      return servicesListClickListner;
   }

   private void initBroadcastReceiver(final Activity activity)
   {
      // Handles various events fired by the Service.
      // ACTION_GATT_CONNECTED: connected to a GATT server.
      // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
      // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
      // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
      //                        or notification operations.
      mGattUpdateReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
               mConnected = true;
               callback.updateConnectionState(R.string.connected);
               activity.invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
               mConnected = false;
               callback.updateConnectionState(R.string.disconnected);
               activity.invalidateOptionsMenu();
               callback.clearUserInterface();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
               // Show all the supported services and characteristics on the user interface.
               callback.setServiceList(displayGattServices(activity, mBluetoothLeService.getSupportedGattServices()));
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
               callback.displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
         }
      };
   }

   private void initServiceConnection(final Activity activity) {
      mServiceConnection = new ServiceConnection() {

         @Override
         public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
               Log.e(activity.getClass().getSimpleName(), "Unable to initialize Bluetooth");
               activity.finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(activity.getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS));
            callback.onConnect();
         }

         @Override
         public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
         }
      };
   }

   private void initOnChildClickListener() {
      // If a given GATT characteristic is selected, check for supported features.  This sample
      // demonstrates 'Read' and 'Notify' features.  See
      // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
      // list of supported characteristic features.
      servicesListClickListner =
           new ExpandableListView.OnChildClickListener() {
              @Override
              public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                          int childPosition, long id) {
                 if (mGattCharacteristics != null)
                 {
                    final BluetoothGattCharacteristic characteristic =
                            mGattCharacteristics.get(groupPosition).get(childPosition);
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                    {
                       // If there is an active notification on a characteristic, clear
                       // it first so it doesn't update the data field on the user interface.
                       if (mNotifyCharacteristic != null)
                       {
                          mBluetoothLeService.setCharacteristicNotification(
                                  mNotifyCharacteristic, false);
                          mNotifyCharacteristic = null;
                       }
                       mBluetoothLeService.readCharacteristic(characteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                    {
                       mNotifyCharacteristic = characteristic;
                       mBluetoothLeService.setCharacteristicNotification(
                               characteristic, true);
                    }
                    return true;
                 }
                 return false;
              }
           };
   }

   @Override
   public void onCreate(Activity activity) {
      Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
      activity.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
   }

   @Override
   public void onResume(Activity activity) {
      activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
      if (mBluetoothLeService != null) {
         final boolean result = mBluetoothLeService.connect(activity.getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS));
         Log.d(TAG, "Connect request result=" + result);
      }
   }

   @Override
   public void onPause(Activity activity)
   {
      activity.unregisterReceiver(mGattUpdateReceiver);
   }

   @Override
   public void onDestroy(Activity activity)
   {
      activity.unbindService(mServiceConnection);
      mBluetoothLeService = null;
   }

   @Override
   public boolean isConnected()
   {
      return mConnected;
   }

   @Override
   public void connect(String address)
   {
      mBluetoothLeService.connect(address);
   }

   @Override
   public void disconnect()
   {
      mBluetoothLeService.disconnect();
   }

   @Override
   public void send(String data)
   {
      mBluetoothLeService.sendData(data);
   }

   private static IntentFilter makeGattUpdateIntentFilter() {
      final IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
      intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
      intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
      return intentFilter;
   }

   private final String LIST_NAME = "NAME";
   private final String LIST_UUID = "UUID";
   // Demonstrates how to iterate through the supported GATT Services/Characteristics.
   // In this sample, we populate the data structure that is bound to the ExpandableListView
   // on the UI.
   private SimpleExpandableListAdapter displayGattServices(Activity activity, List<BluetoothGattService> gattServices) {
      if (gattServices == null) return null;
      String uuid;
      String unknownServiceString = activity.getResources().getString(R.string.unknown_service);
      String unknownCharaString = activity.getResources().getString(R.string.unknown_characteristic);
      ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
      ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
              = new ArrayList<ArrayList<HashMap<String, String>>>();
      mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

      // Loops through available GATT Services.
      for (BluetoothGattService gattService : gattServices)
      {
         HashMap<String, String> currentServiceData = new HashMap<String, String>();
         uuid = gattService.getUuid().toString();
         currentServiceData.put(
                 LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
         currentServiceData.put(LIST_UUID, uuid);
         gattServiceData.add(currentServiceData);

         ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                 new ArrayList<HashMap<String, String>>();
         List<BluetoothGattCharacteristic> gattCharacteristics =
                 gattService.getCharacteristics();
         ArrayList<BluetoothGattCharacteristic> charas =
                 new ArrayList<BluetoothGattCharacteristic>();

         // Loops through available Characteristics.
         for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
         {
            charas.add(gattCharacteristic);
            HashMap<String, String> currentCharaData = new HashMap<String, String>();
            uuid = gattCharacteristic.getUuid().toString();
            currentCharaData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
            currentCharaData.put(LIST_UUID, uuid);
            gattCharacteristicGroupData.add(currentCharaData);
         }
         mGattCharacteristics.add(charas);
         gattCharacteristicData.add(gattCharacteristicGroupData);
      }
      return new SimpleExpandableListAdapter(
              activity,
              gattServiceData,
              android.R.layout.simple_expandable_list_item_2,
              new String[] {LIST_NAME, LIST_UUID},
              new int[] { android.R.id.text1, android.R.id.text2 },
              gattCharacteristicData,
              android.R.layout.simple_expandable_list_item_2,
              new String[] {LIST_NAME, LIST_UUID},
              new int[] { android.R.id.text1, android.R.id.text2 }
      );
   }
}
