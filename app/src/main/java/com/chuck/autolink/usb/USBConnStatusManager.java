package com.chuck.autolink.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.chuck.autolink.AppApplication;

/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public class USBConnStatusManager {
    private static final String TAG = "USBConnStatusManager";
    private static final String ACTION_USB_PERMISSION ="com.chuck.autolink.action.USB_PERMISSION";

    /** 设备已连接 且数据通道可用 */
    public static final int STATUS_CONN_OK = 0;
    /** 设备已连接 数据通道错误 */
    public static final int STATUS_CONN_ERR = 1;
    /** 设备未连接 */
    public static final int STATUS_DISCONN = 2;
    private static int sCurStatus = STATUS_DISCONN;

    private IUSBConnStatusChanged mUSBConnStatusChanged = null;
    private static USBConnStatusManager  sInstance;

    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending = false;

    //系统UsbManager
    private UsbManager mUsbManager;
    //当前实例管理的accessory
    private UsbAccessory mUsbAccessory;

    public static USBConnStatusManager getInstance(){
        if (sInstance == null) {
            sInstance = new USBConnStatusManager();
        }
        return sInstance;
    }

    public USBConnStatusManager(){
        //注册使用usb设备的权限
        // Broadcast Intent for myPermission
        mPermissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        //注册usb设备插拔消息广播
        // Register Intent myPermission and remove accessory
        IntentFilter filter = new IntentFilter();
        //接收权限信息
        filter.addAction(ACTION_USB_PERMISSION);
        //接收accessory连接事件
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        //接收accessory断开事件
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        getContext().registerReceiver(mUsbReceiver, filter);

        mUsbManager = (UsbManager) getContext().getSystemService(getContext().USB_SERVICE);
    }


    public void registOnUSBConnStatusChangedListener(IUSBConnStatusChanged onUSBConnStatusChanged){

        synchronized (USBConnStatusManager.class) {
            mUSBConnStatusChanged = onUSBConnStatusChanged;
        }
    }

    public void unRegistOnUSBConnStatusChangedListener(){

        //synchronized (USBConnStatusManager.class) {
        mUSBConnStatusChanged = null;
        //}
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "receive usb connect broadcast:" + action);

            if (action.equals(ACTION_USB_PERMISSION)) {
                synchronized (this) {
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        Log.d(TAG, "prepare to open usb stream");
                        sCurStatus = STATUS_CONN_OK;
                        mUsbAccessory = accessory;
                        if (mUSBConnStatusChanged != null) {
                            mUSBConnStatusChanged.onUSBConnect(accessory);
                        }
                    } else {
                        Log.d(TAG, "permission denied for accessory " + accessory);

                        sCurStatus = STATUS_CONN_ERR;
                        mUsbAccessory = null;

                        //synchronized (USBConnStatusManager.class) {
                        if (mUSBConnStatusChanged != null) {
                            mUSBConnStatusChanged.onUSBConnectFailed(accessory);
                        }
                        //}
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                //检测到usb断开
                Log.d(TAG, "USB_ACCESSORY_DETACHED " + accessory);

                sCurStatus = STATUS_DISCONN;
                mUsbAccessory = null;
                //closeAccessory();
                //synchronized (USBConnStatusManager.class) {
                if (mUSBConnStatusChanged != null) {
                    mUSBConnStatusChanged.onUSBDisconnect(accessory);
                }
                //}
                //}
            }
        }
    };

    /**
     * 直接检查usb设备是否连接
     * @return
     */
    public void checkUSBDevice() {
        UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        if (accessories == null) {
            Log.i(TAG, "accessories list is null");
            return;
        }
        Log.i(TAG, "accessories length "+accessories.length);
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                mUsbAccessory = accessory;
                if (mUSBConnStatusChanged != null) {
                    mUSBConnStatusChanged.onUSBConnect(accessory);
                }
            }
        } else {
            if (!mPermissionRequestPending) {
                mUsbManager.requestPermission(accessory, mPermissionIntent);
                mPermissionRequestPending = true;
            }
        }

    }

    private Context getContext(){
        //Context context = null;
        //return context;
        return AppApplication.getApplication();
    }
}
