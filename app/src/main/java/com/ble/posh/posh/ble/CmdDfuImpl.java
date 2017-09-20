package com.ble.posh.posh.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ble.posh.posh.ble.internal.internal.exception.DeviceDisconnectedException;
import com.ble.posh.posh.ble.internal.internal.exception.DfuException;
import com.ble.posh.posh.ble.internal.internal.exception.UploadAbortedException;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Admin on 16.08.2017.
 */

public class CmdDfuImpl extends BaseDfuImpl {

    // UUIDs used by the UART
    protected static UUID DEFAULT_UART_SERVICE_UUID       = new UUID(0x6e400001b5a3f393L ,0xe0a9e50e24dcca9eL);

    protected static UUID DEFAULT_UART_RX_UUID            = new UUID(0x6e400002b5a3f393L ,0xe0a9e50e24dcca9eL);
    protected static UUID DEFAULT_UART_TX_UUID            = new UUID(0x6e400003b5a3f393L ,0xe0a9e50e24dcca9eL);
    protected static UUID DEFAULT_UART_CONTROL_UUID       = new UUID(0x6e400004b5a3f393L ,0xe0a9e50e24dcca9eL);

    protected static UUID UART_SERVICE_UUID       = DEFAULT_UART_SERVICE_UUID;

    protected static UUID UART_RX_UUID        = DEFAULT_UART_RX_UUID;
    protected static UUID UART_TX_UUID        = DEFAULT_UART_TX_UUID;
    protected static UUID UART_CONTROL_UUID   = DEFAULT_UART_CONTROL_UUID;


    private final CmdBluetoothCallback mBluetoothCallback = new CmdBluetoothCallback();

    private BluetoothGattCharacteristic mControlCharacteristic;

    private boolean mCmdSend;
    private boolean mWait;

    public boolean ismCancelCall() {
        return mCancelCall;
    }

    public void setmCancelCall(boolean mCancelCall) {
        this.mCancelCall = mCancelCall;
    }

    private boolean mCancelCall;


    protected class CmdBluetoothCallback extends BaseCustomDfuImpl.BaseBluetoothGattCallback {

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCmdSend) {
                    mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_INFO, "Data written to " + characteristic.getUuid() + ", value (0x): " + parse(characteristic));
                    synchronized ( mLock) {
                        mCmdSend = false;
                    }
                }
            }
            notifyLock("onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            final int op_code = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            final int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            mCancelCall = true;
            notifyLock("onCharacteristicChanged");
        }
    }


    CmdDfuImpl(Intent intent, DfuBaseService service) {
        super(intent, service);
    }

    @Override
    public DfuGattCallback getGattCallback() {
        return mBluetoothCallback;
    }

    @Override
    public boolean isClientCompatible(Intent intent, BluetoothGatt gatt) throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        final BluetoothGattService dfuService = gatt.getService(UART_SERVICE_UUID);
        if (dfuService == null)
            return false;

        mGatt = gatt;

        BluetoothGattCharacteristic mRXCharacteristic = dfuService.getCharacteristic(UART_RX_UUID);
        BluetoothGattCharacteristic mTXCharacteristic = dfuService.getCharacteristic(UART_TX_UUID);
        mControlCharacteristic = dfuService.getCharacteristic(UART_CONTROL_UUID);

        if (mTXCharacteristic != null) Log.d("FILE_LOAD","Characteristic RX: " + mRXCharacteristic.getUuid());
        if (mRXCharacteristic != null) Log.d("FILE_LOAD","Characteristic TX: " + mTXCharacteristic.getUuid());
        if (mControlCharacteristic != null) Log.d("FILE_LOAD","Characteristic Control: " + mControlCharacteristic.getUuid());

        return mRXCharacteristic != null && mTXCharacteristic != null && mControlCharacteristic != null;
    }

    public void setWaitAns(boolean wait) {
        mWait = wait;
    }

    public void setFileName(String name) {
        String mFileName = name;
    }

    @Override
    public void performDfu(Intent intent) throws DfuException, DeviceDisconnectedException, UploadAbortedException {

        final BluetoothGatt gatt = mGatt;
        final String name = intent.getStringExtra(DfuBaseService.EXTRA_FILE_NAME);
        writeCmd(mControlCharacteristic,'4',name);
        if (mWait){
            synchronized (mLock) {
                while ((mConnected && mError == 0 && !mAborted) || mPaused) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        loge("Sleeping interrupted", e);
                    }
                }
                if (mAborted)
                    throw new UploadAbortedException();
                if (mError != 0)
                    throw new DfuException("Unable to write Op Code", mError);
                if (!mConnected)
                    throw new DeviceDisconnectedException("Unable to write Op Code: device disconnected");
            }
        }
        mGatt.disconnect();
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void writeCmd(final BluetoothGattCharacteristic characteristic, final char cmd, final String param)
            throws DeviceDisconnectedException, DfuException, UploadAbortedException {

        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        final String sendCmd = cmd + param + '#';
        characteristic.setValue(sendCmd);
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_VERBOSE, "Writing to characteristic " + characteristic.getUuid());
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_DEBUG, "gatt.writeCharacteristic(" + characteristic.getUuid() + ")");

        mGatt.writeCharacteristic(characteristic);

        try {
            synchronized (mLock) {
                //Log.d("BIN_BOOT","WSaA: "+mImageSizeInProgress+" "+mConnected+" "+mError+" "+!mAborted+" "+mPaused);
                while ((mCmdSend && mConnected && mError == 0 && !mAborted) || mPaused)
                    mLock.wait();
            }
        } catch (final InterruptedException e) {
            loge("Sleeping interrupted", e);
        }
        if (mAborted)
            throw new UploadAbortedException();
        if (mError != 0)
            throw new DfuException("Unable to write Image Sizes", mError);
        if (!mConnected)
            throw new DeviceDisconnectedException("Unable to write Image Sizes: device disconnected");

        //Log.d("BIN_BOOT","wait send imagesize 2");
    }


    @Override
    public void lastFile(int type) {

    }


}
