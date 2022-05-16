package com.amity.imagefeed

import android.content.Context
import java.util.concurrent.TimeUnit

fun Long.readableFeedPostTime(context: Context): String {
    val diff = System.currentTimeMillis() - this
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

    return when {
        days > 0 -> context.resources.getQuantityString(
            R.plurals.amity_number_of_days,
            days.toInt(),
            days
        )
        hours > 0 -> context.resources.getQuantityString(
            R.plurals.amity_number_of_hours,
            hours.toInt(),
            hours
        )
        minutes > 0 -> context.resources.getQuantityString(
            R.plurals.amity_number_of_mins,
            minutes.toInt(),
            minutes
        )
        else -> context.getString(R.string.amity_just_now)
    }
}