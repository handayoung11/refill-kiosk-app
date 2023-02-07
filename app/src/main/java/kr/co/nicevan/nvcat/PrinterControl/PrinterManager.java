package kr.co.nicevan.nvcat.PrinterControl;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.bixolon.commonlib.connectivity.searcher.BXLUsbDevice;
import com.bxl.config.editor.BXLConfigLoader;

import java.util.Set;

public class PrinterManager {
    private BixolonPrinter receiptPrinter; // 영수증 프린터
    private BixolonPrinter labelPrinter; // 라벨 프린터

    private static PrinterManager instance;

    private String TAG = PrinterManager.class.getSimpleName();

    private PrinterManager(Context context) {
        DefaultBixolonPrinterUserListener listener = new DefaultBixolonPrinterUserListener();

        receiptPrinter = new BixolonPrinter(context);
        labelPrinter = new BixolonPrinter(context);
        receiptPrinter.setUserListener(listener);
        labelPrinter.setUserListener(listener);
    }

    public static PrinterManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PrinterManager가 초기화되지 않았습니다.");
        }
        return instance;
    }

    public static PrinterManager init(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new PrinterManager(context);
        instance.openPrinter(context); // 영수증 & 라벨 프린터 오픈
        return instance;
    }

    /**
     * 프린터 오픈
     */
    public void openPrinter(Context context){

        int portType = BXLConfigLoader.DEVICE_BUS_USB;
        Boolean checkBoxAsyncMode = true;

        Set<UsbDevice> usbDevices = BXLUsbDevice.refreshUsbDevicesList(context, false);
        if (usbDevices != null && !usbDevices.isEmpty()) {
            for (UsbDevice device : usbDevices) {
                //logical name
                String productName = device.getProductName();
                //address
                String deviceName = device.getDeviceName();

                if(productName.equals("BK3-3")){ // 영수증 프린터
                    receiptPrinter.printerOpen(portType, productName, deviceName, checkBoxAsyncMode);
                }

                if(productName.equals("BK5-3")){ // 라벨 프린터
                    labelPrinter.printerOpen(portType, productName, deviceName, checkBoxAsyncMode);
                }
            }
        }else {
            Log.d(TAG, "Not found USB devices");
        }
    }

    public BixolonPrinter getRecPrt() {
        return receiptPrinter;
    }

    public BixolonPrinter getLabPrt() {
        return labelPrinter;
    }
}
