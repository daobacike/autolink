package com.chuck.autolink.screen;


import android.media.projection.MediaProjection;



/**
 * Created by chuck.liuzhaopeng on 2018/10/15.
 */

public class ScreenCapture extends  Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private long startTime = 0;
    private MediaProjection mMediaProjection;
    private String mDstPath;

    public void  ScreenRecorder(int width, int height, int dpi,
                          MediaProjection mp, String dstPath){
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
        mMediaProjection = mp;
        startTime = 0;
        //mVideoEncoder = new VideoEncoder(video);
        mDstPath = dstPath;




    }
}
