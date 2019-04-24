package david.eden.yishi.corsh;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ioteratech.blueterasdk.BlueteraCallbacks;
import com.ioteratech.blueterasdk.BlueteraManager;
import com.ioteratech.blueterasdk.data.Quaternion;
import com.ioteratech.blueterasdk.data.Vector3D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActionActivity extends AppCompatActivity implements BlueteraCallbacks, View.OnClickListener {

    private BlueteraManager mBlueteraManager;
    private TextView mStatusTextView;
    private Button mConnectButton;
    private Button mDisconnectButton;
    private Button btn1 ,btn2,btn3,btn4,btn5,btn6,btn7;
    private String data;
    private Quaternion CurentQuaternion;
    private Vector3D Accelerometer;
    FileOutputStream out;
    InputStream in;
    String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        btn5 = (Button)findViewById(R.id.btn5);
        btn6 = (Button)findViewById(R.id.btn6);
        btn7 = (Button)findViewById(R.id.btn7);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);

        mBlueteraManager = new BlueteraManager(getApplication());
        mBlueteraManager.setGattCallbacks(this);



//        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBlueteraManager.disconnect().enqueue();
//            }
//        });

        connecting ();
    }


    public void writedata(Button btn){
        for (int i = 0;i<50;i++){
            data+="\n"+btn.getText()+"Quaternion: W = "+String.valueOf(CurentQuaternion.getW())+" X = "+String.valueOf(CurentQuaternion.getX())
                    +" Y = "+String.valueOf(CurentQuaternion.getY())+" Z = "+String.valueOf(CurentQuaternion.getZ())+
                    " Accelerometer: X = "+String.valueOf(Accelerometer.getX())+" Y = "+String.valueOf(Accelerometer.getY())+" Z = "+String.valueOf(Accelerometer.getZ());
        }
        Toast.makeText(this, "סיים", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccelerometerData(BluetoothDevice bluetoothDevice, int i, Vector3D vector3D) {

        this.Accelerometer = vector3D;
    }

    @Override
    public void onQuaternionData(BluetoothDevice bluetoothDevice, int i, Quaternion quaternion) {
        Log.i("tag", "X" + quaternion.getX());
        Log.i("tag", "Y" + quaternion.getY());
        Log.i("tag", "Z" + quaternion.getZ());
        this.CurentQuaternion = quaternion;

    }

    @Override
    public void onUARTData(BluetoothDevice bluetoothDevice, byte[] bytes) {

    }

    @Override
    public void onDeviceConnecting(@NonNull BluetoothDevice device) {
        //mStatusTextView.setText("Connecting...");
       // mConnectButton.setEnabled(false);
    }

    @Override
    public void onDeviceConnected(@NonNull BluetoothDevice device) {
       // mStatusTextView.setText("Connected");
    }

    @Override
    public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
        //mStatusTextView.setText("Disconnecting...");
    }

    @Override
    public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
        //mStatusTextView.setText("Disconnected");
       // mConnectButton.setEnabled(true);
       // mDisconnectButton.setEnabled(false);
    }

    @Override
    public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
        //mStatusTextView.setText("Link lost");
        //mConnectButton.setEnabled(true);
    }

    @Override
    public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
        //mStatusTextView.setText("Services Discovered");
    }

    @Override
    public void onDeviceReady(@NonNull BluetoothDevice device) {

        Log.d("tag", "device ready");
        //mStatusTextView.setText("Device Ready");
        mBlueteraManager.enableAccelerometer(true);
        mBlueteraManager.enableQuaternion(true);
        //mDisconnectButton.setEnabled(true);
    }

    @Override
    public void onBondingRequired(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onBonded(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onBondingFailed(@NonNull BluetoothDevice device) {

    }

    @Override
    public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
        //mStatusTextView.setText("Error: " + message);
       // mConnectButton.setEnabled(true);
    }

    @Override
    public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
        //mStatusTextView.setText("Device not supported");
        //mConnectButton.setEnabled(true);
    }

    public void connecting () {
        Intent in = getIntent();
        BluetoothDevice d = (BluetoothDevice) in.getExtras().get("device");
        mBlueteraManager.connect(d)
                .retry(3, 100)
                .useAutoConnect(false)
                .enqueue();

    }

    @Override
    public void onClick(View v) {
        if (v == btn7) {

                 str = this.data;

                 File file = new File(Environment.DIRECTORY_DOWNLOADS,"cycare.txt");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(str.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(v==btn1){
            writedata(btn1);
        }
        else if(v==btn2){
            writedata(btn2);
        }
        else if(v==btn3){
            writedata(btn3);
        }
        else if(v==btn4){
            writedata(btn4);
        }
        else if(v==btn5){
            writedata(btn5);
        }
        else if(v==btn6){
            writedata(btn6);
        }

    }
}
