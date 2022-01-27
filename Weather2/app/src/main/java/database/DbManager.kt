package database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DbManager (context: Context){
    private val dbHelper = DbHelper(context)
    private var dbWritable : SQLiteDatabase? = null
    private var dbReadable : SQLiteDatabase? = null

    fun openDb(){
        dbWritable = dbHelper.writableDatabase
        dbReadable = dbHelper.readableDatabase
    }

    fun insertDb(dataArray : ArrayList<String>){
        val values = ContentValues().apply {
            put(DbConst.COLUMN_NAME_CITY, dataArray[0])
            put(DbConst.COLUMN_NAME_TEMP, dataArray[1])
            put(DbConst.COLUMN_NAME_FEELS_TEMP,dataArray[2])
            put(DbConst.COLUMN_NAME_WIND_SPEED, dataArray[3])
            put(DbConst.COLUMN_NAME_PROB_PRECIP, dataArray[4])
            put(DbConst.COLUMN_NAME_PRESSURE, dataArray[5])
            put(DbConst.COLUMN_NAME_HUMIDITY, dataArray[6])
            put(DbConst.COLUMN_NAME_ICON, dataArray[7])
        }

        dbWritable?.insert(DbConst.TABLE_NAME,null,values)
    }

    fun updateDb(dataArray : ArrayList<String>){
        val values = ContentValues().apply {
            put(DbConst.COLUMN_NAME_CITY, dataArray[0])
            put(DbConst.COLUMN_NAME_TEMP, dataArray[1])
            put(DbConst.COLUMN_NAME_FEELS_TEMP,dataArray[2])
            put(DbConst.COLUMN_NAME_WIND_SPEED, dataArray[3])
            put(DbConst.COLUMN_NAME_PROB_PRECIP, dataArray[4])
            put(DbConst.COLUMN_NAME_PRESSURE, dataArray[5])
            put(DbConst.COLUMN_NAME_HUMIDITY, dataArray[6])
            put(DbConst.COLUMN_NAME_ICON, dataArray[7])
        }

        dbWritable?.update(DbConst.TABLE_NAME,values,"_ID=?", arrayOf("1"))
    }

    fun readDb(): ArrayList<String> {
        val data = ArrayList<String>()

        val cursor = dbReadable?.query(
            DbConst.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_CITY)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_TEMP)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_FEELS_TEMP)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_WIND_SPEED)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_PROB_PRECIP)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_PRESSURE)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_HUMIDITY)))
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(DbConst.COLUMN_NAME_ICON)))
        }
        cursor.close()
        return data
    }

    fun checkIsDbEmpty() : Boolean{
        val mCursor= dbReadable!!.rawQuery("SELECT * FROM ${DbConst.TABLE_NAME}", null)
        val rowExists: Boolean = mCursor.moveToFirst()
        mCursor.close()
        return rowExists
    }
}