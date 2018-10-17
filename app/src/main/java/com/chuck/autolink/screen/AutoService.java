package com.chuck.autolink.screen;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import static com.chuck.autolink.data.Defines.REQUEST_CODE_CAPTURE;

public class AutoService extends Service {
    public static final String ACTION_STARTSERVICE = "com.chuck.autolink.startservice";
    public static final String ACTION_STOPSERVICE = "com.chuck.autolink.stopservice";
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ScreenRecord mScreenRecord;

    private ServiceBinder mBinder;
    private Listener mListener;

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

        mBinder = new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    public class ServiceBinder extends Binder {
        public  ServiceBinder(){}

        public void checkAccessoryConfigue() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            mScreenRecord = new ScreenRecord(mMediaProjection);
            mScreenRecord.start();
        }

        public void registListener(Listener  listener) {
            mListener = listener;
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            mListener.requestPermission(captureIntent, REQUEST_CODE_CAPTURE);
        }

        public void unregistListener(Listener  listener) {
            if (mListener == listener) {
                mListener = null;
            }
        }
    }


    public interface Listener{
        void  onEncodeErr();
        void onFrameOut(int i,int i2);
        void onLoseAccessory();
        void onStatusChanged(int i);
        void requestPermission(Intent intent, int id);
        void requestUpdate(Bundle bundle);
    }
}
