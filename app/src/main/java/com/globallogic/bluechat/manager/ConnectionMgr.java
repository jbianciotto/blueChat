package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.HashMap;

/**
 * Created by jbianciotto on 04/06/15.
 */
public abstract class ConnectionMgr {
    public static HashMap<String, BluetoothSocket> connections = new HashMap();

    public static void registerConnection(BluetoothSocket socket) {
        connections.put(socket.getRemoteDevice().getAddress(), socket);
    }

    public static BluetoothSocket getConnection(String address) {
        return connections.get (address);
    }
}
