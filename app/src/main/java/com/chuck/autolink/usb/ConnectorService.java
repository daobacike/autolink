package com.chuck.autolink.usb;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

public class ConnectorService extends Service {
    private static final String TAG = "ConnectorService";
    protected DataEngine mSimpleTcpWrapper;

    public ConnectorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mRemoteStud;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSimpleTcpWrapper = new DataEngine(ConnectorService.this);
        setupLocalSocketClients();
        mSimpleTcpWrapper.start();
    }

    IRemoteConnector.Stub mRemoteStud = new IRemoteConnector.Stub(){

        @Override
        public int openUSBAsync(int param) throws RemoteException {

            if(mSimpleTcpWrapper != null){
                boolean reset;
                if(param == 0){
                    reset = false;
                }else {
                    reset = true;
                }
                mSimpleTcpWrapper.openUSBAsync(reset);

                return 0;
            }else {
                return -1;
            }
        }

        @Override
        public int closeUSBAsync(int param) throws RemoteException {

            if(mSimpleTcpWrapper != null) {
                mSimpleTcpWrapper.closeUSB();
                return 0;
            }else{
                return -1;
            }
        }

        @Override
        public ParcelFileDescriptor getFileDescriptor(int port) throws RemoteException {
            return null;
        }
    };

    protected void setupLocalSocketClients(){
        Log.i(TAG, "setupLocalSocketClients");
    }
}
