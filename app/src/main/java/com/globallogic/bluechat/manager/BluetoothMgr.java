package com.globallogic.bluechat.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.globallogic.bluechat.activity.HomeActivity;
import com.globallogic.bluechat.interfaces.BTManager;
import com.globallogic.bluechat.task.listenConnectionsTask;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by ecamarotta on 04/06/15.
 */
public class BluetoothMgr implements BTManager {

    private BluetoothAdapter mBluetoothAdapter;
    private AsyncTask<HomeActivity,Void,BluetoothSocket> mServer;

    public BluetoothMgr(Context context) {
        BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager)
                context.getSystemService(context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        mServer = new listenConnectionsTask();
    }

    @Override
    public void startServer(Context context) {
        mServer.execute((HomeActivity) context);
    }

    @Override
    public void stopServer(Context context) {
        mServer.cancel(true);
    }

    @Override
    public void connect(Context context, BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("96d85412-43a3-422e-92cb-1346f76ee620"));
            socket.connect();
            Log.d("BLUECHAT", "connection established to" + socket.getRemoteDevice().getAddress());

        } catch (IOException e) {
            Log.d("ConnectionFragment", "IO EXCEPTION: " + e.getMessage() + "!!!!!");
        }

        if (socket.isConnected()) {
            ((HomeActivity) context).onConnectionEstablished(socket);
        } else {
            Toast error = Toast.makeText(context, "No listening socket", Toast.LENGTH_LONG);
            error.show();
        }
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
