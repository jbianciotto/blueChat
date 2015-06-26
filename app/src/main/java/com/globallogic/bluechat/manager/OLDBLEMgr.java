package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.globallogic.bluechat.Constants;
import com.globallogic.bluechat.interfaces.BTManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by ecamarotta on 04/06/15.
 */

public class OLDBLEMgr implements BTManager {
    private BluetoothAdapter.LeScanCallback mListener;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothGattService> services;
    private BluetoothGatt mBluetoothGatt;


    public OLDBLEMgr(Context context, BluetoothAdapter.LeScanCallback listener) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        services = new ArrayList<BluetoothGattService>();

        mListener = listener;
    }

    @Override
    public void startServer(Context context) {
        Toast error = Toast.makeText(context, "Server mode not supported", Toast.LENGTH_LONG);
        error.show();
    }

    @Override
    public void stopServer(Context context) {

    }

    @Override
    public void connect(Context context, BluetoothDevice device) {

        BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                // this will get called anytime you perform a read or write characteristic operation
                Log.d(Constants.LOGTAG, "OnCharacteristicChanged");
            }

            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                // this will get called when a device connects or disconnects
                if (newState == BluetoothProfile.STATE_CONNECTED) Log.d(Constants.LOGTAG, "Connection status Changed to connected");
                if (newState == BluetoothProfile.STATE_DISCONNECTED) Log.d(Constants.LOGTAG, "Connection status Changed to disconnected");

                Log.d(Constants.LOGTAG, "Status "+status);
//                if(status == BluetoothGatt.GATT_SUCCESS) {
//                    Log.d(Constants.LOGTAG, "Me conecte bien!!!");
//                } else {
//                    Log.d(Constants.LOGTAG, "Me conecte mal!!!");
//                }
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                // this will get called after the client initiates a BluetoothGatt.discoverServices() call
                String services = "";

                Log.d(Constants.LOGTAG, "inside onServicesDiscovered!");

//                List<BluetoothGattService> servs = gatt.getServices();
//                Collections.reverse(servs);

                for(BluetoothGattService service : gatt.getServices()) {
                    services = services + service.getUuid() + "\n";
                    for (BluetoothGattCharacteristic charact : service.getCharacteristics()) {
                        services = services + "-----" + charact.getUuid() + "\n";
//                        if (charact.getUuid().compareTo(UUID.fromString("ebf76999-173c-41ae-949b-3e8ab3c09555")) == 0) {
//                            Log.d(Constants.LOGTAG, "reading char");
//                            gatt.readCharacteristic(charact);
//                        }
                    }
                }

                Log.d(Constants.LOGTAG, "Services:\n" + services);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.d(Constants.LOGTAG, "Charcteristic read with status "+status);
                //Log.d(Constants.LOGTAG, "Charcteristic read: "+ characteristic.getStringValue(0));
            }
        };

        mBluetoothGatt = device.connectGatt(context, false, btleGattCallback);
        if(mBluetoothGatt.discoverServices()) {
            Log.d(Constants.LOGTAG, "Discover services returned true");
        } else {
            Log.d(Constants.LOGTAG, "Discover services returned false");
        }
    }

    @Override
    public BluetoothAdapter getBTAdapter() {
        return mBluetoothAdapter;
    }

    @Override
    public void startDiscovery() {
        mBluetoothAdapter.startLeScan(mListener);
    }

    @Override
    public void stopDiscovery() {
        mBluetoothAdapter.stopLeScan(mListener);
    }
}