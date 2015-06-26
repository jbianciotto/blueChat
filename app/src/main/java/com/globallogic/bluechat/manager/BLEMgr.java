package com.globallogic.bluechat.manager;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.globallogic.bluechat.Constants;
import com.globallogic.bluechat.activity.HomeActivity;
import com.globallogic.bluechat.interfaces.BTManager;

import java.util.UUID;

/**
 * Created by ecamarotta on 04/06/15.
 */

@TargetApi(21)
public class BLEMgr implements BTManager {
    private final UUID CHAT_SERVICE_UUID = UUID.fromString("6f37dc2a-fbfa-4964-89c7-ac6cc3deb808");
    private final UUID CHAT_CHARACT_UUID = UUID.fromString("ebf76999-173c-41ae-949b-3e8ab3c09555");
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private BluetoothGattServer mGattServer;
    private ScanCallback mListener;

    public BLEMgr(Context context, ScanCallback listener) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mListener = listener;
    }

    private AdvertiseData getAdvertisementData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeTxPowerLevel(false); // reserve advertising space for URI
        builder.addServiceUuid(ParcelUuid.fromString(CHAT_SERVICE_UUID.toString()));

        return builder.build();
    }

    private AdvertiseSettings getAdvertiseSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        builder.setConnectable(true);

        return builder.build();
    }

    @Override
    public void startServer(final Context context) {
        AdvertiseData adData = getAdvertisementData();
        AdvertiseSettings adSettings = getAdvertiseSettings();

        BluetoothGattServerCallback serverCallback = new BluetoothGattServerCallback() {
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                Log.d(Constants.LOGTAG,"Characteristic read attempt");
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                Log.d(Constants.LOGTAG, "Characteristic write attempt");
            }

            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                Log.d(Constants.LOGTAG, "Descriptor read attempt");
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                Log.d(Constants.LOGTAG, "Descriptor write attempt");
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                Log.d(Constants.LOGTAG, "Execute write attempt");
            }

            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                Log.d(Constants.LOGTAG, "Gatt server connection state has changed");
            }

        };

        BluetoothGattService service = new BluetoothGattService(
                CHAT_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                CHAT_CHARACT_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);

        //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        byte[] text = new String("test").getBytes();
        characteristic.setValue("texttttt");

       /* BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(
                CHAT_DESCR_UUID,
                BluetoothGattDescriptor.PERMISSION_READ);

        characteristic.addDescriptor(descriptor);*/
        service.addCharacteristic(characteristic);

        mGattServer = mBluetoothManager.openGattServer(context, serverCallback);

        mGattServer.addService(service);

        Log.d(Constants.LOGTAG, "Services in mGattServer:\n");
        for(BluetoothGattService srv : mGattServer.getServices()) {
            Log.d(Constants.LOGTAG, srv.getUuid() + "\n");
        }

        AdvertiseCallback callback = new AdvertiseCallback() {
            @SuppressLint("Override")
            @Override
            public void onStartSuccess(AdvertiseSettings advertiseSettings) {
                final String message = "Advertisement successful";
                ((HomeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @SuppressLint("Override")
            @Override
            public void onStartFailure(int i) {
                final String message = "Advertisement failed error code: " + i;
                ((HomeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

        };

        mBluetoothAdvertiser.startAdvertising(adSettings, adData, callback);
    }

    @Override
    public void stopServer(final Context context) {

        AdvertiseCallback callback = new AdvertiseCallback() {
            @SuppressLint("Override")
            @Override
            public void onStartSuccess(AdvertiseSettings advertiseSettings) {
                final String message = "mGattServer stopped!";
                ((HomeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @SuppressLint("Override")
            @Override
            public void onStartFailure(int i) {
                final String message = "stop failure: " + i;
                ((HomeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

        };

        mBluetoothAdvertiser.stopAdvertising(callback);
        mGattServer.close();
    }

    @Override
    public void connect(Context context, BluetoothDevice device) {

    }

    @Override
    public BluetoothAdapter getBTAdapter() {
        return mBluetoothAdapter;
    }

    @Override
    public void startDiscovery() {
        mBluetoothScanner.startScan(mListener);
    }

    @Override
    public void stopDiscovery() {
        mBluetoothScanner.stopScan(mListener);
    }
}
