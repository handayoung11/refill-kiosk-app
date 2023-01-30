package kr.co.nicevan.nvcat.Printer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bixolon.commonlib.connectivity.searcher.BXLUsbDevice;
import com.bxl.config.editor.BXLConfigLoader;
import com.bxl.config.util.BXLBluetoothLE;
import com.bxl.config.util.BXLNetwork;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jpos.JposException;
import kr.co.nicevan.nvcat.MainActivity;
import kr.co.nicevan.nvcat.R;

public class PrinterConnectActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener, View.OnTouchListener, View.OnClickListener {

    String TAG = this.getClass().getSimpleName();

    private final int REQUEST_PERMISSION = 0;
    private final String DEVICE_ADDRESS_START = " (";
    private final String DEVICE_ADDRESS_END = ")";

    private final ArrayList<CharSequence> bondedDevices = new ArrayList<>();
    private ArrayAdapter<CharSequence> arrayAdapter;

    private int portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH;
    private String logicalName = "";
    private String address = "";

    private LinearLayout layoutModel;
    private LinearLayout layoutIPAddress;

    private RadioGroup radioGroupPortType;
    private TextView textViewBluetooth;
    private ListView listView;
    private EditText editTextIPAddress;
    private CheckBox checkBoxAsyncMode;

    private Button btnPrinterOpen;

    private ProgressBar mProgressLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_connect);

        layoutModel = findViewById(R.id.LinearLayout2);
        layoutIPAddress = findViewById(R.id.LinearLayout3);
        layoutIPAddress.setVisibility(View.GONE);

        radioGroupPortType = findViewById(R.id.radioGroupPortType);
        radioGroupPortType.setOnCheckedChangeListener(this);

        textViewBluetooth = findViewById(R.id.textViewBluetoothList);
        editTextIPAddress = findViewById(R.id.editTextIPAddr);
        editTextIPAddress.setText("192.168.0.1");

        checkBoxAsyncMode = findViewById(R.id.checkBoxAsyncMode);

        btnPrinterOpen = findViewById(R.id.btnPrinterOpen);
        btnPrinterOpen.setOnClickListener(this);

        mProgressLarge = findViewById(R.id.progressBar1);
        mProgressLarge.setVisibility(ProgressBar.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
        setPairedDevices();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, bondedDevices);
        listView = findViewById(R.id.listViewPairedDevices);
        listView.setAdapter(arrayAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        listView.setOnTouchListener(this);

        Spinner modelList = findViewById(R.id.spinnerModelList);

        ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(this, R.array.modelList, android.R.layout.simple_spinner_dropdown_item);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelList.setAdapter(modelAdapter);
        modelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                logicalName = (String) parent.getItemAtPosition(position);
                Log.d(TAG,"P-logicalName:" + logicalName);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setPairedDevices() {
        bondedDevices.clear();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission") Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDeviceSet) {
            bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
        }

        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private void setBleDevices() {
        mHandler.obtainMessage(0).sendToTarget();
        BXLBluetoothLE.setBLEDeviceSearchOption(5, 1);
        new searchBLEPrinterTask().execute();
    }

    private void setUSBDevice(){

        String[] items;
        int index = 0;
        bondedDevices.clear();

        Set<UsbDevice> usbDevices = BXLUsbDevice.refreshUsbDevicesList(this, false);
        items = new String[usbDevices.size()];
        if (usbDevices != null && !usbDevices.isEmpty()) {
            for (UsbDevice device : usbDevices) {
                items[index] = device.getProductName() + " (" + device.getDeviceName() + ")";

                bondedDevices.add(items[index++]);

            }
        }else {
            Toast.makeText(getApplicationContext(), "Can't found USB devices. ", Toast.LENGTH_SHORT).show();
        }

        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }

        mHandler.obtainMessage(1).sendToTarget();

    }

    private void setNetworkDevices() {
        mHandler.obtainMessage(0).sendToTarget();
        BXLNetwork.setWifiSearchOption(5, 1);
        new searchNetworkPrinterTask().execute();
    }

    private class searchNetworkPrinterTask extends AsyncTask<Integer, Integer, Set<CharSequence>> {
        private String message;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Set<CharSequence> NetworkDeviceSet) {
            bondedDevices.clear();

            String[] items;
            if (NetworkDeviceSet != null && !NetworkDeviceSet.isEmpty()) {
                items = NetworkDeviceSet.toArray(new String[NetworkDeviceSet.size()]);
                for (int i = 0; (items != null) && (i < items.length); i++) {
                    bondedDevices.add(items[i]);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Can't found network devices. ", Toast.LENGTH_SHORT).show();
            }

            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            mHandler.obtainMessage(1).sendToTarget();
        }

        @Override
        protected Set<CharSequence> doInBackground(Integer... params) {
            try {
                return BXLNetwork.getNetworkPrinters(PrinterConnectActivity.this, BXLNetwork.SEARCH_WIFI_ALWAYS);
            } catch (NumberFormatException | JposException e) {
                message = e.getMessage();
                return new HashSet<>();
            }
        }
    }

    private class searchBLEPrinterTask extends AsyncTask<Integer, Integer, Set<BluetoothDevice>> {
        private String message;

        @Override
        protected void onPreExecute() {
        }

        @SuppressLint("MissingPermission")
        @Override
        protected void onPostExecute(Set<BluetoothDevice> bluetoothDeviceSet) {
            bondedDevices.clear();

            if (bluetoothDeviceSet.size() > 0) {
                for (BluetoothDevice device : bluetoothDeviceSet) {
                    bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Can't found BLE devices. ", Toast.LENGTH_SHORT).show();
            }

            if (arrayAdapter != null) {
                arrayAdapter.notifyDataSetChanged();
            }

            if (message != null) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            mHandler.obtainMessage(1).sendToTarget();
        }

        @Override
        protected Set<BluetoothDevice> doInBackground(Integer... params) {
            try {
                return BXLBluetoothLE.getBLEPrinters(PrinterConnectActivity.this, BXLBluetoothLE.SEARCH_BLE_ALWAYS);
            } catch (NumberFormatException | JposException e) {
                message = e.getMessage();
                return new HashSet<>();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radioBT:
                portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH;
                textViewBluetooth.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layoutIPAddress.setVisibility(View.GONE);

                setPairedDevices();
                break;
            case R.id.radioBLE:
                portType = BXLConfigLoader.DEVICE_BUS_BLUETOOTH_LE;
                textViewBluetooth.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layoutIPAddress.setVisibility(View.GONE);

                // Bluetooth 5.0 BLE(MSO4)
                setPairedDevices();

                // Bluetooth 4.1 BLE(MSO3)
                // setBleDevices();
                break;
            case R.id.radioWifi:
                portType = BXLConfigLoader.DEVICE_BUS_WIFI;
                layoutIPAddress.setVisibility(View.VISIBLE);
                textViewBluetooth.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                setNetworkDevices();
                break;
            case R.id.radioUSB:
                portType = BXLConfigLoader.DEVICE_BUS_USB;
                Log.d(TAG,"P-portType:" + portType);

                textViewBluetooth.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                layoutIPAddress.setVisibility(View.GONE);

                setUSBDevice();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            listView.requestDisallowInterceptTouchEvent(false);
        else listView.requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String device = ((TextView) view).getText().toString();
        if (portType == BXLConfigLoader.DEVICE_BUS_WIFI) {
            editTextIPAddress.setText(device);
            address = device;
        } else {
            address = device.substring(device.indexOf(DEVICE_ADDRESS_START) + DEVICE_ADDRESS_START.length(), device.indexOf(DEVICE_ADDRESS_END));
            Log.d(TAG,"P-address:" + address);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrinterOpen:
                mHandler.obtainMessage(0).sendToTarget();
                new Thread(() -> {
                    // TODO Auto-generated method stub
                    if (portType == BXLConfigLoader.DEVICE_BUS_WIFI) {
                        address = editTextIPAddress.getText().toString();
                    }

                    Log.d("TAG", "postType : " + portType);
                    Log.d("TAG", "logicalName : " + logicalName);
                    Log.d("TAG", "address : " + address);
                    Log.d("TAG", "checkBoxAsyncMode : " + checkBoxAsyncMode.isChecked());

                    if (MainActivity.getPrinterInstance().printerOpen(portType, logicalName, address, checkBoxAsyncMode.isChecked())) {
                        finish();
                    } else {
                        mHandler.obtainMessage(1, 0, 0, "Fail to printer open!!").sendToTarget();
                    }
                }).start();

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public final Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mProgressLarge.setVisibility(ProgressBar.VISIBLE);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;
                case 1:
                    String data = (String) msg.obj;
                    if (data != null && data.length() > 0) {
                        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                    }
                    mProgressLarge.setVisibility(ProgressBar.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;
            }
            return false;
        }
    });

    // ----------------- Bluetooth Scan & Pairing ----------------- //
    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery(int timeout) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getApplicationContext().registerReceiver(bluetoothReceiver, filter);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopBluetoothDiscovery(false);
                }
            }, timeout);
        }
    }

    @SuppressLint("MissingPermission")
    private void stopBluetoothDiscovery(boolean isReceiver) {
        if (isReceiver) {
            try {
                getApplicationContext().unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String devName = device.getName();
                    String address = device.getAddress();
                    if (address.startsWith("74:F0:7D") || address.startsWith("40:19:20")) {
                        // Bixolon Printer
                        // 1. stop scan
                        stopBluetoothDiscovery(true);
                        // 2. Request pairing
                        try {
                            Method method = device.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(device, (Object[]) null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String devName = device.getName();
                    String address = device.getAddress();
                    try {
                        String pin = "0000";
                        byte[] pinBytes = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, pin);
                        Method m = device.getClass().getMethod("setPin", byte[].class);
                        m.invoke(device, pinBytes);
                        device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}
