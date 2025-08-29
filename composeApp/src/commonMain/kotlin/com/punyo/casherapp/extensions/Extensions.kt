package com.punyo.casherapp.extensions

import kotlinx.datetime.LocalDateTime

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
