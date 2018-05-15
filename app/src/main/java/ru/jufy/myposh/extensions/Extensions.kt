package com.jufy.mgtshr.extensions

/**
 * Created by rolea on 01.03.2018.
 */

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.LayoutRes
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import ru.jufy.myposh.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.color(colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.copyPlainText(label: String, value: String) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.primaryClip = clip
}

fun ImageView.tint(colorRes: Int) = this.setColorFilter(this.context.color(colorRes))

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun EditText.showKeyboardAndFocus(context: Context?){
    this.requestFocus()
    this.setSelection(0)
    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun BottomNavigationView.disableShiftMode() {
    val menuView = this.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView

            item.setShiftingMode(false)
            // set once again checked value, so view will be updated

            item.setChecked(item.itemData.isChecked)
        }
    } catch (e: NoSuchFieldException) {
        Log.e("BNVHelper", "Unable to get shift mode field", e)
    } catch (e: IllegalAccessException) {
        Log.e("BNVHelper", "Unable to change value of shift mode", e)
    }

}

fun TextView.showTextOrHide(str: String?) {
    this.text = str
    this.visible(!str.isNullOrBlank())
}

fun Fragment.tryOpenLink(link: String?, basePath: String? = "https://google.com/search?q=") {
    if (link != null) {
        try {
            startActivity(Intent(
                    Intent.ACTION_VIEW,
                    when {
                        URLUtil.isValidUrl(link) -> Uri.parse(link)
                        else -> Uri.parse(basePath + link)
                    }
            ))
        } catch (e: Exception) {
            startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://google.com/search?q=$link")
            ))
        }
    }
}

fun Fragment.shareText(text: String?) {
    text?.let {
        startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                },
                getString(R.string.share_to)
        ))
    }
}

fun Fragment.sendEmail(email: String?) {
    if (email != null) {
        startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null)),
                null
        ))
    }
}

fun Fragment.isTelephoneAvailable(): Boolean {
    context?.let {
        val pm = context!!.packageManager

        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
    }
    return false
}

fun Fragment.call(phone: String?) {
    if (phone != null && isTelephoneAvailable()) {
        startActivity(Intent(Intent.ACTION_DIAL,
                Uri.fromParts("tel", phone, null)))
    }
}

fun TextView.setDrawableRight(drawable: Drawable?) {
    this.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null)
}

object DateHelper {
    const val DF_SIMPLE_STRING = "HH:mm dd.MM.yyyy"
    @JvmField
    val DF_SIMPLE_FORMAT = object : ThreadLocal<DateFormat>() {
        override fun initialValue(): DateFormat {
            return SimpleDateFormat(DF_SIMPLE_STRING, Locale.getDefault())
        }
    }
}

fun dateNow(): String = Date().asString()

fun timestamp(): Long = System.currentTimeMillis()

fun Date.asString(format: DateFormat): String = format.format(this)

fun Date.asString(format: String): String = asString(SimpleDateFormat(format, Locale.getDefault()))

fun Date.asString(): String = DateHelper.DF_SIMPLE_FORMAT.get().format(this)

fun Date.humanDuration(): String {
    var now = Date(timestamp())
    val difference = now.time - this.time

    when {
        TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) > 1 -> return this.asString("dd.MM.yyyy")
        TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS).toInt() == 1 -> return "Вчера"
        TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS) in 1..23 -> {
            val hour = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS).toInt()
            return when (hour) {
                1 -> "Час назад"
                in 2..4 -> {
                    "$hour часа назад"
                }
                else -> "$hour часов назад"
            }
        }
        TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS) in 1..59 -> {
            val hour = TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS).toInt()
            return when (hour) {
                1 -> "Минуту назад"
                in 2..4 -> {
                    "$hour минуты назад"
                }
                else -> "$hour минут назад"
            }
        }
        else -> {
            val hour = TimeUnit.SECONDS.convert(difference, TimeUnit.MILLISECONDS).toInt()
            return when (hour) {
                0 -> "Сейчас"
                1 -> "Секунду назад"
                in 2..4 -> {
                    "$hour секунды назад"
                }
                else -> "$hour секунд назад"
            }
        }
    }

}

fun Context.dpToPx(valueInDp: Float): Float {
    var metrics = this.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
}



