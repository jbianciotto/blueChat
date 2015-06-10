package com.globallogic.bluechat.manager;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;


import com.globallogic.bluechat.interfaces.BTManager;

/**
 * Created by ecamarotta on 04/06/15.
 */

@TargetApi(21)
public class BLEMgr implements BTManager {
    private ScanCallback mListener;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBlueToothScanner;

    public BLEMgr(Context context, ScanCallback listener) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter =  bluetoothManager.getAdapter();
        mBlueToothScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mListener = listener;
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
