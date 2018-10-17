package com.chuck.autolink.usb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public class UsbmuxdManager {
    private static final String TAG = "UsbmuxdManager";
    private Context mContext;
    private static UsbmuxdManager sUsbmuxdManager;
    private IRemoteConnector mRemoteConnector;
    private ServiceConnection mRemoteConn;
    private OnRemoteServiceConnectedListener mOnRemoteServiceConnectedListener = null;

    public interface OnRemoteServiceConnectedListener{
        void onConnected();
        void onDisconnected();
    }

    public static UsbmuxdManager getInstance() {
        if (sUsbmuxdManager == null) {
            sUsbmuxdManager = new UsbmuxdManager();
        }
        return sUsbmuxdManager;
    }

    public void startUsbmuxdService(Context context,  Class<? extends ConnectorService> clazz) {
        mContext = context;

        Intent intent = new Intent(mContext, clazz);
        //Intent intent = new Intent(mContext, TestConnectorService.class);
        //Intent intent = new Intent(mContext, ConnectorService.class);
        mContext.startService(intent);

        mRemoteConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                mRemoteConnector = IRemoteConnector.Stub.asInterface(iBinder);
                Log.i(TAG, "mRemoteConnector bind success");
                if(mOnRemoteServiceConnectedListener != null){
                    mOnRemoteServiceConnectedListener.onConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
               Log.i(TAG, "mRemoteConnector bind failed");
                if(mOnRemoteServiceConnectedListener != null){
                    mOnRemoteServiceConnectedListener.onDisconnected();
                }
            }
        };

        Intent intentConnectorService = new Intent(mContext, clazz);
        //Intent intentConnectorService = new Intent(mContext, TestConnectorService.class);
        //Intent intentConnectorService = new Intent(mContext, ConnectorService.class);
        mContext.bindService(intentConnectorService, mRemoteConn, BIND_AUTO_CREATE);
    }

    public void setOnRemoteServiceConnectedListener(OnRemoteServiceConnectedListener onRemoteServiceConnectedListener){
        mOnRemoteServiceConnectedListener = onRemoteServiceConnectedListener;
    }

    private void openUsb(int reset){
        Log.i(TAG, "openUsb mRemoteConnector "+mRemoteConnector);

        try {
            if(mRemoteConnector != null) {
                mRemoteConnector.openUSBAsync(reset);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closeUsb(){

        try {
            if(mRemoteConnector != null) {
                mRemoteConnector.closeUSBAsync(1);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在activity的生命周期方法中调用
     */
    private boolean mIsStoped = true;
    private int mActivityCnt = 1;

    public void openUsbOnActivityStart(){

        if(mIsStoped) {
            mIsStoped = false;
            openUsb(1);
        }
        mActivityCnt++;
    }

    public void closeUsbOnActivityStop(){
        mActivityCnt--;
        if(mActivityCnt == 0) {
            closeUsb();
            mIsStoped = true;
        }
    }
}
