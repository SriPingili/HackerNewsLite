package sp.android.hackernewslite.play.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import sp.android.hackernewslite.play.R


/**
 * Utility class to load the custom tabs.
 * By default has no start animation, has a slide right to left exit animation, and a black close button
 */
class CustomTabsUtil(context: Context) {
    private val context: Context
    private val builder = CustomTabsIntent.Builder()

    /**
     * Will make the generated custom tab use a black back arrow
     */
    fun setToUseBackArrow() {
        val drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_custom_tab_arrow_back_24dp)
        val backArrowBitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(backArrowBitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        builder.setCloseButtonIcon(backArrowBitmap)
    }

    fun openCustomTab(url: String?) {
        val customTabsIntent = builder.build()
        try {
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open this page", Toast.LENGTH_SHORT).show()
        }

    }

    init {
        builder.setExitAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
            .setStartAnimations(context, 0, 0)
        builder.setToolbarColor(context.resources.getColor(R.color.colorPrimary))
        this.context = context
    }
}