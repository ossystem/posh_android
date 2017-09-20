package com.ble.posh.posh.ble.internal.internal;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Admin on 25.07.2017.
 */

public class BinInputStream extends FilterInputStream {

    private int localPos;
    private int available, bytesRead;
    private int size_packet;

    public BinInputStream(final InputStream in, int size_packet) throws IOException {
        super(new BufferedInputStream(in));
        int LINE_LENGTH = 128;
        byte[] localBuf = new byte[LINE_LENGTH];
        this.localPos = LINE_LENGTH; // we are at the end of the local buffer, new one must be obtained
        int size = localBuf.length;
        int lastAddress = 0;
        size = size_packet;

        this.available = calculateBinSize();
    }

    private int calculateBinSize() throws IOException {
        int binSize = 0;
        final InputStream in = this.in;

        binSize = in.available();
        in.mark(in.available());

        return  binSize;
    }

    private ArrayList<byte[]> tempStorage = new ArrayList<byte[]>();
    private ArrayList<byte[]> tempStorage128 = new ArrayList<byte[]>();
    private ArrayList<byte[]> tempStorageCrypt = new ArrayList<byte[]>();
    private byte[] byteArray;


    @Override
    public int available() {
        return available - bytesRead;
    }

    public int readPacket(byte[] buffer) throws  IOException {
        int i = 0;
        int b;
        while (i < buffer.length) {
            b = in.read();
            if (b != -1) {
                buffer[i++] = (byte) b;
                bytesRead++;
            } else
                break;
        }
        return i;
    }

    public int readBlockStorage(byte[] buffer,int numBlock) {
        System.arraycopy(tempStorage.get(numBlock),0,buffer,0,tempStorage.get(numBlock).length);
        return tempStorage.get(numBlock).length;
    }

    public int readPacket(byte[] buffer,int numBlock) throws  IOException {
        int i = 0;
        int b;
        in.reset();
        Log.d("BIN_BOOT", "pos " + 16 * (numBlock) + " block " + numBlock);
        if ( 16 * numBlock < sizeInBytes()) {
            in.skip(16 * (numBlock));
            while (i < buffer.length) {
                b = in.read();
                if (b != -1) {
                    buffer[i++] = (byte) b;
                    bytesRead++;
                } else
                    break;
            }
        } else {
            in.skip(16 * (numBlock));
            while (i < buffer.length) {
                b = in.read();
                if (b != -1) {
                    buffer[i++] = (byte) b;
                    bytesRead++;
                } else
                    break;
            }
        }
        Log.d("BIN_BOOT","i "+i);
        return i;
    }
    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return readPacket(buffer);
    }

    public int readBlock(byte[] buffer,int numberBlock) throws IOException {
        return readPacket(buffer,numberBlock);
    }

    @Override
    public int read(byte[] buffer, int offset, int count) throws IOException {
        throw new UnsupportedOperationException("Please, use readPacket() method instead");
    }

    public int sizeInBytes() {
        return available;
    }

    public int sizeInPackets(final int packetSize) throws IOException {
        final int sizeInBytes = sizeInBytes();

        return sizeInBytes / packetSize + ((sizeInBytes % packetSize) > 0 ? 1 : 0);
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();

        int pos = 0;
        bytesRead = 0;
        localPos = 0;
    }

}
