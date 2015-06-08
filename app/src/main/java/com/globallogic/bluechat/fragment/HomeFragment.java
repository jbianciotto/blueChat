package com.globallogic.bluechat.fragment;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.globallogic.bluechat.R;
import com.globallogic.bluechat.activity.HomeActivity;
import com.globallogic.bluechat.adapter.DeviceAdapter;
import com.globallogic.bluechat.interfaces.BTManager;
import com.globallogic.bluechat.manager.BLEMgr;
import com.globallogic.bluechat.manager.BluetoothMgr;
import com.globallogic.bluechat.task.listenConnectionsTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * A placeholder fragment containing a simple view.
 */

public class HomeFragment extends Fragment {

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    BTManager mBluetoothMgr;
    private BroadcastReceiver mReceiver;
    private View mView;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private DeviceAdapter mDeviceAdapter;

    public HomeFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        final int REQUEST_ENABLE_BT = 20;

        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothMgr.isDisabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBT();
    }

    public void startBT() {
        if(mBluetoothMgr != null) {
            mBluetoothMgr.stopDiscovery();
        }

        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mDeviceAdapter.add(device);
                }
            }
        };

        mBluetoothMgr = new BluetoothMgr(getActivity());

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        new listenConnectionsTask().execute((HomeActivity) getActivity());
    }

    @TargetApi(21)
    public void startBLE() {
        if(mBluetoothMgr != null) {
            mBluetoothMgr.stopDiscovery();
        }

        ScanCallback callback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                getActivity().runOnUiThread(new deviceReceiver(result));
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        mBluetoothMgr = new BLEMgr(getActivity(), callback);
    }

    public void onBondedSearch() {
        mBluetoothMgr.stopDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothMgr.getBTAdapter().getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mDeviceAdapter.add(device);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void startDiscovery() {
        mDeviceAdapter.clear();
        Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
        cancelButton.setEnabled(true);
        mBluetoothMgr.startDiscovery();
    }

    public void cancelDiscovery() {
        Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
        cancelButton.setEnabled(false);
        mBluetoothMgr.stopDiscovery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        mDeviceAdapter = new DeviceAdapter(getActivity(), mDeviceList);
//
        ListView deviceListView = (ListView) mView.findViewById(R.id.home_devices_list);
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cancelDiscovery();
                BluetoothDevice device = (BluetoothDevice) mDeviceAdapter.getItem(i);
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("96d85412-43a3-422e-92cb-1346f76ee620"));
                    socket.connect();
                    Log.d("BLUECHAT", "connection established to" + socket.getRemoteDevice().getAddress());

                } catch (IOException e) {
                    Log.d("ConnectionFragment", "IO EXCEPTION: " + e.getMessage() + "!!!!!");
                }


                ((HomeActivity) getActivity()).onConnectionEstablished(socket);
            }
        });

        Button searchButton = (Button) mView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        Button bondButton = (Button) mView.findViewById(R.id.bonded_button);
        bondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceAdapter.clear();
                onBondedSearch();
            }
        });

        Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDiscovery();
            }
        });

        Button bleButton = (Button) mView.findViewById(R.id.enable_ble_button);
        bleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBLE();
            }
        });

        Button btButton = (Button) mView.findViewById(R.id.enable_bt_button);
        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBT();
            }
        });


        return mView;
    }

    public interface Callbacks {
        public void onConnectionEstablished(BluetoothSocket socket);
    }

    @TargetApi(21)
    private class deviceReceiver implements Runnable {
        private BluetoothDevice device;

        public deviceReceiver(ScanResult result) {
            device = result.getDevice();
        }

        @Override
        public void run() {
            mDeviceAdapter.add(device);
        }
    }
}

