package com.chuck.autolink;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class AutoService extends Service {
    public static final String ACTION_STARTSERVICE = "com.chuck.autolink.startservice";
    public static final String ACTION_STOPSERVICE = "com.chuck.autolink.stopservice";
    private MediaProjectionManager mMediaProjectionManager;
    private ServiceBinder mBinder;


    public AutoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();

        mBinder = new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    public class ServiceBinder extends Binder {
        public  ServiceBinder(){}

//        public int ForceLandscape(boolean paramBoolean) {
//            return  CarLinkService.this.ForceLandscape(paramBoolean);
//        }

        public void checkAccessoryConfigue() {

        }

        public int getStatus() {
            return 0;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        public int start() {
            return 0;
        }

        public int stop() {
            return 0;
        }

//        public void registListener(Listener  listener) {
//            mListener = listener;
//        }
//
//        public void unregistListener(Listener  listener) {
//            if (mListener == listener) {
//                mListener = null;
//            }
//        }
    }


    public interface Listener{
        void  onEncodeErr();
        void onFrameOut(int i,int i2);
        void onLoseAccessory();
        void onStatusChanged(int i);
        void requestPermission(Intent intent, int i);
        void requestUpdate(Bundle bundle);
    }
}
