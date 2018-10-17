package com.chuck.autolink.usb;

/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public interface ISimpleTcpManager {
    /** 读入tcp数据 */
    int readTcpData(byte[] data, int realLen);

    /** 写入未封装tcp的数据 */
    void writeRawData(byte[] data, int realLen);
}
