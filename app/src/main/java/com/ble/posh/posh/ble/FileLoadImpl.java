package com.ble.posh.posh.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.ble.posh.posh.ble.internal.internal.exception.DeviceDisconnectedException;
import com.ble.posh.posh.ble.internal.internal.exception.DfuException;
import com.ble.posh.posh.ble.internal.internal.exception.UploadAbortedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.UUID;

public class FileLoadImpl extends  BaseCustomDfuImpl {

    // UUIDs used by the UART
    protected static UUID DEFAULT_UART_SERVICE_UUID       = new UUID(0x6e400001b5a3f393L ,0xe0a9e50e24dcca9eL);

    protected static UUID DEFAULT_UART_RX_UUID            = new UUID(0x6e400002b5a3f393L ,0xe0a9e50e24dcca9eL);
    protected static UUID DEFAULT_UART_TX_UUID            = new UUID(0x6e400003b5a3f393L ,0xe0a9e50e24dcca9eL);
    protected static UUID DEFAULT_UART_CONTROL_UUID       = new UUID(0x6e400004b5a3f393L ,0xe0a9e50e24dcca9eL);

    private static final UUID UART_SERVICE_UUID       = DEFAULT_UART_SERVICE_UUID;

    private static final UUID UART_RX_UUID        = DEFAULT_UART_RX_UUID;
    private static final UUID UART_TX_UUID        = DEFAULT_UART_TX_UUID;
    private static final UUID UART_CONTROL_UUID   = DEFAULT_UART_CONTROL_UUID;


    private final byte[] mbinBuffer;

    private BluetoothGattCharacteristic mRXCharacteristic;
    private BluetoothGattCharacteristic mControlCharacteristic;

    private volatile boolean mEnd = false;
    private volatile boolean mImageSizeInProgress;

    private volatile  boolean mFirmwareUploadInProgress;
    private volatile int mNumberBlock;

    private int total = 0;


    private final FileBluetoothCallback mBluetoothCallback = new FileBluetoothCallback();
    private String mFileName;
    private int rw_size;

    protected class FileBluetoothCallback extends BaseCustomBluetoothCallback {
        @Override
        protected void onPacketCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            //Log.d("FILE_LOAD","Data written to " + characteristic.getUuid());
            if (mImageSizeInProgress) {
                // We've got confirmation that the image size was sent
                //Log.d("FILE_LOAD","mImageSizeInProgress " + mImageSizeInProgress);
                mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_INFO, "Data written to " + characteristic.getUuid() + ", value (0x): " + parse(characteristic));
                synchronized (mLock) {
                    mImageSizeInProgress = false;
                }
            } else {
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            //Log.d("FILE_LOAD","Data was send from: "+characteristic.getUuid());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().equals(getPacketCharacteristicUUID())){
                   //Log.d("FILE_LOAD","Data was send");
                    synchronized (mLock) {
                        if (mFirmwareUploadInProgress) {
                            mFirmwareUploadInProgress = false;
                        }
                    }
                }
                if (characteristic.getUuid().equals(getControlPointCharacteristicUUID())){
                    onPacketCharacteristicWrite(gatt,characteristic,status);
                    //Log.d("FILE_LOAD","Command was send");
                }

            } else {

            }
            notifyLock("onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            final int op_code = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            final int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            notifyLock("onCharacteristicChanged");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d("GATT","FileBluetoothCallback");
        }
    }


    FileLoadImpl(Intent intent, DfuBaseService service) {
        super(intent, service);
        mbinBuffer = new byte[16];
    }

    @Override
    public boolean initialize(Intent intent, BluetoothGatt gatt, int fileType, InputStream firmwareStream, InputStream initPacketStream, int baseAddress) throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        String mPathFile = intent.getStringExtra(DfuBaseService.EXTRA_FILE_PATH);
        mPathFile +="#";
        Log.d("FILE_LOAD","path file "+ mPathFile);
        String [] names = mPathFile.split("/");
        mFileName = names[names.length - 1];
        Log.d("FILE_LOAD","path file "+names[names.length-1] + " "+mFileName);
        return super.initialize(intent, gatt, fileType, firmwareStream, initPacketStream, baseAddress);
    }

    @Override
    public DfuGattCallback getGattCallback() {
        return mBluetoothCallback;
    }

    @Override
    public boolean isClientCompatible(Intent intent, BluetoothGatt gatt) throws DfuException, DeviceDisconnectedException, UploadAbortedException {
        Log.d("FILE_LOAD","Data written to " + gatt.getServices());
        for (BluetoothGattService service: gatt.getServices()){
            Log.d("FILE_LOAD","UUID :"+service.getUuid());
        }
        final BluetoothGattService dfuService = gatt.getService(UART_SERVICE_UUID);
        if (dfuService == null)
            return false;
        for (BluetoothGattCharacteristic ch: dfuService.getCharacteristics()){
            Log.d("FILE_LOAD","Characteristic UUID: " + ch.getUuid());
        }
        mRXCharacteristic = dfuService.getCharacteristic(UART_RX_UUID);
        BluetoothGattCharacteristic mTXCharacteristic = dfuService.getCharacteristic(UART_TX_UUID);
        mControlCharacteristic = dfuService.getCharacteristic(UART_CONTROL_UUID);

        Log.d("FILE_LOAD","Characteristic RX: " + mRXCharacteristic);
        Log.d("FILE_LOAD","Characteristic TX: " + mTXCharacteristic);
        Log.d("FILE_LOAD","Characteristic Control: " + mControlCharacteristic);

        return mRXCharacteristic != null && mTXCharacteristic != null && mControlCharacteristic != null;
    }


    @Override
    public void performDfu(Intent intent) throws DfuException, DeviceDisconnectedException, UploadAbortedException {

        int send_byte = 0;
        mNumberBlock = 0;
        total = 0;


        mEnd = false;
        mProgressInfo.setProgress(DfuBaseService.PROGRESS_STARTING);

        mService.waitFor(500);
        final BluetoothGatt gatt = mGatt;
        mService.waitFor(500);
        Log.d("FILE_LOAD","create "+mFileName + " "+mControlCharacteristic.getUuid());
        cmdCreateFile(mControlCharacteristic,mFileName);
        mService.waitFor(10);
        try {

            synchronized (mLock) {
                while ((mConnected && mError == 0 && !mAborted) || mPaused || !mEnd) {
                    rw_size = 0;
                    uploadBlockImage(mRXCharacteristic);
                    if ( rw_size == 0 ) break;
                    send_byte += rw_size;
                    mProgressInfo.setBytesSent(send_byte);
                    mLock.wait();
                }
            }
            Log.d("FILE_LOAD","PROGRESS_COMPLETED");
            mProgressInfo.setProgress(DfuBaseService.PROGRESS_COMPLETED);
            cmdCloseFile(mControlCharacteristic);

        } catch (final InterruptedException e) {
            loge("Sleeping interrupted", e);
        }
        if (mAborted)
            throw new UploadAbortedException();
        if (mError != 0)
            throw new DfuException("Unable to write Op Code", mError);
        if (!mConnected)
            throw new DeviceDisconnectedException("Unable to write Op Code: device disconnected");
    }

    @Override
    public void lastFile(int last) {

    }

    @Override
    protected UUID getControlPointCharacteristicUUID() {
        return UART_CONTROL_UUID;
    }

    @Override
    protected UUID getPacketCharacteristicUUID() {
        return UART_RX_UUID;
    }


    protected UUID getTXCharacteristicUUID() {
        return UART_TX_UUID;
    }

    protected UUID getRXCharacteristicUUID() {
        return UART_RX_UUID;
    }

    protected UUID getUARTServiceUUID() {
        return UART_SERVICE_UUID;
    }

    @Override
    protected UUID getDfuServiceUUID() {
        return null;
    }

    private int readVersion(final BluetoothGattCharacteristic characteristic) {
        return characteristic != null ? characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) : 0;
    }

    private void cmdCreateFile(final BluetoothGattCharacteristic characteristic, String name) throws UploadAbortedException, DfuException, DeviceDisconnectedException {

        mImageSizeInProgress = true;

        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        String cmd  = "0".concat(name);
        characteristic.setValue(cmd);
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_VERBOSE, "Writing to characteristic " + characteristic.getUuid());
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_DEBUG, "gatt.writeCharacteristic(" + characteristic.getUuid() + ")");
        mGatt.writeCharacteristic(characteristic);
        try {
            synchronized (mLock) {
                while ((mImageSizeInProgress && mConnected && mError == 0 && !mAborted) || mPaused) {
                    mLock.wait();
                }

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
    }

    private void cmdCloseFile(final BluetoothGattCharacteristic characteristic) throws UploadAbortedException, DfuException, DeviceDisconnectedException {

        mImageSizeInProgress = true;

        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue(new byte[20]);
        String cmd  = "2";
        characteristic.setValue(cmd);
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_VERBOSE, "Writing to characteristic " + characteristic.getUuid());
        mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_DEBUG, "gatt.writeCharacteristic(" + characteristic.getUuid() + ")");
        mGatt.writeCharacteristic(characteristic);
        try {
            synchronized (mLock) {
                while ((mImageSizeInProgress && mConnected && mError == 0 && !mAborted) || mPaused)
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
    }

    private void uploadBlockImage(final BluetoothGattCharacteristic packetCharacteristic) throws
            DfuException, UploadAbortedException {
        if (mAborted)
            throw new UploadAbortedException();

        mReceivedData = null;
        mError = 0;
        mFirmwareUploadInProgress = true;
        mPacketsSentSinceNotification = 0;

        final byte[] buffer = mbinBuffer;
        try {
            rw_size = (mFirmwareStream).read(buffer);
            if (rw_size <= 0) { // This should never happen
                synchronized (mLock) {
                    mEnd = true;
                    Log.d("FILE_LOAD","send mEnd "+mEnd);
                    mLock.notifyAll();
                }
                return;
            }
            mService.sendLogBroadcast(DfuBaseService.LOG_LEVEL_VERBOSE, "Sending firmware to characteristic " + packetCharacteristic.getUuid() + "...");
            packetCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            packetCharacteristic.setValue(buffer);
            mGatt.writeCharacteristic(packetCharacteristic);
            total +=rw_size;
        } catch (final IOException e) {
            throw new DfuException("Error while reading file", DfuBaseService.ERROR_FILE_IO_EXCEPTION);
        }
    }

    private void writeData(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final byte[] buffer, final int size) {
        if (size <= 0) { // This should never happen
            synchronized (mLock) {
                mEnd = true;
                Log.d("FILE_LOAD","send mEnd "+mEnd);
                mLock.notifyAll();
            }
            return;
        }
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue(new byte[20]);
        int crc = (int) buffer[0];
        synchronized (mLock)
        {

            for (int i = 1; i < buffer.length; i++) {
                crc ^= (int) buffer[i];
            }

            int lb = 0xff & (mNumberBlock);
            int hb = 0xff & (mNumberBlock >> 8);
            crc ^= lb;
            crc ^= hb;

            byte[] locBuffer = new byte[20];
            locBuffer[0] = (byte) crc;

            System.arraycopy(buffer, 0, locBuffer, 1, size);
            characteristic.setValue(locBuffer);
            characteristic.setValue(mNumberBlock, BluetoothGattCharacteristic.FORMAT_UINT16, 18);
        }
        gatt.writeCharacteristic(characteristic);
        total +=size;
        Log.d("FILE_LOAD","data size "+total);

    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x ", b);
        }

        return sb.toString();
    }

}
