package com.fdhasna21.fond.utility

import android.content.Context
import android.preference.PreferenceManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer

/**
 * Created by Fernanda Hasna on 26/10/2024.
 */

object Utils {
    interface INTENT {
        companion object {
            const val DETAIL = "detail"
        }
    }

    class SPManager {
        val LONGITUDE = "longitude"
        val LATITUDE = "latitude"

        fun saveString(activity: Context?, key: String?, value: String?) {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }


        fun getString(activity: Context?, key: String?, defaultValue: String = ""): String {
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(activity)
            val value = sharedPreferences.getString(key, defaultValue) ?: ""

            return value
        }
    }

    @Throws(IOException::class)
    fun convertStreamToString(`is`: InputStream): String {
        val writer: Writer = StringWriter()
        val buffer = CharArray(2048)
        try {
            val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        } finally {
            `is`.close()
        }
        return writer.toString()
    }
}