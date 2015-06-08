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
        BluetoothManager mBluetoothManager = (android.bluetooth.BluetoothManager)
                context.getSystemService(context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public BluetoothAdapter getBTAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isDisabled() {
        return mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled();
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
