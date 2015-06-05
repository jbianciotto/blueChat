package com.globallogic.bluechat.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import com.globallogic.bluechat.activity.HomeActivity;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by jbianciotto on 04/06/15.
 */
public class listenConnectionsTask extends AsyncTask<HomeActivity,Void,BluetoothSocket> {
    HomeActivity activity = null;

    @Override
    protected BluetoothSocket doInBackground(HomeActivity... objects) {

        activity = objects[0];

        BluetoothServerSocket serverSocket = null;
        BluetoothSocket socket = null;

        try {
            serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("blueChat", UUID.fromString("96d85412-43a3-422e-92cb-1346f76ee620"));
            socket = serverSocket.accept();
            serverSocket.close();
            Log.d("BLUECHAT", "Connection accepted from" + socket.getRemoteDevice().getAddress());
        } catch (IOException e){
            try {
                Log.e("BLUECHAT", e.toString());
                serverSocket.close();
                socket.close();
            }catch (IOException closeE) {}
        }

        return socket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        activity.onConnectionAccepted(bluetoothSocket);
    }
}
