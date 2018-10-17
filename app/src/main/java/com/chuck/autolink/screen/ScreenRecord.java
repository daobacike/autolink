package com.chuck.autolink.screen;



import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.view.Surface;

import com.chuck.autolink.media.VideoMediaCodec;


/**
 * Created by chuck.liuzhaopeng on 2018/10/15.
 */

public class ScreenRecord extends  Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private long startTime = 0;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;
    private String mDstPath;
    private VideoMediaCodec mVideoMediaCodec;
    private Surface mSurface;
    private VirtualDisplay mVirtualDisplay;

    public ScreenRecord(MediaProjection mp){
        mMediaProjection = mp;
        startTime = 0;
        mVideoMediaCodec = new VideoMediaCodec();
        //mDstPath = dstPath;
    }

    @Override
    public void run() {
        mVideoMediaCodec.prepare();
        mSurface = mVideoMediaCodec.getSurface();
        mVirtualDisplay =mMediaProjection.createVirtualDisplay(TAG + "-display", VideoInfo.VIDEO_WIDTH, VideoInfo.VIDEO_HEIGHT, VideoInfo.VIDEO_DPI, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mSurface, null, null);
        mVideoMediaCodec.isRun(true);
        mVideoMediaCodec.getBuffer();
    }

    public void release(){
        mVideoMediaCodec.release();
    }
}
