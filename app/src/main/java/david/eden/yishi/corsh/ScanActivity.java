package david.eden.yishi.corsh;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ioteratech.blueterasdk.BlueteraScanCallback;
import com.ioteratech.blueterasdk.BlueteraScanner;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private final String mTag = getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Bluetera> mDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDevices = new ArrayList<>();

        mAdapter = new MyAdapter(mDevices);
        mRecyclerView.setAdapter(mAdapter);

        BlueteraScanner scanner = new BlueteraScanner();

        scanner.startScan(new BlueteraScanCallback() {
            @Override
            public void onScanResult(BluetoothDevice device, int hardware_version, int firmware_version, int capabilities) {
                for(int i = 0; i < mDevices.size(); i++) {
                    if(mDevices.get(i).getBluetoothDevice().equals(device))
                        return;
                }

                Log.d(mTag, "found!\nname: " + device.getName() + " , hardware:" + hardware_version + ", firmware: " + firmware_version + ", caps: " + capabilities);
                mDevices.add(new Bluetera(device, hardware_version, firmware_version));
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<Bluetera> mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ConstraintLayout mView;
            public TextView mDeviceName;
            public TextView mMacAddress;
            public TextView mHardwareVersion;
            public TextView mFirmwareVersion;
            private final Activity mCallingActivity;
            private BluetoothDevice mDevice;

            public MyViewHolder(ConstraintLayout v, Activity calling_activity) {
                super(v);
                mView = v;
                mDeviceName = v.findViewById(R.id.textView_deviceName);
                mMacAddress = v.findViewById(R.id.textView_mac);
                mHardwareVersion = v.findViewById(R.id.textView_hw);
                mFirmwareVersion = v.findViewById(R.id.textView_fw);
                mCallingActivity = calling_activity;

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(ScanActivity.this,ActionActivity.class);
                        in.putExtra("device", mDevice);
                        startActivity(in);
                        mCallingActivity.finish();
                    }
                });
            }

            public void setDevice(BluetoothDevice device) {
                mDevice = device;
            }
        }

        public MyAdapter(List<Bluetera> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ConstraintLayout v = (ConstraintLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v, ScanActivity.this);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Bluetera b = mDataset.get(position);

            holder.mDeviceName.setText(b.getBluetoothDevice().getName());
            holder.mMacAddress.setText(b.getBluetoothDevice().getAddress());
            holder.mHardwareVersion.setText("" + b.getHardwareVersion());
            holder.mFirmwareVersion.setText("" + b.getFirmwareVersion());
            holder.setDevice(b.getBluetoothDevice());
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    private class Bluetera {
        private final BluetoothDevice mBluetoothDevice;
        private final int mHardwareVersion;
        private final int mFirmwareVersion;

        private Bluetera(BluetoothDevice mBluetoothDevice, int mHardwareVersion, int mFirmwareVersion) {
            this.mBluetoothDevice = mBluetoothDevice;
            this.mHardwareVersion = mHardwareVersion;
            this.mFirmwareVersion = mFirmwareVersion;
        }

        public BluetoothDevice getBluetoothDevice() {
            return mBluetoothDevice;
        }

        public int getHardwareVersion() {
            return mHardwareVersion;
        }

        public int getFirmwareVersion() {
            return mFirmwareVersion;
        }
    }
}
