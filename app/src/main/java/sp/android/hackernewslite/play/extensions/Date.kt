package sp.android.hackernewslite.play.extensions

import android.content.Context
import sp.android.hackernewslite.play.R
import java.text.SimpleDateFormat
import java.util.*

fun Date?.relativeDateFormat(context: Context, now: Date = Date()): String? {
    if (this == null) {
        return null
    }
    val defaultLocale = Locale.getDefault()
    val timeDateFormat =
        SimpleDateFormat(context.getString(R.string.time_only_date_format), defaultLocale)
    val dayOfWeekDateFormat =
        SimpleDateFormat(context.getString(R.string.day_of_week_date_format), defaultLocale)
    val shortFormDateFormat =
        SimpleDateFormat(context.getString(R.string.short_form_date_format), defaultLocale)
    val longFormDateFormat =
        SimpleDateFormat(context.getString(R.string.long_form_date_format), defaultLocale)
    val formatter: SimpleDateFormat
    formatter = when {
        this.after(now.getMidnight()) -> timeDateFormat
        this.after(now.getPreviousDay()) -> return "Yesterday"
        this.after(now.getBeginningOfWeek()) -> dayOfWeekDateFormat
        this.after(now.getFirstOfYear()) -> shortFormDateFormat
        else -> longFormDateFormat
    }
    return formatter.format(this)
}

fun Date.getMidnight(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.getPreviousDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this.getMidnight()
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    return calendar.time
}

fun Date.getBeginningOfWeek(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this.getMidnight()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    return calendar.time
}

fun Date.getFirstOfYear(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this.getMidnight()
    calendar.set(Calendar.MONTH, 0)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    return calendar.time
}