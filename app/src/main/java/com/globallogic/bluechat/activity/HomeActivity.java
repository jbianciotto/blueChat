package com.globallogic.bluechat.activity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.globallogic.bluechat.R;
import com.globallogic.bluechat.fragment.ConnectionFragment;
import com.globallogic.bluechat.fragment.HomeFragment;


public class HomeActivity extends ActionBarActivity implements HomeFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            this.switchFragment(new HomeFragment());
        }
    }

    public void switchFragment(Fragment f, String backStackString) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).addToBackStack(backStackString).commit();
    }

    public void switchFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, f).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionEstablished (BluetoothSocket socket) {
        ConnectionFragment connFragment = new ConnectionFragment();
        connFragment.setbSocket(socket);
        Log.d("BLUECHAT", "connection established to" + socket.getRemoteDevice().getAddress());
        this.switchFragment(connFragment, "ConnectionFragment");

    }

}
