package com.chuck.autolink.usb;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public class USBHelper implements IConmunicateHelper {

    private static final String TAG = "USBHelper";
    private Context mContext;
    private static final int RECIVE_BUF_SIZE = 1024 * 10;
    private static final int SEND_BUF_SIZE = 1024 * 10;

    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    private UsbManager mUsbManager;
    private UsbAccessory mAccessory;
    private byte[] mReciveBuffer;

    //自定义usb状态管理器
    private USBConnStatusManager mUSBConnStatusManager;

    public USBHelper(Context context) {
        mContext = context;
        mUSBConnStatusManager = USBConnStatusManager.getInstance();
        mUsbManager = (UsbManager) mContext.getSystemService(mContext.USB_SERVICE);
    }

    /**
     * accessory模式打开android的 usb设备
     * 如果当前列表有处于accessory模式的句柄则直接打开
     * 如果当前没有则回监听usb插拔，监听到对应事件后检查系统列表
     * @param onUSBConnStatusChanged
     */
    @Override
    public void openAsync(final IUSBConnStatusChanged onUSBConnStatusChanged) {
        mReciveBuffer = new byte[RECIVE_BUF_SIZE];

        mUSBConnStatusManager.registOnUSBConnStatusChangedListener(new IUSBConnStatusChanged() {
            @Override
            public void onUSBConnect(UsbAccessory accessory) {
                openAccessory(accessory);
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBConnect(accessory);
                }
            }

            @Override
            public void onUSBConnectFailed(UsbAccessory accessory) {
                closeAccessory();
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBConnectFailed(accessory);
                }
            }

            @Override
            public void onUSBDisconnect(UsbAccessory accessory) {
                closeAccessory();
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBDisconnect(accessory);
                }
            }
        });

        //检查usb列表 查看是否已经连接accessory设备
        mUSBConnStatusManager.checkUSBDevice();
    }

    @Override
    public void close() {
        closeAccessory();
    }

    @Override
    public byte[] readSyncFromUSB() {
        return new byte[0];
    }

    @Override
    public void writeSyncToUSB(byte[] data) {

    }

    @Override
    public void writeSyncToUSB(byte[] data, int off, int len) {

    }

    @Override
    public void release() {

    }

    /**
     * 用于监听是否可以开启数据收发线程(如果拿到usb读写流则回调此接口)
     */
    public interface IDataTranPrepared {
        void onDataTranPrepared(FileInputStream inputStream, FileOutputStream outputStream);
    }

    public void setOnDataTranPrepared(IDataTranPrepared onDataTranPrepared) {
        mDataTranPrepared = onDataTranPrepared;
    }

    private IDataTranPrepared mDataTranPrepared = null;

    /**
     * 通过accessory句柄拿到usb设备的输入输出流
     * @param accessory
     */
    private void openAccessory(UsbAccessory accessory) {

        mFileDescriptor = mUsbManager.openAccessory(accessory);

        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();

            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            if (mDataTranPrepared != null) {
                Log.d(TAG, "accessory opened DataTranPrepared");
                mDataTranPrepared.onDataTranPrepared(mInputStream, mOutputStream);
            }

            Log.d(TAG, "accessory opened");
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }

    private void closeAccessory() {

        //停止监听usb连接状态变化
        mUSBConnStatusManager.unRegistOnUSBConnStatusChangedListener();

        try {
            if (mFileDescriptor != null) {
                synchronized (mFileDescriptor) {
                    mFileDescriptor.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }

        if (mInputStream != null) {
            try {
                synchronized (mInputStream) {
                    mInputStream.close();
                    mInputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mOutputStream != null) {
            try {
                synchronized (mOutputStream) {
                    mOutputStream.close();
                    mOutputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "accessory closed ...");
    }

}
