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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.controlwear.virtual.joystick.android.JoystickView;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class PepeControlActivity extends Activity {
    private final static String TAG = PepeControlActivity.class.getSimpleName();

    // Settings
    private static final int MAX_SERVO_LOOK = 550;
    private static final int MIN_SERVO_LOOK = 130;

    // Only want up to 90 degrees for the max lean
    private static final int MAX_SERVO_LEAN = 350;
    private static final int MIN_SERVO_LEAN = 130;
    private static final int STRENGTH_JOYSTICK_LEAN = 40;

    // Derived Values
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // Derived for the look
    private static final int DEFAULT_LOOK = ((MAX_SERVO_LOOK + MIN_SERVO_LOOK) / 2 );
    private static final int STEPS = 6;
    private static final int STEP_SIZE = (MAX_SERVO_LOOK - MIN_SERVO_LOOK) / STEPS;
    private static final int NORM = (MAX_SERVO_LOOK - MIN_SERVO_LOOK)/2;
    private static final int MEAN_LOOK = (MAX_SERVO_LOOK + MIN_SERVO_LOOK)/2;

    // Derived for the lean
    private static final int MEAN_LEAN = (MAX_SERVO_LEAN + MIN_SERVO_LEAN) / 2;

//    private static final int THRESHOLD_1 = ((MAX_SERVO_LOOK - DEFAULT_LOOK) / 2)  + DEFAULT_LOOK;
//    private static final int THRESHOLD_2 = ((DEFAULT_LOOK - MIN_SERVO_LOOK) / 2)  + MIN_SERVO_LOOK;

    private JoystickView joystick;
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    public int strength_value;
    private int angle_value;
    private boolean sendIt = false;
    private int previousLook = 0;
    private int   look = 128;
    private int previousLean = 0;
    private int   lean = 0;
    private int previousFlap = 0;
    private int   flap = 0;
    private int previousTweet = 0;
    private int  tweet = 0;

    private int lookCount = 0;

    // only sends data after this number of data points collected
    // intent is to reduce jitter`
    private int dataLimiter = 5;

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
                    angle_value = 0;
                    strength_value = 0;
                }
                return false;
            }

        });
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {

            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                angle_value = angle;
                strength_value = strength;
            }
        });

        // perform seek bar change listener event used for getting the progress value
        final Button flapButton = (Button) findViewById(R.id.flap);
        flapButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(PepeControlActivity.this, "Flap up", Toast.LENGTH_SHORT).show();
                    flap = 1;
//                    sendUpdatedPositionData();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {

                    Toast.makeText(PepeControlActivity.this, "Flap down", Toast.LENGTH_SHORT).show();
                    flap = 0;
//                    sendUpdatedPositionData();
                }
                return false;
            }
        });

        final Button tweetButton = (Button) findViewById(R.id.tweet);
        tweetButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(PepeControlActivity.this, "Tweet on", Toast.LENGTH_SHORT).show();
                    tweet = 1;
//                    sendUpdatedPositionData();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(PepeControlActivity.this, "Tweet off", Toast.LENGTH_SHORT).show();
                    tweet = 0;
//                    sendUpdatedPositionData();
                }
                return false;
            }
        });
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



    private int threshold = MAX_SERVO_LOOK;
    private boolean sendUpdatedPositionData() {

//        threshold = MAX_SERVO_LOOK;
        double distance = Math.cos(angle_value * (Math.PI/180));
        double value = NORM * distance;
        look = (int) (MEAN_LOOK + value);

        if ( (angle_value == 0) && (strength_value == 0) )
        {
            look = DEFAULT_LOOK;
        }

        if ( (angle_value > 45) && (angle_value < 135) )
        {
            // Forward Tilt
            if ( strength_value > STRENGTH_JOYSTICK_LEAN  ) {
                lean = MAX_SERVO_LEAN;
            }
        }
        else if ( ( angle_value > 225) && (angle_value < 325) )
        {
            // Backward Tilt
            if ( strength_value > STRENGTH_JOYSTICK_LEAN ) {
                lean = MIN_SERVO_LEAN;
            }

        }

        int bin = Math.round((look - MIN_SERVO_LOOK) / STEP_SIZE);
        look = (bin * STEP_SIZE) + MIN_SERVO_LOOK;

        if ( look < MIN_SERVO_LOOK)
        {
            look = MIN_SERVO_LOOK;
        }

        if ( lean > MEAN_LEAN )
        {
            lean = MAX_SERVO_LEAN;
        }
        else // if ( lean < MEAN_LEAN )
        {
            lean = MIN_SERVO_LEAN;
        }

        sendIt = false;
        if ( (previousLook != look) ||
             (previousLean != lean) ||
             (previousFlap != flap) ||
             (previousTweet != tweet))
        {
            sendIt = true;
        }

        previousLook = look;
        previousLean = lean;
        previousFlap = flap;
        previousTweet = tweet;

        //Write to the Bluetooth service transmit characteristic
        //Data transmit interface:
        //      ! == look
        //      @ == lean
        //      $ == flap
        //      # == tweet (see what I did there ;) )
        if((mBluetoothLeService != null) && sendIt) {
            mBluetoothLeService.sendData( look + "|" +
                                          lean + "|" +
                                          flap + "|" +
                                          tweet + "%");
        }

        return false;
    }
}
