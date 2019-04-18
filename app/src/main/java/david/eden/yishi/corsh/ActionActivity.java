package david.eden.yishi.corsh;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ioteratech.blueterasdk.BlueteraCallbacks;
import com.ioteratech.blueterasdk.BlueteraManager;
import com.ioteratech.blueterasdk.data.Quaternion;
import com.ioteratech.blueterasdk.data.Vector3D;

public class ActionActivity extends AppCompatActivity implements BlueteraCallbacks {

    private BlueteraManager mBlueteraManager;
    private TextView mStatusTextView;
    private Button mConnectButton;
    private Button mDisconnectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);


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

    @Override
    public void onAccelerometerData(BluetoothDevice bluetoothDevice, int i, Vector3D vector3D) {

    }

    @Override
    public void onQuaternionData(BluetoothDevice bluetoothDevice, int i, Quaternion quaternion) {

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
        //mBlueteraManager.enableQuaternion(true);
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
}
