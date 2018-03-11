package com.example.shado.smartsystems;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_ENABLE_BT = 1;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address, direction;
    BluetoothSocket btSocket;

    private Handler mHandler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
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

        //region TouchListeners
        //------------------------------------------------------------------------------------------

        btnForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                {
                    direction = "forward";
                    mHandler.removeCallbacks(mUpdateTask);
                    mHandler.postAtTime(mUpdateTask,SystemClock.uptimeMillis()+100);
                }
                else if(action == MotionEvent.ACTION_UP)
                {
                    mHandler.removeCallbacks(mUpdateTask);
                }
                return false;
            }
        });

        btnBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                {
                    direction = "backward";
                    mHandler.removeCallbacks(mUpdateTask);
                    mHandler.postAtTime(mUpdateTask,SystemClock.uptimeMillis()+100);
                }
                else if(action == MotionEvent.ACTION_UP)
                {
                    mHandler.removeCallbacks(mUpdateTask);
                }
                return false;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                {
                    direction = "left";
                    mHandler.removeCallbacks(mUpdateTask);
                    mHandler.postAtTime(mUpdateTask,SystemClock.uptimeMillis()+100);
                }
                else if(action == MotionEvent.ACTION_UP)
                {
                    mHandler.removeCallbacks(mUpdateTask);

                }
                return false;
            }
        });

        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN)
                {
                    direction = "right";
                    mHandler.removeCallbacks(mUpdateTask);
                    mHandler.postAtTime(mUpdateTask,SystemClock.uptimeMillis()+100);
                }
                else if(action == MotionEvent.ACTION_UP)
                {
                    mHandler.removeCallbacks(mUpdateTask);
                }
                return false;
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

    //region Drive
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
    //endregion

    //region RunnableTask
    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            switch (direction){
                case "forward":
                    mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
                    DriveForward();
                    break;
                case "backward":
                    mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
                    DriveBackward();
                    break;
                case "left":
                    mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
                    DriveLeft();
                    break;
                case "right":
                    mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
                    DriveRight();
                    break;
            }
        }
    };
    //endregion
}

