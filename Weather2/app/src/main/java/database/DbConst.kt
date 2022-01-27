package database

import android.provider.BaseColumns

object DbConst {
    const val TABLE_NAME = "todayWeather"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "Weather.db"

    const val COLUMN_NAME_CITY = "city"
    const val COLUMN_NAME_TEMP = "temperature"
    const val COLUMN_NAME_FEELS_TEMP = "feelsTemp"
    const val COLUMN_NAME_WIND_SPEED = "windSpeed"
    const val COLUMN_NAME_PROB_PRECIP = "probPrecip"
    const val COLUMN_NAME_PRESSURE = "pressure"
    const val COLUMN_NAME_HUMIDITY = "humidity"
    const val COLUMN_NAME_ICON = "icon"

        const val CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_CITY TEXT," +
                "$COLUMN_NAME_TEMP TEXT," +
                "$COLUMN_NAME_FEELS_TEMP TEXT," +
                "$COLUMN_NAME_WIND_SPEED TEXT," +
                "$COLUMN_NAME_PROB_PRECIP TEXT," +
                "$COLUMN_NAME_PRESSURE INTEGER," +
                "$COLUMN_NAME_HUMIDITY INTEGER," +
                "$COLUMN_NAME_ICON TEXT)"

    const val DELETE_DATABASE = "DROP TABLE IF EXISTS $TABLE_NAME"
}