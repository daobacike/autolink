package com.chuck.autolink.usb;

/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public interface IConmunicateHelper {
    void openAsync(IUSBConnStatusChanged onUSBConnStatusChanged);
    void close();

    /** 同步读取usb数据 */
    byte[] readSyncFromUSB();
    /** 同步写入usb数据 */
    void writeSyncToUSB(byte[] data);
    /** 同步写入usb数据 */
    void writeSyncToUSB(byte[] data, int off, int len);

    void release();
}
