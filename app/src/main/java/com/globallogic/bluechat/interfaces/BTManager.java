package com.globallogic.bluechat.interfaces;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by ecamarotta on 04/06/15.
 */

public interface BTManager {
    public BluetoothAdapter getBTAdapter();
    public void startDiscovery();
    public void stopDiscovery();
}
