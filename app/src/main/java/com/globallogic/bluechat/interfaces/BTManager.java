package com.globallogic.bluechat.interfaces;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by ecamarotta on 04/06/15.
 */

public interface BTManager {
    public BluetoothAdapter getBTAdapter();
    public void startDiscovery();
    public void stopDiscovery();
    public void connect(Context context, BluetoothDevice device);
    public void startServer(Context context);
    public void stopServer(Context context);
}
