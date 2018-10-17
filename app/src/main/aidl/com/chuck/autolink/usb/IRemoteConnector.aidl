// IRemoteConnector.aidl
package com.chuck.autolink.usb;

// Declare any non-default types here with import statements

interface IRemoteConnector {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int openUSBAsync(int param);
    int closeUSBAsync(int param);
    ParcelFileDescriptor getFileDescriptor(int port);
}
