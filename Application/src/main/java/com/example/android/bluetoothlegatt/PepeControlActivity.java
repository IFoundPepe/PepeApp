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

package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.android.bluetoothlegatt.InputManagerCompat.InputDeviceListener;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class PepeControlActivity extends Activity implements InputDeviceListener {
    private final static String TAG = PepeControlActivity.class.getSimpleName();

    // Input manager for Pepe control via controller
    private InputManagerCompat mInputManager;

    // Derived Values
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private JoystickView joystick;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private PepeBluetoothConnectionManager pepeManager;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // only sends data after this number of data points collected
    // intent is to reduce jitter

    private int mInterval = 10; // 0.5 seconds by default, can be changed later
    private Handler mHandler;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            pepeManager.connectTweet();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayPepeControl(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pepe_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

       pepeManager = new PepeBluetoothConnectionManager();
        //TODO: Commented out stuff that isn't displayed on the UI
        // Sets up UI references.
      //  ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
      //  mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
      //  mGattServicesList.setOnChildClickListener(servicesListClickListner);
      //  mConnectionState = (TextView) findViewById(R.id.connection_state);
      //  mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mHandler = new Handler();
        startRepeatingTask();

        joystick = (JoystickView) findViewById(R.id.joystick);

        joystick.setOnTouchListener( new JoystickView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_UP )
                {
                   pepeManager.setAngle_value(0);
                   pepeManager.setStrength_value(0);
                }
                return false;
            }

        });
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {

            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
               pepeManager.setAngle_value(angle);
               pepeManager.setStrength_value(strength);
               pepeManager.calculateLookAndLean();
            }
        });


        final Button flapButton = (Button) findViewById(R.id.flap);
        flapButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   pepeManager.flapDown();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                   v.performClick();
                   pepeManager.flapUp();
                }
                return false;
            }
        });

       final Button tweetButton = (Button) findViewById(R.id.tweet);
       tweetButton.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
             switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                   pepeManager.tweet();
                   return true;
                case MotionEvent.ACTION_UP:
                   v.performClick();
                   pepeManager.silence();
                   return true;
             }
             return false;
          }
       });
        mInputManager = InputManagerCompat.Factory.getInputManager(this);
        mInputManager.registerInputDeviceListener(this, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("PEPE DEBUG", "keyCode: " + keyCode);
        // 99/67(silence), 96/23(tweet), 100/62(flap)
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A: // press X: 96 - do nothing?
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
                return true;
            case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99 - do nothing?
                return true;
            case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
                return true;
            case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
                Log.d("PEPE DEBUG", "flap up");
                pepeManager.flapUp();
                return true;
            case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("PEPE DEBUG", "keyCode: " + keyCode);
        // 99/67(silence), 96/23(tweet), 100/62(flap)
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A: // press X: 96
                Log.d("PEPE DEBUG", "tweet");
                pepeManager.tweet();
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER: // held X: 23 - do nothing?
                return true;
            case KeyEvent.KEYCODE_BUTTON_X: // press IOS: 99
                Log.d("PEPE DEBUG", "silence/automate?");
                pepeManager.silence();
                return true;
            case KeyEvent.KEYCODE_DEL: // held IOS: 67 - do nothing?
                return true;
            case KeyEvent.KEYCODE_BUTTON_Y: // press triangle: 100
                Log.d("PEPE DEBUG", "flap down");
                pepeManager.flapDown();
                return true;
            case KeyEvent.KEYCODE_SPACE: // held triangle 62 - do nothing?
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                sendUpdatedPositionData(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        stopRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: Commented out due to doesn't exist
//                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
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
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
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

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private void displayPepeControl(List<BluetoothGattService> gattServices)  {

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private boolean sendUpdatedPositionData() {

        String data = pepeManager.generateData();
        if((mBluetoothLeService != null) && pepeManager.sendIt()) {
            Log.d("PEPE DEBUG", "Data: " + data);
            mBluetoothLeService.sendData(data);
        }

        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d("PEPE DEBUG", "Generic motion event occured!");
        mInputManager.onGenericMotionEvent(event);

        int eventSource = event.getSource();
        int pointerIndex = event.getPointerCount() - 1; // since we only care about the last location of the motion
        Log.d("PEPE EVENT", "to string: " + event.toString());
        Log.d("PEPE EVENT", "actionToString: " + event.actionToString(event.getAction()));
        int id = event.getDeviceId();
        if (-1 != id) {

            int pointerCount = event.getPointerCount();
            System.out.printf("At time %d:", event.getEventTime());
            for (int p = 0; p < pointerCount; p++) {
                // Copy/pasta from sendUpdatedPositionData
                Log.d("PEPE DEBUG", "Controller data");
                pepeManager.joystickLook(event.getY(p)); // -0.88 to 1.0
                if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == 1.0) {
                    pepeManager.leanBack();
                }

                if (event.getAxisValue(MotionEvent.AXIS_HAT_X, p) == -1.0) {
                    pepeManager.leanForward();
                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        Log.d("PEPE DEBUG", "Input device " + deviceId + " has been added...");
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        Log.d("PEPE DEBUG", "Input device " + deviceId + " has been changed...");
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        Log.d("PEPE DEBUG", "Input device " + deviceId + " has been removed...");
    }
}
