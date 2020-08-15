package sp.android.hackernewslite.play.util

import android.content.Context
import android.content.Intent
import android.net.Uri

class AppUtil {

    companion object {

        /*
        * launches share intent
        * */
        fun startShareIntent(string: String, context: Context) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                string
            )
            sendIntent.type = "text/plain"
            context.startActivity(sendIntent)
        }

        /*
        * launches play store
        * */
        fun startPlayStoreIntent(stringUrl: String, context: Context) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(stringUrl)
                )
            )
        }
    }
}