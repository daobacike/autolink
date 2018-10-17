package com.chuck.autolink.usb;

import android.hardware.usb.UsbAccessory;

/**
 * Created by chuck.liuzhaopeng on 2018/10/17.
 */

public interface IUSBConnStatusChanged {
    /** USB连接上 并且连接成功 */
    void onUSBConnect(UsbAccessory accessory);
    /** USB连接上 但是连接失败*/
    void onUSBConnectFailed(UsbAccessory accessory);
    /** USB拔出 */
    void onUSBDisconnect(UsbAccessory accessory);
}
