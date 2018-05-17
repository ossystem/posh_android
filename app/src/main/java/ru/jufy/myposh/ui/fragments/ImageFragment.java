package ru.jufy.myposh.ui.fragments;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ble.posh.posh.DfuService;
import com.ble.posh.posh.ble.DfuServiceInitiator;
import com.ble.posh.posh.scanner.BluetoothLeScannerCompat;
import com.ble.posh.posh.scanner.ScanCallback;
import com.ble.posh.posh.scanner.ScanResult;
import com.ble.posh.posh.scanner.ScanSettings;

import java.util.List;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.main.MainActivity;
import ru.jufy.myposh.entity.Image;

/**
 * Created by BorisDev on 07.08.2017.
 */

public class ImageFragment extends Fragment {
    private View rootView;
    FloatingActionButton fabCancel;
    FloatingActionButton fabLikeTrash;
    FloatingActionButton fabSet;
    FloatingActionButton fabBuyDownload;
    private Image image;

    private boolean mIsScanning = false;
    private final Handler mHandler = new Handler();
    private BluetoothDevice device = null;

    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private final static long SCAN_DURATION = 5000;

    private boolean showPoshik;

    public ImageFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.bigImage);
        ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.bigProgress);
        calculateImageSize();
        image.showMiddle(getActivity(), imageView, progressBar);

        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showCurrentFragment();
            }
        });

        fabLikeTrash = (FloatingActionButton)rootView.findViewById(R.id.fab_like_delete);
        if (image.canUnlike()) {
            setLikedIcon();
        } else if (image.canLike()) {
            setUnlikedIcon();
        } else if (image.canDelete()) {
            setTrashIcon();
        } else {
            fabLikeTrash.setVisibility(View.INVISIBLE);
        }
        fabLikeTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.canUnlike()) {
                    onUnLike();
                } else if (image.canLike()) {
                    onLike();
                } else if (image.canDelete()) {
                    onDelete();
                }
            }
        });

        fabSet = (FloatingActionButton)rootView.findViewById(R.id.fab_set);
        fabSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPoshik = true;
                installImage();
            }
        });

        fabBuyDownload = (FloatingActionButton)rootView.findViewById(R.id.fab_buy_download);
        if (image.canDownload()) {
            setDownloadIcon();
        }
        fabBuyDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (image.available()) {
                    Toast.makeText(getActivity(), R.string.image_available, Toast.LENGTH_SHORT).show();
                    installImage();
                }
                else */if (image.canDownload()) {
                    if (downloadPoshik()) {
                        showPoshik = false;
                        installImage();
                    }
                } else {
                    buyImage();
                }
            }
        });

        ((MainActivity)getActivity()).hideBottomNav();

        return rootView;
    }

    private void setDownloadIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install, MyPoshApplication.Companion.getContext().getTheme()));
        } else {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install));
        }
    }

    private void installImage() {
        if (((MainActivity)getActivity()).isBLEEnabled()) {
            if (permissionGranted()) {
                performAllBleInteractions();
            }
        } else {
            ((MainActivity)getActivity()).showBLEDialog();
        }
    }

    private boolean permissionGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Here we can show the user a message with explanation why we need this permission
                return false;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    private boolean permissionGrantedStorage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Here we can show the user a message with explanation why we need this permission
                return false;
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Here we can show the user a message with explanation why we need this permission
                return false;
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    public void performAllBleInteractions() {
        if (!mIsScanning) {
            scan();
        }
    }

    private boolean scan() {
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build();
        scanner.startScan(null, settings, scanCallback);

        mIsScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                if (null == device) {
                    Toast.makeText(getActivity(), R.string.no_device_scanned, Toast.LENGTH_SHORT).show();
                }
            }
        }, SCAN_DURATION);
        return false;
    }

    private void stopScan() {
        if (mIsScanning) {
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);
            mIsScanning = false;
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            // do nothing
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            for (ScanResult result : results) {
                if (null != result.getScanRecord() && null != result.getScanRecord().getDeviceName() && result.getScanRecord().getDeviceName().equals("Posh")) {
                    device = result.getDevice();
                    stopScan();
                    Toast.makeText(getActivity(), R.string.device_scanned, Toast.LENGTH_SHORT).show();
                    setPoshik();
                    return;
                }
            }
        }

        @Override
        public void onScanFailed(final int errorCode) {
            // should never be called
        }
    };

    private void setPoshik() {
        final DfuServiceInitiator starter;
        if (showPoshik) {
            starter = new DfuServiceInitiator(device.getAddress())
                    .setDeviceName(device.getName())
                    .setKeepBond(true)
                    .setForceDfu(false)
                    .setPacketsReceiptNotificationsEnabled(true)
                    .setPacketsReceiptNotificationsValue(12)
                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                    .setCmdOrFile(true)
                    .setFileName(image.getTempFilename())
                    .setCmd_op(1);
        } else {
            starter = new DfuServiceInitiator(device.getAddress())
                    .setDeviceName(device.getName())
                    .setKeepBond(true)
                    .setForceDfu(false)
                    .setPacketsReceiptNotificationsEnabled(true)
                    .setPacketsReceiptNotificationsValue(12)
                    .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                    .setCmdOrFile(false);
            starter.setBinOrHex(DfuService.TYPE_SOFT_DEVICE, null, image.getDownloadedFile().getAbsolutePath()).setInitFile(null, null);
        }

        Log.d("BOOT", " send command ");
        starter.start(getActivity(), DfuService.class);
    }

    private boolean downloadPoshik() {
        permissionGrantedStorage();
        if (image.download()) {
            Toast.makeText(getActivity(), R.string.image_downloaded, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getActivity(), R.string.image_download_failed, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void buyImage() {
        if (image.buy()) {
            setDownloadIcon();
            fabLikeTrash.setVisibility(View.INVISIBLE);
        }
    }

    private void setLikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_liked);
    }

    private void setUnlikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_like);
    }

    private void setTrashIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_trash);
    }

    private void setIcon(ImageView view, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(id, MyPoshApplication.Companion.getContext().getTheme()));
        } else {
            view.setImageDrawable(getResources().getDrawable(id));
        }
    }

    private void onUnLike() {
        if (image.unlike()) {
            setUnlikedIcon();
        }
    }

    private void onLike() {
        if (image.like()) {
            setLikedIcon();
        }
    }

    private void onDelete() {
        if (image.delete()) {
            ((MainActivity)getActivity()).showCurrentFragment();
        } else {
            Toast.makeText(getActivity(), R.string.image_deletion_failed, Toast.LENGTH_LONG).show();
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private void calculateImageSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.image.setSize(size.x);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with image installation.
                    performAllBleInteractions();
                } else {
                    // We can also show the user explanations why we require this permission
                    Toast.makeText(getActivity(), R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
