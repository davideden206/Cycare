package david.eden.yishi.corsh;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActionActivity extends AppCompatActivity implements BlueteraCallbacks, View.OnClickListener {

    private BlueteraManager mBlueteraManager;
    private TextView mStatusTextView;
    private Button mConnectButton;
    private Button mDisconnectButton;
    private Button btn1 ,btn2,btn3,btn4,btn5,btn6,btn7,btn8;
    private String data,data1,data2;
    static Quaternion CurentQuaternion;
    static Vector3D Accelerometer;
    FileOutputStream out;
    InputStream in;
    String str;
    boolean clickbtn =false;
    public  Quaternion invers;
    public  Quaternion curent;
    public static boolean flag=true;
    static boolean enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        curent = new Quaternion();
        invers = new Quaternion();
        if (!checkPermission()) {

        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();

            } else {

            }
        }

        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        btn5 = (Button)findViewById(R.id.btn5);
        btn6 = (Button)findViewById(R.id.btn6);
        btn7 = (Button)findViewById(R.id.btn7);
        btn8 = (Button)findViewById(R.id.btn8);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);

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
        data=btn.getText().toString();
        str+="\n"+data;
        clickbtn = true;
        new Thread(new Runnable(){
            public void run(){
                try{
                    synchronized (this){
                        data1="\n W(Q)\t X(Q) \t Y(Q) \t Z(Q)\n";
                        data2="X(A) \t Y(A) \t Z(A)\n";

                        wait(5000);
                        clickbtn = false;
                        str += data1+data2;
                        data1="";
                        data2="";
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }}
        }).start();

        Toast.makeText(this, "סיים", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onAccelerometerData(BluetoothDevice bluetoothDevice, int i, Vector3D vector3D) {
        if(clickbtn) {

            data2 += "\t" + String.valueOf(vector3D.getX()) + "\t" + String.valueOf(vector3D.getY()) + "\t" + String.valueOf(vector3D.getZ())+"\n";
        }
    }

    @Override
    public void onQuaternionData(BluetoothDevice bluetoothDevice, int i, Quaternion quaternion) {


        if(enable) {
            curent = quaternion;
            enable=false;
        }
        if(flag) {
            invers = inverse(quaternion);
            curent = mulLeft(invers,quaternion);
            Log.i("curent", "curent X" + curent.getX());
            Log.i("curent", "curent Y" + curent.getY());
            Log.i("curent", "curent Z" + curent.getZ());
            Log.i("curent", "curent W" + curent.getW());





            flag=false;
        }

//doblet the curent quaternion  with the invers
        curent = mulLeft(invers,quaternion);
        Log.i("tag", "X" + curent.getX());
        Log.i("tag", "Y" + curent.getY());
        Log.i("tag", "Z" + curent.getZ());
        Log.i("tag", "W" + curent.getZ());
        if(clickbtn) {
            data1 +=  String.valueOf(curent.getW()) + "\t" + String.valueOf(curent.getX())
                    + "\t" + String.valueOf(curent.getY()) + "\t" + String.valueOf(curent.getZ())+"\n";
        }

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
            save();

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
        else if(v==btn8){
            flag = true;
            writedata(btn8);
        }

    }

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ActionActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(ActionActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            //openActivity();
        }
    }

    public void save(){

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File file = new File(root + "/saved_images");
        file.mkdirs();
        File file2 = new File (file, "cycare2.txt");
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream fos = new FileOutputStream(file2);
            fos.write(str.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Quaternion inverse(Quaternion quaternion) {
        flag=false;
        float norm = norm(quaternion);
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            return new Quaternion(-quaternion.getX() * invNorm, -quaternion.getY() * invNorm, -quaternion.getZ() * invNorm, quaternion.getW()
                    * invNorm);
        }

        // return an invalid result to flag the error
        return null;
    }

    public float norm(Quaternion quaternion) {
        return quaternion.getW()*quaternion.getW() + quaternion.getX()*quaternion.getX()
                + quaternion.getY()*quaternion.getY() + quaternion.getZ()*quaternion.getZ();
    }


    public Quaternion mulLeft (Quaternion other,Quaternion q) {
        Quaternion curent = new Quaternion();
        final float newX = other.getW() * q.getX() + other.getX() * q.getW() + other.getY() * q.getZ() - other.getZ() * q.getY();
        final float newY = other.getW() * q.getY() + other.getY() * q.getW() + other.getZ() * q.getX() - other.getX() * q.getZ();
        final float newZ = other.getW() * q.getZ() + other.getZ() * q.getW() + other.getX() * q.getY() - other.getY() * q.getX();
        final float newW = other.getW() * q.getW() - other.getX() * q.getX() - other.getY() * q.getY() - other.getZ() * q.getZ();
        curent.setX(newX);
        curent.setY(newY);
        curent.setZ(newZ);
        curent.setW(newW);
        return curent;
    }


}
