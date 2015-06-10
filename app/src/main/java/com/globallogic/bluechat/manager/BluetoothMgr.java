package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.globallogic.bluechat.interfaces.BTManager;

/**
 * Created by ecamarotta on 04/06/15.
 */
public class BluetoothMgr implements BTManager {

    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothMgr(Context context) {
        BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager)
                context.getSystemService(context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public BluetoothAdapter getBTAdapter() {
        return mBluetoothAdapter;
    }

    @Override
    public void startDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void stopDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }
}
