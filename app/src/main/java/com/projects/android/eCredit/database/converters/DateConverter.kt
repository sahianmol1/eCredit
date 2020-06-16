package com.projects.android.birthdayappfinal.database.converters

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun toTimeStamp(date: Date?): Long? {
        return (date?.time)
    }

    @TypeConverter
    fun toDate(timeStamp: Long): Date? {
        return (Date(timeStamp))
    }
}