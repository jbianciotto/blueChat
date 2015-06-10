package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.globallogic.bluechat.Constants;
import com.globallogic.bluechat.interfaces.BTManager;

import java.util.ArrayList;

/**
 * Created by ecamarotta on 04/06/15.
 */

public class OLDBLEMgr implements BTManager {
    private BluetoothAdapter.LeScanCallback mListener;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothGattService> services;
    private BluetoothGattServer mServer;


    public OLDBLEMgr(Context context, BluetoothAdapter.LeScanCallback listener) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        services = new ArrayList<BluetoothGattService>();

        mListener = listener;
    }

    @Override
    public void startServer(Context context) {

        BluetoothGattServerCallback callback = new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
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

        mServer = mBluetoothManager.openGattServer(context, callback);

        for(int i = 0; i < services.size(); i++) {
            mServer.addService(services.get(i));
        }
    }

    @Override
    public void stopServer(Context context) {
        mServer.close();
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
                // this will get called after the client initiates a            BluetoothGatt.discoverServices() call
                Log.i(Constants.LOGTAG, "onServicesDiscovered");
            }
        };

        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, btleGattCallback);
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