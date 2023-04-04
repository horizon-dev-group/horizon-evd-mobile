package com.example.fibath.ui.Printer.bluetooth;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;

public class SelectedDevice {
    private static BluetoothConnection selectedDevice;
    private static boolean deviceSelected = false;

    public static boolean isDeviceSelected() {
        return deviceSelected;
    }

    public static void setIsDeviceSelected(boolean isDeviceSelected) {
        SelectedDevice.deviceSelected = isDeviceSelected;
    }

    public static BluetoothConnection getSelectedDevice() {
        return selectedDevice;
    }

    public static void setSelectedDevice(BluetoothConnection selectedDevice) {
        SelectedDevice.selectedDevice = selectedDevice;
    }
}
