package com.ble.posh.posh.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by Admin on 24.07.2017.
 */
@TargetApi(Build.VERSION_CODES.M)
public class BluetoothLeScannerImplMarshmallow extends BluetoothLeScannerImplLollipop {

    protected android.bluetooth.le.ScanSettings toImpl(@NonNull final BluetoothAdapter adapter, @NonNull final ScanSettings settings) {
        final android.bluetooth.le.ScanSettings.Builder builder = new android.bluetooth.le.ScanSettings.Builder().setScanMode(settings.getScanMode());

        if (adapter.isOffloadedScanBatchingSupported() && settings.getUseHardwareBatchingIfSupported())
            builder.setReportDelay(settings.getReportDelayMillis());

        if (settings.getUseHardwareCallbackTypesIfSupported())
            builder.setCallbackType(settings.getCallbackType())
                    .setMatchMode(settings.getMatchMode())
                    .setNumOfMatches(settings.getNumOfMatches());

        return builder.build();
    }
}