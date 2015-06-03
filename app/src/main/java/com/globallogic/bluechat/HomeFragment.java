package com.globallogic.bluechat;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private BroadcastReceiver mReceiver;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private View mView;
    private ArrayAdapter<String> mDeviceList;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public HomeFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        final int REQUEST_ENABLE_BT = 20;

        mBluetoothManager = (BluetoothManager) getActivity().
                getSystemService(getActivity().BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();


        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mDeviceList.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }

    public void onBondedSearch() {
        mBluetoothAdapter.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_main, container, false);

        mDeviceList = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_device_list,
                R.id.device_name,
                new ArrayList<String>()
        );

        ListView deviceList = (ListView) mView.findViewById(R.id.home_devices_list);
        deviceList.setAdapter(mDeviceList);

        Button searchButton = (Button) mView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceList.clear();
                mBluetoothAdapter.startDiscovery();
            }
        });

        Button bondButton = (Button) mView.findViewById(R.id.bonded_button);
        bondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceList.clear();
                onBondedSearch();


            }
        });

        return mView;
    }
}
