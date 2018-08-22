package ru.jufy.myposh.ui.artwork.detail;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.google.gson.Gson;
import com.jufy.mgtshr.ui.base.BaseFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.entity.Image;
import ru.jufy.myposh.entity.MarketImage;
import ru.jufy.myposh.presentation.artwork.detail.DetailArtworkMvpView;
import ru.jufy.myposh.presentation.artwork.detail.DetailArtworkPresenter;
import ru.jufy.myposh.presentation.global.RouterProvider;
import ru.jufy.myposh.ui.main.MainActivity;
import ru.jufy.myposh.ui.utils.DataLayer;

/**
 * Created by BorisDev on 07.08.2017.
 */

public class ImageFragment extends BaseFragment implements DetailArtworkMvpView {
    private static String IMAGE = "IMAGE";
    FloatingActionButton fabCancel;
    FloatingActionButton fabLikeTrash;
    FloatingActionButton fabBuyDownload;

    private PublishSubject<Boolean> likeSubject = PublishSubject.create();
    private Observable<Boolean> likeEvent = likeSubject;
    private Disposable disposable = null;
    private Boolean isLiked = false;
    private MarketImage image;

    private BluetoothDevice device = null;

    @Inject
    DetailArtworkPresenter<DetailArtworkMvpView> presenter;

    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private final static int REQUEST_STORAGE_PERMISSION_REQ_CODE = 35;
    private final static int REQUEST_PERMISSION_FORMAT = 36;
    private final static long SCAN_DURATION = 5000;

    private boolean showPoshik;
    private View fabFormat;

    public static ImageFragment newInstance(Image image) {
        Bundle args = new Bundle();

        ImageFragment fragment = new ImageFragment();
        args.putString(IMAGE, new Gson().toJson(image));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        image = new Gson().fromJson(getArguments().getString(IMAGE), MarketImage.class);

        presenter.onAttach(this);
        presenter.init(image);
        setUp(rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
        ((MainActivity) getActivity()).showBottomNav();
        super.onDestroyView();
    }

    private void setDownloadIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install, MyPoshApplication.Companion.getContext().getTheme()));
        } else {
            fabBuyDownload.setImageDrawable(getResources().getDrawable(R.drawable.icon_install));
        }
    }

    @Override
    public void installImage() {
        DataLayer.sendImageURL(getActivity(), image.getLink());

        if (getActivity() != null && ((MainActivity) getActivity()).isBLEEnabled()) {
            if (isBlePermissionGranted(REQUEST_PERMISSION_REQ_CODE)) {
                presenter.performAllBleInteractions();
            }
        } else {
            ((MainActivity) getActivity()).showBLEDialog();
        }
    }

    private boolean isBlePermissionGranted(int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Here we can show the user a message with explanation why we need this permission
                return false;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            // When user pressed Deny and still wants to use this functionality, show the rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Here we can show the user a message with explanation why we need this permission
                return;
            }

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_STORAGE_PERMISSION_REQ_CODE);

        } else {
            presenter.downloadArtwork();
        }

    }

    private void setLikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_liked);
    }

    private void setUnlikedIcon() {
        setIcon(fabLikeTrash, R.drawable.icon_like);
    }

    private void setIcon(ImageView view, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(id, MyPoshApplication.Companion.getContext().getTheme()));
        } else {
            view.setImageDrawable(getResources().getDrawable(id));
        }
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
                    presenter.performAllBleInteractions();
                } else {
                    // We can also show the user explanations why we require this permission
                    Toast.makeText(getActivity(), R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_STORAGE_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with image installation.
                    // call to download poshik on storage
                    presenter.downloadArtwork();
                } else {
                    // We can also show the user explanations why we require this permission
                    Toast.makeText(getActivity(), R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_PERMISSION_FORMAT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.ACCESS_COARSE_LOCATION permission. Now we may proceed with image installation.
                    presenter.format();
                } else {
                    // We can also show the user explanations why we require this permission
                    Toast.makeText(getActivity(), R.string.no_required_permission, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void setUp(@Nullable View rootView) {
        ImageView imageView = rootView.findViewById(R.id.bigImage);
        ProgressBar progressBar = rootView.findViewById(R.id.bigProgress);
        calculateImageSize();
        image.showMiddle(getActivity(), imageView, progressBar);

        presenter.setRouter(((RouterProvider) getParentFragment()).getRouter());

        fabCancel = rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(view -> onBackPressed());

        fabLikeTrash = rootView.findViewById(R.id.fab_like_delete);
        setupLikeEvent();

        fabFormat = rootView.findViewById(R.id.fab_format);
        fabFormat.setOnClickListener(view -> formatKulon());

        fabBuyDownload = rootView.findViewById(R.id.fab_buy_download);
        updatePurchaseState(image.isPurchased());
        fabBuyDownload.setOnClickListener(view -> presenter.buyDownloadClicked());

        ((MainActivity) getActivity()).hideBottomNav();
    }

    private void formatKulon() {
        if (getActivity() != null && ((MainActivity) getActivity()).isBLEEnabled()) {
            if (isBlePermissionGranted(REQUEST_PERMISSION_FORMAT)) {
                presenter.format();
            }
        } else {
            ((MainActivity) getActivity()).showBLEDialog();
        }
    }

    private void setupLikeEvent() {
        likeEvent
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .doOnSubscribe(d -> disposable = d)
                .throttleLast(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> presenter.toggleLike());

        fabLikeTrash.setOnClickListener(view -> {
            isLiked = !isLiked;
            updateLikeState(isLiked);
            likeSubject.onNext(isLiked);
        });
    }

    @Override
    public void updateLikeState(boolean isLiked) {
        if (isLiked) setLikedIcon();
        else setUnlikedIcon();
    }

    @Override
    public void updatePurchaseState(boolean isPurchased) {
        if (isPurchased) {
            setDownloadIcon();
            fabLikeTrash.setVisibility(View.INVISIBLE);
        } else {
            fabBuyDownload.setImageResource(R.drawable.icon_buy);
        }
    }

    @Override
    public boolean onBackPressed() {
        presenter.onBackPressed();
        return true;
    }

    @Override
    public void setupLikeState(boolean favorite) {
        isLiked = favorite;
        updateLikeState(isLiked);
    }

    @Override
    public void setupPurchaseState(boolean purchased) {
        if (!purchased) fabLikeTrash.setVisibility(View.VISIBLE);
        updatePurchaseState(image.isPurchased());
        fabBuyDownload.setOnClickListener(view -> presenter.buyDownloadClicked());
        fabBuyDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendPoshikToDevice(@Nullable File tempFile, @Nullable BluetoothDevice device, @NotNull String tempFilename) {
        if (device == null || tempFile == null) {
            return;
        }

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
                    .setFileName(tempFilename)
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
            starter.setBinOrHex(DfuService.TYPE_SOFT_DEVICE, null, tempFile.getAbsolutePath())
                    .setInitFile(null, null);
        }

        Log.d("BOOT", " send command ");
        starter.start(getActivity(), DfuService.class);
    }

    @Override
    public void formatDevice(@Nullable BluetoothDevice device) {
        if (device == null ) {
            return;
        }

        final DfuServiceInitiator starter;

        // cmd_op and filename don't impact on anything
        // this is broken service
        // need to fix
        starter = new DfuServiceInitiator(device.getAddress())
                .setDeviceName(device.getName())
                .setKeepBond(true)
                .setForceDfu(false)
                .setPacketsReceiptNotificationsEnabled(true)
                .setPacketsReceiptNotificationsValue(12)
                .setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true)
                .setCmdOrFile(true)
                .setFileName("close")
                .setCmd_op(1);


        Log.d("BOOT", " send command ");
        starter.start(getActivity(), DfuService.class);
    }
}
