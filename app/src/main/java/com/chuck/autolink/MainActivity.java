package com.chuck.autolink;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import com.chuck.autolink.media.h264data;
import com.chuck.autolink.screen.AutoService;

import java.util.concurrent.ArrayBlockingQueue;

import static com.chuck.autolink.data.Defines.REQUEST_CODE_CAPTURE;

public class MainActivity extends Activity {
    AutoService.ServiceBinder mBinder;
    private static int queuesize = 30;
    public static ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<>(queuesize);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        Intent  intent = new Intent();
        intent.setAction(AutoService.ACTION_STARTSERVICE);
        intent.setClass(this, AutoService.class);
        startService(intent);
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);


    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        moveTaskToBack(true);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mBinder.onActivityResult(requestCode, resultCode,  data);
        moveTaskToBack(true);
    }


    private final ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (AutoService.ServiceBinder) service;
            mBinder.registListener(mServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder.unregistListener(mServiceListener);
            finish();
        }
    };



    private final AutoService.Listener mServiceListener = new AutoService.Listener() {
        @Override
        public void onEncodeErr() {

        }

        @Override
        public void onFrameOut(int i, int i2) {

        }

        @Override
        public void onLoseAccessory() {

        }

        @Override
        public void onStatusChanged(int i) {

        }

        @Override
        public void requestPermission(Intent intent, int id) {
            switch (id) {
                case REQUEST_CODE_CAPTURE :
                    startActivityForResult(intent, id);
                    break;
                default:
                    break;
            }

        }

        @Override
        public void requestUpdate(Bundle bundle) {

        }
    };

    public static void putData(byte[] buffer, int type,long ts) {
        if (h264Queue.size() >= queuesize) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        data.type = type;
        data.ts = ts;
        h264Queue.add(data);
    }

}
