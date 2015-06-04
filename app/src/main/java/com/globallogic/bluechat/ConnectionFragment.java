package com.globallogic.bluechat;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ConnectionFragment extends Fragment {
    private View mView;
    private BluetoothDevice mDevice;
    private BluetoothSocket bSocket;

    private ListView chatWindow;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatList;
    private EditText messageWindow;
    private Button sendButton;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mDevice = args.getParcelable("BTDevice");

        chatList = new ArrayList<String>();

        try {
            bSocket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("96d85412-43a3-422e-92cb-1346f76ee620"));
            bSocket.connect();
        } catch (IOException e) {
            Log.d("ConnectionFragment", "IO EXCEPTION!!!!!");
        }
    }

    private void sendText(byte[] payload) {
        try {

            if(bSocket != null && bSocket.isConnected()) {
                bSocket.getOutputStream().write(payload);
                chatAdapter.add("Me: " + payload.toString());
            }
        } catch (IOException e) {
            Log.d("ConnectionFragment", "Transfer EXCEPTION!!!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_connection, container, false);
        sendButton = (Button) mView.findViewById(R.id.send_button);
        messageWindow = (EditText) mView.findViewById(R.id.msg_window);
        chatWindow = (ListView) mView.findViewById(R.id.chat_window);

        chatAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_device_list,
                R.id.device_name,
                chatList);

        chatWindow.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText(messageWindow.getText().toString().getBytes());
                messageWindow.setText("");
            }
        });



        return mView;
    }
}
