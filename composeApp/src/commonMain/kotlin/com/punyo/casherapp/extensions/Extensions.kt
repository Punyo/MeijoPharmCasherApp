package com.punyo.casherapp.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.startOfDay(): LocalDateTime = LocalDateTime(
    year = year,
    monthNumber = monthNumber,
    dayOfMonth = dayOfMonth,
    hour = 0,
    minute = 0,
    second = 0,
)

fun LocalDateTime.endOfDay(): LocalDateTime = LocalDateTime(
    year = year,
    monthNumber = monthNumber,
    dayOfMonth = dayOfMonth,
    hour = 23,
    minute = 59,
    second = 59,
    nanosecond = 999_999_999,
)

fun Instant.toDateString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val dateTime = this.toLocalDateTime(timeZone)
    return "${dateTime.year}/${dateTime.monthNumber.toString().padStart(2, '0')}/${
        dateTime.dayOfMonth.toString().padStart(2, '0')
    } ${dateTime.hour.toString().padStart(2, '0')}:${
        dateTime.minute.toString().padStart(2, '0')
    }:${dateTime.second.toString().padStart(2, '0')}"
}
