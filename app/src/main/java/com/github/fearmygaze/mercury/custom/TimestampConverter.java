package com.github.fearmygaze.mercury.custom;

import androidx.room.TypeConverter;

import java.util.Date;

public class TimestampConverter {

    @TypeConverter
    public static Date unixToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToUnix(Date date) {
        return date == null ? 0L : date.getTime();
    }
}
