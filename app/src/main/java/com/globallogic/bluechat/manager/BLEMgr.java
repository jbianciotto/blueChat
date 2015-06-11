package com.globallogic.bluechat.manager;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.util.Log;

import com.globallogic.bluechat.Constants;
import com.globallogic.bluechat.interfaces.BTManager;

import java.util.ArrayList;

/**
 * Created by ecamarotta on 04/06/15.
 */

@TargetApi(21)
public class BLEMgr implements BTManager {
    BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBlueToothScanner;
    private BluetoothGattServer mServer;
    private ScanCallback mListener;
    private ArrayList<BluetoothGattService> services;

    public BLEMgr(Context context, ScanCallback listener) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBlueToothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        services = new ArrayList<BluetoothGattService>();
        mListener = listener;
    }



    @Override
    public void startServer(Context context) {

        BluetoothGattServerCallback callback = new BluetoothGattServerCallback() {

            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                Log.i(Constants.LOGTAG, "onConnectionStateChange" + status + " " + newState);
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            }
        };

        Log.i(Constants.LOGTAG, "Starting BLE Server");
        mServer = mBluetoothManager.openGattServer(context, callback);
        Log.i(Constants.LOGTAG, "BLE Server Started");


        for(int i = 0; i < services.size(); i++) {
            mServer.addService(services.get(i));
        }
    }

    @Override
    public void stopServer(Context context) {
        mServer.close();
    }

    public void addService(BluetoothGattService service) {
        services.add(service);
    }

    @Override
    public void connect(Context context, BluetoothDevice device) {
        BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                // this will get called anytime you perform a read or write characteristic operation
                Log.i(Constants.LOGTAG, "OnCharacteristicChanged");
            }

            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                // this will get called when a device connects or disconnects
                Log.i(Constants.LOGTAG, "OnConnectionStateChange");
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                // this will get called after the client initiates a BluetoothGatt.discoverServices() call
                Log.i(Constants.LOGTAG, "onServicesDiscovered");
            }
        };

        BluetoothGatt bluetoothGatt = device.connectGatt(context, true, btleGattCallback);
    }

    @Override
    public BluetoothAdapter getBTAdapter() {
        return mBluetoothAdapter;
    }

    @Override
    public void startDiscovery() {
        mBlueToothScanner.startScan(mListener);
    }

    @Override
    public void stopDiscovery() {
        mBlueToothScanner.stopScan(mListener);
    }
}
