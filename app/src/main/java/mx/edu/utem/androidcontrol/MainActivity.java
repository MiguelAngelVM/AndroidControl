package mx.edu.utem.androidcontrol;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;

public class MainActivity extends Activity  implements SensorEventListener{

    protected PowerManager.WakeLock wakelock;
    public static final String TAG = "bluetooth";


    ImageView imViewAndroid;
    public static Handler handler;

    static final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    public BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    public boolean connect = false;
    Button btnPower;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "F8:E0:79:B5:88:5A"; // 98:D3:31:30:3A:38


    static private ConnectedThread mConnectedThread;
    public static ConnectedThread getmConnectedThread() {
        return mConnectedThread;
    }
    private static Orientation orientation = new Orientation();

    public static Orientation getOrientation() {
        return orientation;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        imViewAndroid = (ImageView) findViewById(R.id.imageVolante);


        btnPower  = (Button) this.findViewById(R.id.power);
        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());

                        }
                        break;
                }
            };
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        SensorManager sensorManager=(SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometro=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(
                this, accelerometro, SensorManager.SENSOR_DELAY_FASTEST);
        super.onRestart();
        new MiAssyncTask().execute();
        orientation.setDestroy(true);

    }



    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        getmConnectedThread().write("9");
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        try {
            getmConnectedThread().write("0");
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getOrientation().setDestroy(false);
        this.wakelock.release();
    }

    @Override
    public void onResume() {
        super.onResume();

        btnPower.setEnabled(false);
        wakelock.acquire();

        Log.d(TAG, "...onResume - try connect...");
         BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        btAdapter.cancelDiscovery();
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            connect = true;
            } catch (IOException e) {
            try {
                connect = false;
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        new MiAssyncTask().execute();
        orientation.setDestroy(true);

        if (connect == true)
        {
            btnPower.setText("Conectado");
            btnPower.setEnabled(false);
            getmConnectedThread().write("9");
        }else
        {
            btnPower.setText("Conectar");
            btnPower.setEnabled(true);
        }




    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        getmConnectedThread().write("0");
        getmConnectedThread().write("5");
        getmConnectedThread().write("6");
        getmConnectedThread().write("7");
        getmConnectedThread().write("8");
        try     {
            btSocket.close();
            getOrientation().setDestroy(false);
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }

    }

    private void checkBTState() {

        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        getOrientation().setX(event.values[0]);
        getOrientation().setY(event.values[1]);
        getOrientation().setZ(event.values[2]);
        getOrientation().setA(Math.sqrt(getOrientation().getX() * getOrientation().getX() + getOrientation().getY() *
                getOrientation().getY() + getOrientation().getZ() * getOrientation().getZ()));
        if (getOrientation().getA()> getOrientation().getAmax())
            getOrientation().setAmax(getOrientation().getA());


    }

    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        this.wakelock.release();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }
    public void power(View view)
    {
        onResume();
    }



}