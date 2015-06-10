package com.globallogic.bluechat.fragment;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.globallogic.bluechat.R;
import com.globallogic.bluechat.activity.HomeActivity;
import com.globallogic.bluechat.adapter.DeviceAdapter;
import com.globallogic.bluechat.interfaces.BTManager;
import com.globallogic.bluechat.manager.BLEMgr;
import com.globallogic.bluechat.manager.BluetoothMgr;
import com.globallogic.bluechat.manager.OLDBLEMgr;
import com.globallogic.bluechat.task.listenConnectionsTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * A placeholder fragment containing a simple view.
 */

public class HomeFragment extends Fragment implements BluetoothAdapter.LeScanCallback {
    final int REQUEST_ENABLE_BT = 20;

    private View mView;
    private BTManager mBluetoothMgr;
    private BroadcastReceiver mReceiver;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();
    private DeviceAdapter mDeviceAdapter;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager bluetoothManager = (android.bluetooth.BluetoothManager)
                getActivity().getSystemService(getActivity().BLUETOOTH_SERVICE);

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startBT(); //Default adapter
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT && resultCode == getActivity().RESULT_CANCELED ) {
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mBluetoothMgr != null) {
            new listenConnectionsTask().execute((HomeActivity) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        mDeviceAdapter = new DeviceAdapter(getActivity(), mDeviceList);

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

                if(socket.isConnected()) {
                    ((HomeActivity) getActivity()).onConnectionEstablished(socket);
                } else {
                    Toast error = Toast.makeText(getActivity(), "No listening socket", Toast.LENGTH_LONG);
                    error.show();
                }
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

    public void startBT() {
        cancelDiscovery();

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
    }

    @TargetApi(21)
    public void startBLE() {
        cancelDiscovery();

        if(android.os.Build.VERSION.SDK_INT >= 21) {
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
        } else {
            mBluetoothMgr = new OLDBLEMgr(getActivity(), this);
        }
    }

    public void onBondedSearch() {
        if(mBluetoothMgr != null) {
            mBluetoothMgr.stopDiscovery();
            Set<BluetoothDevice> pairedDevices = mBluetoothMgr.getBTAdapter().getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    addDevice(device);
                }
            }
        }
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
            addDevice(device);
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addDevice(bluetoothDevice);
            }
        });
    }


    private void addDevice(BluetoothDevice device) {
        if(!mDeviceList.contains(device)){
            mDeviceAdapter.add(device);
        }
    }

    private void startDiscovery() {
        //Ten-second scan limit
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                  cancelDiscovery();
            }
        }, 10000);

        cancelDiscovery();

        mDeviceAdapter.clear();
        Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
        cancelButton.setEnabled(true);
        mBluetoothMgr.startDiscovery();
    }

    public void cancelDiscovery() {
        if(mBluetoothMgr != null) {
            Button cancelButton = (Button) mView.findViewById(R.id.cancel_button);
            cancelButton.setEnabled(false);
            mBluetoothMgr.stopDiscovery();
        }
    }
}

