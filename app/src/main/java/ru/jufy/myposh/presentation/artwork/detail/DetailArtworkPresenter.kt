package ru.jufy.myposh.presentation.artwork.detail

import android.bluetooth.BluetoothDevice
import android.os.Handler
import com.ble.posh.posh.scanner.BluetoothLeScannerCompat
import com.ble.posh.posh.scanner.ScanCallback
import com.ble.posh.posh.scanner.ScanResult
import com.ble.posh.posh.scanner.ScanSettings
import com.jufy.mgtshr.ui.base.ChildFragmentPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.jufy.myposh.R
import ru.jufy.myposh.entity.MarketImage
import ru.jufy.myposh.model.interactor.DetailArtworkInteractor
import ru.jufy.myposh.model.interactor.LikeArtworkInteractor
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.global.ErrorHandler
import javax.inject.Inject

class DetailArtworkPresenter<V : DetailArtworkMvpView> @Inject constructor(val interactor: DetailArtworkInteractor,
                                                                           val likeArtworkInteractor: LikeArtworkInteractor,
                                                                           val resourceManager: ResourceManager,
                                                                           val errorHandler: ErrorHandler)
    : ChildFragmentPresenter<V>() {

    private val SCAN_DURATION: Long = 5000
    private var mIsScanning = false
    private val mHandler = Handler()
    private var device: BluetoothDevice? = null

    fun init(artwork: MarketImage) {
        interactor.artwork = artwork
        likeArtworkInteractor.artwork = artwork
        interactor.loadArtwork()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    getMvpView()?.setupLikeState(artwork.isFavorite)
                    getMvpView()?.setupPurchaseState(artwork.isPurchased)
                }, {})
    }

    fun buyDownloadClicked() {
        if (interactor.artwork.canDownload()) {
            // since we need download posh to storage and kulon
            // we should check permission on storage and coarse location
            getMvpView()?.checkPermissions()
        } else {
            buy()
        }
    }

    fun downloadArtwork() {
        // download image to storage
        interactor.download()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    // then download image to kulon
                    getMvpView()?.showMessage(resourceManager.getString(R.string.image_downloaded))
                    // check permissions
                    getMvpView()?.installImage()
                }, {
                    errorHandler.proceed(it, { getMvpView()?.onError(it) })
                })

    }

    fun performAllBleInteractions() {
        if (!mIsScanning) {
            scan()
        }
    }

    private fun scan(): Boolean {
        val scanner = BluetoothLeScannerCompat.getScanner()
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(1000).setUseHardwareBatchingIfSupported(false).build()
        scanner.startScan(null, settings, scanCallback)

        mIsScanning = true
        mHandler.postDelayed({
            stopScan()
            if (null == device) {
                //Toast.makeText(getActivity(), R.string.no_device_scanned, Toast.LENGTH_SHORT).show()
            }
        }, SCAN_DURATION)
        return false
    }


    private fun stopScan() {
        if (mIsScanning) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            mIsScanning = false
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // do nothing
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            for (result in results) {
                if (null != result.scanRecord && null != result.scanRecord!!.deviceName && result.scanRecord!!.deviceName == "Posh") {
                    device = result.device
                    stopScan()
                    getMvpView()?.showMessage(resourceManager.getString(R.string.device_scanned))
                  //  Toast.makeText(getActivity(), R.string.device_scanned, Toast.LENGTH_SHORT).show()
                    getMvpView()?.sendPoshikToDevice(interactor.artwork.downloadedFile, device, interactor.artwork.tempFilename)
                    //setPoshik()
                    return
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // should never be called
        }
    }




    private fun buy() {
        interactor.buy()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposable.add(it)
                    getMvpView()?.updatePurchaseState(true)
                    //getMvpView().showProgress()
                }
                .subscribe({
                    getMvpView()?.showMessage(it.message!!)
                }) {
                    errorHandler.proceed(it, {
                        getMvpView()?.updatePurchaseState(false)
                        getMvpView()?.showMessage(it)
                    })
                }
    }


    fun toggleLike() {
        likeArtworkInteractor
                .toggleLike()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable.add(it) }
                .subscribe({
                    getMvpView()?.showMessage(it.message!!)
                }, { errorHandler.proceed(it, { getMvpView()?.showMessage(it) }) })

    }
}