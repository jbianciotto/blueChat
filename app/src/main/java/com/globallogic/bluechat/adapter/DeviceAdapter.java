package com.globallogic.bluechat.adapter;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.globallogic.bluechat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbianciotto on 03/06/15.
 */
public class DeviceAdapter extends ArrayAdapter {
    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> values) {
        super(context, 0, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;

        BluetoothDevice device = (BluetoothDevice) getItem(position);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_device_list, parent, false);
        tv = ((TextView) v.findViewById(R.id.device_name));

        tv.setText(device.getName()+"@"+device.getAddress());
        return v;
    }
}
