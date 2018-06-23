/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pepedyne.pepe.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothCallbackInf;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProvider;
import com.pepedyne.pepe.bluetoothlegatt.BluetoothLeServiceProviderImpl;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity implements BluetoothCallbackInf {
   private final static String TAG = DeviceControlActivity.class.getSimpleName();

   public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
   public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

   private BluetoothLeServiceProvider bluetoothLeServiceProvider;
   private TextView mConnectionState;
   private TextView mDataField;
   private String mDeviceName;
   private String mDeviceAddress;
   private ExpandableListView mGattServicesList;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.gatt_services_characteristics);

      final Intent intent = getIntent();
      mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
      mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

      bluetoothLeServiceProvider = new BluetoothLeServiceProviderImpl(this);
      bluetoothLeServiceProvider.registerCallback(this);

      // Sets up UI references.
      ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
      mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
      mGattServicesList.setOnChildClickListener(bluetoothLeServiceProvider.getListener());
      mConnectionState = (TextView) findViewById(R.id.connection_state);
      mDataField = (TextView) findViewById(R.id.data_value);

      getActionBar().setTitle(mDeviceName);
      getActionBar().setDisplayHomeAsUpEnabled(true);
      bluetoothLeServiceProvider.onCreate(this);
   }

   @Override
   protected void onResume() {
      super.onResume();
      bluetoothLeServiceProvider.onResume(this);
   }

   @Override
   protected void onPause() {
      super.onPause();
      bluetoothLeServiceProvider.onPause(this);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      bluetoothLeServiceProvider.onDestroy(this);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.gatt_services, menu);
      if (bluetoothLeServiceProvider.isConnected())
      {
         menu.findItem(R.id.menu_connect).setVisible(false);
         menu.findItem(R.id.menu_disconnect).setVisible(true);
      }
      else
      {
         menu.findItem(R.id.menu_connect).setVisible(true);
         menu.findItem(R.id.menu_disconnect).setVisible(false);
      }
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId())
      {
         case R.id.menu_connect:
            bluetoothLeServiceProvider.connect(mDeviceAddress);
            return true;
         case R.id.menu_disconnect:
            bluetoothLeServiceProvider.disconnect();
            return true;
         case android.R.id.home:
            onBackPressed();
            return true;
      }
      return super.onOptionsItemSelected(item);
   }

   // Callback Methods
   @Override
   public void updateConnectionState(final int resourceId) {
      runOnUiThread(new Runnable() {
         @Override
         public void run() {
            mConnectionState.setText(resourceId);
         }
      });
   }

   @Override
   public void clearUserInterface() {
      mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
      mDataField.setText(R.string.no_data);
   }

   @Override
   public void displayData(String data) {
      if (data != null)
      {
         mDataField.setText(data);
      }
   }

   @Override
   public void setServiceList(SimpleExpandableListAdapter adapter) {
      if (adapter != null)
      {
         mGattServicesList.setAdapter(adapter);
      }
   }

   @Override
   public void onConnect()
   {

   }


}
