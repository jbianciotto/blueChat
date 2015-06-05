package com.globallogic.bluechat.fragment;

import android.support.v4.app.Fragment;
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

import com.globallogic.bluechat.R;

import java.io.IOException;
import java.util.ArrayList;

public class ConnectionFragment extends Fragment {
    private View mView;
    private BluetoothDevice mDevice;
    private BluetoothSocket bSocket;
    private ListView chatWindow;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatList = new ArrayList<String>();
    private EditText messageWindow;
    private Button sendButton;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    public void setbSocket(BluetoothSocket bSocket) {
        this.bSocket = bSocket;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (bSocket != null && bSocket.isConnected()) {
            new Thread(new socketRead(bSocket)).start();
            Log.d("ConnectionFragment", "AsyncTask Launched");
        }
    }

    private void sendText(byte[] payload) {
        try {

            if(bSocket != null && bSocket.isConnected()) {
                bSocket.getOutputStream().write(payload);
                chatAdapter.add("Me: " + new String(payload));
                chatWindow.smoothScrollToPosition(chatAdapter.getCount() - 1);
            }else {
                Log.d("ConnectionFragment", "Socket está cerrado!!!!");
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

    private class socketRead implements Runnable {
        BluetoothSocket mSocket;

        public socketRead(BluetoothSocket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[255];
            int bytes;
            String inputMessage;

            Log.d("ConnectionFragment", "AsyncTask started");

            while(true) {
                try {
                    bytes = mSocket.getInputStream().read(buffer);
                    inputMessage = new String(buffer).substring(0, bytes);
                    Log.d("ConnectionFragment", "I read " + bytes + " bytes: " + inputMessage);
                    mView.post(new updateUI(inputMessage));
                } catch (IOException readException) {
                    Log.d("ConnectionFragment", "Exception: " + readException.getMessage());
                    try {
                        mSocket.close();
                    } catch (IOException closeException) {
                        Log.d("ConnectionFragment", "Input socket couldn't have been closed");
                    }
                }
            }
        }

        private class updateUI implements Runnable {
            String message;

            public updateUI(String message) {
                this.message = message;
            }

            @Override
            public void run() {
                chatAdapter.add(mSocket.getRemoteDevice().getName() + ": " + message);
                chatWindow.smoothScrollToPosition(chatAdapter.getCount() - 1);
            }
        }
    }
}