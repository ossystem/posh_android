package ru.jufy.myposh.entity

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.gson.annotations.SerializedName
import ru.jufy.myposh.MyPoshApplication
import ru.jufy.myposh.R
import ru.jufy.myposh.model.data.server.response.AcqusitionParam
import ru.jufy.myposh.ui.legacy.HttpDelAsyncTask
import ru.jufy.myposh.ui.legacy.HttpGetAsyncTask
import ru.jufy.myposh.ui.legacy.HttpPostAsyncTask
import ru.jufy.myposh.ui.utils.GlideApp
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException


/**
 * Created by BorisDev on 04.08.2017.
 */

class MarketImage(id: String, val name:String, extension: String, @field:SerializedName("is_favorite")
     var isFavorite: Boolean, @field:SerializedName("is_purchased")
                   var isPurchased: Boolean, private val link: String?,
                  val artist: Artist,
                  var devices:List<DeviceInfo>,
                  @SerializedName("min_price") val minPrice:Int?,
                  @SerializedName("acquisition_params") var acqusitionParam: AcqusitionParam?)
    : Image(id, extension) {
    private val marketLinkCommonPart: StringBuilder
        get() = if (link != null) {
            StringBuilder(this.link)
        } else
            StringBuilder(image.link)

    override fun getSize(): Int {
        setSize(image.height)
        return super.getSize()
    }

    override fun canLike(): Boolean {
        return !isFavorite && !isPurchased
    }

    override fun canUnlike(): Boolean {
        return isFavorite && !isPurchased
    }

    override fun canDownload(): Boolean {
        return isPurchased
    }

    override fun showSmall(context: Context, view: ImageView, progressBar: ProgressBar) {
        /*
        StringBuilder link = getMarketLinkCommonPart();
        link.append("/img?size=small");
*/

        showImage(context, view, progressBar, marketLinkCommonPart)
    }

    override fun showMiddle(context: Context, view: ImageView, progressBar: ProgressBar) {
        val link = marketLinkCommonPart

        showImage(context, view, progressBar, link)
    }

    override fun showBig(context: Context, view: ImageView, progressBar: ProgressBar) {
        val link = marketLinkCommonPart

        showImage(context, view, progressBar, link)
    }

    private fun showImage(context: Context, view: ImageView, progressBar: ProgressBar, link: StringBuilder) {
        try {
            GlideApp
                    .with(context)
                    .load(URL(link.toString()))
                    .override(size, size)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions.errorOf(R.drawable.error))
                    .into(view)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

    }

    override fun like(): Boolean {
        val link = marketLinkCommonPart
        link.append("/fav")
        val imgFavRequest = arrayOfNulls<String>(2)
        imgFavRequest[0] = link.toString()
        imgFavRequest[1] = ""
        val reqProps = HashMap<String, String>()
        reqProps["Authorization"] = "Bearer " + MyPoshApplication.currentToken!!.token
        val postRequest = HttpPostAsyncTask()
        postRequest.setRequestProperties(reqProps)
        try {
            val postResult = postRequest.execute(*imgFavRequest).get()
                    ?: throw InterruptedException()
            isFavorite = true
            return true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return false
    }

    override fun unlike(): Boolean {
        val link = StringBuilder(MyPoshApplication.DOMAIN + "favorites/")
        link.append(id)
        val imgUnFavRequest = arrayOfNulls<String>(2)
        imgUnFavRequest[0] = link.toString()
        imgUnFavRequest[1] = ""
        val reqProps = HashMap<String, String>()
        reqProps["Authorization"] = "Bearer " + MyPoshApplication.currentToken!!.token
        val delRequest = HttpDelAsyncTask()
        delRequest.setRequestProperties(reqProps)
        try {
            val delResult = delRequest.execute(*imgUnFavRequest).get()
                    ?: throw InterruptedException()
            isFavorite = false
            return true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return false
    }

    override fun buy(): Boolean {
        val link = marketLinkCommonPart
        val imgBuyRequest = arrayOfNulls<String>(2)
        imgBuyRequest[0] = link.toString()
        imgBuyRequest[1] = ""
        val reqProps = HashMap<String, String>()
        reqProps["Authorization"] = "Bearer " + MyPoshApplication.currentToken!!.token
        val postRequest = HttpPostAsyncTask()
        postRequest.setRequestProperties(reqProps)
        try {
            val postResult = postRequest.execute(*imgBuyRequest).get()
                    ?: throw InterruptedException()
            isPurchased = true
            return true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return false
    }

    override fun download(): Boolean {
        val link = StringBuilder(MyPoshApplication.DOMAIN + "poshiks/purchase/set/")
        link.append(id)

        val getRequest = HttpGetAsyncTask()
        try {
            tempFile = createTempFile()
            getRequest.setFileToStoreImage(tempFile)
            val getResult = getRequest.execute(*Image.getRequestAuthorized(link.toString())).get()
            if (null == getResult) {
                tempFile.delete()
                throw InterruptedException()
            }

            if (getRequest.receivedDataIsBinary()) {
                return true
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return false
    }

    override fun getTempFilename(): String {
        return name
                .replace(" ", "_")
                .replace("-", "_")+ "." + getExtension()

    }

    fun getLink(): String {
        return marketLinkCommonPart.toString();
    }
}
