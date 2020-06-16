package com.projects.android.birthday.database

import androidx.room.TypeConverter
import java.sql.Time

class TimeConverter {

    @TypeConverter
    fun toTimeStamp(time: Time?): Long? {
        return (time?.time)
    }

    @TypeConverter
    fun toTime(timestamp: Long): Time? {
        return (Time(timestamp))
    }
}