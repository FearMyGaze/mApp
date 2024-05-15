package com.github.fearmygaze.mercury.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {

    @TypeConverter
    public static Date unixToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToUnix(Date date) {
        return date == null ? 0L : date.getTime();
    }


}
