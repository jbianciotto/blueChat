package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.globallogic.bluechat.interfaces.BTManager;

/**
 * Created by ecamarotta on 04/06/15.
 */

public class OLDBLEMgr implements BTManager {
    private BluetoothAdapter.LeScanCallback mListener;
    private BluetoothAdapter mBluetoothAdapter;

    public OLDBLEMgr(Context context, BluetoothAdapter.LeScanCallback listener) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter =  bluetoothManager.getAdapter();

        mListener = listener;
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