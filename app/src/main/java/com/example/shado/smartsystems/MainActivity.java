package com.example.shado.smartsystems;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_ENABLE_BT = 1;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address;
    BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button btnForward = (Button)findViewById(R.id.btnForward);
        Button btnBackward = (Button)findViewById(R.id.btnBackward);
        Button btnLeft = (Button)findViewById(R.id.btnLeft);
        Button btnRight = (Button)findViewById(R.id.btnRight);


        //region Enabling Bluetooth
        if(!myBluetoothAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        }
        //endregion

        //region Getting address paired devices
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                if(device.getName().equals("HC-05")){
                    address = device.getAddress();
                    Log.d("Address", address);
                }
            }
        }
        //endregion

        //region Connecting to device
        BluetoothDevice HC05 = myBluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
        try {
            btSocket = HC05.createInsecureRfcommSocketToServiceRecord(myUUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            Log.d("Connection", "SUCCES!");
        } catch (IOException e) {
            Log.d("Connection", e.toString());
        }
        //endregion

        //region ClickListeners
        //------------------------------------------------------------------------------------------
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveForward();
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveBackward();
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveLeft();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveRight();
            }
        });

        //endregion
    }

    private void DriveForward(){
        try
        {
            btSocket.getOutputStream().write('0');
        }
        catch (IOException e)
        {
            Log.d("Error", "failed");
        }
    }

    private void DriveBackward(){
        try
        {
            btSocket.getOutputStream().write('1');
        }
        catch (IOException e)
        {
            Log.d("Error","failed");
        }
    }

    private void DriveLeft(){
        try
        {
            btSocket.getOutputStream().write('2');
        }
        catch (IOException e)
        {
            Log.d("Error","failed");
        }
    }

    private void DriveRight(){
        try
        {
            btSocket.getOutputStream().write('3');
        }
        catch (IOException e)
        {
            Log.d("Error","failed");
        }
    }
}

