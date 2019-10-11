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
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
   private LeDeviceListAdapter mLeDeviceListAdapter;
   private BluetoothAdapter mBluetoothAdapter;
   private boolean mScanning;
   private Handler mHandler;

   private static final int REQUEST_ENABLE_BT = 1;
   // Stops scanning after 10 seconds.
   private static final long SCAN_PERIOD = 10000;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mHandler = new Handler();


//        listView = (ListView) findViewById(R.id.list_view);
      // Use this check to determine whether BLE is supported on the device.  Then you can
      // selectively disable BLE-related features.
      if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
      {
         Log.d("PEPE DEBUG", "Feature not found: " + R.string.ble_not_supported);
         finish();
      }

      // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
      // BluetoothAdapter through BluetoothManager.
      final BluetoothManager bluetoothManager =
              (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
      mBluetoothAdapter = bluetoothManager.getAdapter();

      // Checks if Bluetooth is supported on the device.
      if (mBluetoothAdapter == null)
      {
         Log.d("PEPE DEBUG", "Feature is null " + R.string.error_bluetooth_not_supported);
         finish();
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main, menu);
      if (!mScanning)
      {
         menu.findItem(R.id.menu_stop).setVisible(false);
         menu.findItem(R.id.menu_scan).setVisible(true);
         MenuItemCompat.setActionView(menu.findItem(R.id.menu_refresh), null);
      }
      else
      {
         menu.findItem(R.id.menu_stop).setVisible(true);
         menu.findItem(R.id.menu_scan).setVisible(false);
         menu.findItem(R.id.menu_refresh).setActionView(
                 R.layout.actionbar_indeterminate_progress);
      }
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId())
      {
         case R.id.menu_scan:
            mLeDeviceListAdapter.clear();
            scanLeDevice(true);
            break;
         case R.id.menu_stop:
            scanLeDevice(false);
            break;
      }
      return true;
   }

   @Override
   protected void onResume() {
      super.onResume();

      // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
      // fire an intent to display a dialog asking the user to grant permission to enable it.
      if (!mBluetoothAdapter.isEnabled())
      {
         if (!mBluetoothAdapter.isEnabled())
         {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
         }
      }

      // Initializes list view adapter.
      mLeDeviceListAdapter = new LeDeviceListAdapter();
      setListAdapter(mLeDeviceListAdapter);
      scanLeDevice(true);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // User chose not to enable Bluetooth.
      if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
      {
         finish();
         return;
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   protected void onPause() {
      super.onPause();
      scanLeDevice(false);
      mLeDeviceListAdapter.clear();
   }

   @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
      final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
      if (device == null)
      {
         return;
      }
      final Intent intent = new Intent(this, PepeControlActivity.class);
      intent.putExtra(PepeControlActivity.EXTRAS_DEVICE_NAME, device.getName());
      intent.putExtra(PepeControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
      if (mScanning)
      {
         mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
         mScanning = false;
      }
      startActivity(intent);
   }

   private void scanLeDevice(final boolean enable) {
      final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
      if (enable)
      {
         // Stops scanning after a pre-defined scan period.
         mHandler.postDelayed(() -> {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
            invalidateOptionsMenu();
         }, SCAN_PERIOD);

         mScanning = true;
         bluetoothLeScanner.startScan(mLeScanCallback);
      }
      else
      {
         mScanning = false;
         bluetoothLeScanner.stopScan(mLeScanCallback);
      }
      invalidateOptionsMenu();
   }

   // Adapter for holding devices found through scanning.
   private class LeDeviceListAdapter extends BaseAdapter {
      private ArrayList<BluetoothDevice> mLeDevices;
      private LayoutInflater mInflator;

      public LeDeviceListAdapter() {
         super();
         mLeDevices = new ArrayList<>();
         mInflator = DeviceScanActivity.this.getLayoutInflater();
      }

      public void addDevice(BluetoothDevice device) {
         if (!mLeDevices.contains(device))
         {
            mLeDevices.add(device);
         }
      }

      public BluetoothDevice getDevice(int position) {
         return mLeDevices.get(position);
      }

      public void clear() {
         mLeDevices.clear();
      }

      @Override
      public int getCount() {
         return mLeDevices.size();
      }

      @Override
      public Object getItem(int i) {
         return mLeDevices.get(i);
      }

      @Override
      public long getItemId(int i) {
         return i;
      }

      @Override
      public View getView(int i, View view, ViewGroup viewGroup) {
         ViewHolder viewHolder;
         // General ListView optimization code.
         if (view == null)
         {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = view.findViewById(R.id.device_address);
            viewHolder.deviceName = view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
         }
         else
         {
            viewHolder = (ViewHolder) view.getTag();
         }

         BluetoothDevice device = mLeDevices.get(i);
         final String deviceName = device.getName();
         if (deviceName != null && deviceName.length() > 0)
         {
            viewHolder.deviceName.setText(deviceName);
         }
         else
         {
            viewHolder.deviceName.setText(R.string.unknown_device);
         }
         viewHolder.deviceAddress.setText(device.getAddress());

         return view;
      }
   }

   private ScanCallback mLeScanCallback = new ScanCallback(){
      @Override
      public void onScanResult(int callbackType, ScanResult result) {
         super.onScanResult(callbackType, result);
         runOnUiThread(() -> {
            mLeDeviceListAdapter.addDevice(result.getDevice());
            mLeDeviceListAdapter.notifyDataSetChanged();
         });
      }

      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      public void onBatchScanResults(List<ScanResult> results) {
         super.onBatchScanResults(results);
         results.forEach(result -> runOnUiThread(() -> {
            mLeDeviceListAdapter.addDevice(result.getDevice());
            mLeDeviceListAdapter.notifyDataSetChanged();
         }));
      }

      @Override
      public void onScanFailed(int errorCode) {
         super.onScanFailed(errorCode);
      }
   };

   static class ViewHolder {
      TextView deviceName;
      TextView deviceAddress;
   }
}