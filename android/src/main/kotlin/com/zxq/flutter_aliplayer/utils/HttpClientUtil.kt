package com.zxq.flutter_aliplayer.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

import javax.net.ssl.HttpsURLConnection

import android.util.Log
import org.json.JSONObject

object HttpClientUtil {
    private val CONNECTION_TIMEOUT = 10000

    fun doGet(serverUrl: String): String? {
        return if (serverUrl.startsWith("https://")) {
            doHttpsGet(serverUrl)
        } else if (serverUrl.startsWith("http://")) {
            doHttpGet(serverUrl)
        } else {
            null
        }
    }

    fun doPost(serverUrl: String, data: String): String? {
        return if (serverUrl.startsWith("https://")) {
            doHttpsPost(serverUrl, data)
        } else if (serverUrl.startsWith("http://")) {
            doHttpPost(serverUrl, data)
        } else {
            null
        }
    }


    private fun doHttpGet(serverURL: String): String? {
        var connection: HttpURLConnection? = null
        var `in`: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufr: BufferedReader? = null
        try {
            val url = URL(serverURL)
            val urlConnection = url.openConnection() as? HttpURLConnection ?: return null

            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.readTimeout = CONNECTION_TIMEOUT
            connection.connect()


            var response: StringBuilder? = null

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                `in` = connection.inputStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }

                return response.toString()
            } else {
                `in` = connection.errorStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }
                val jsonObject = JSONObject()
                jsonObject.put("StatusCode", responseCode)
                jsonObject.put("ResponseStr", response.toString())

                return jsonObject.toString()

            }
        } catch (e: Exception) {
            Log.d("HttpClientUtil", e.message)
        } finally {

            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {

                }

            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (e: IOException) {

                }

            }

            if (bufr != null) {
                try {
                    bufr.close()
                } catch (e: IOException) {

                }

            }

            connection?.disconnect()
        }
        return null
    }

    private fun doHttpsGet(serverURL: String): String? {
        var connection: HttpsURLConnection? = null
        var `in`: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufr: BufferedReader? = null
        try {
            val url = URL(serverURL)
            val urlConnection = url.openConnection() as? HttpsURLConnection ?: return null

            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.readTimeout = CONNECTION_TIMEOUT
            connection.connect()


            var response: StringBuilder? = null

            val responseCode = connection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                `in` = connection.inputStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }

                return response.toString()
            } else {
                `in` = connection.errorStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }
                val jsonObject = JSONObject()
                jsonObject.put("StatusCode", responseCode)
                jsonObject.put("ResponseStr", response.toString())

                return jsonObject.toString()

            }
        } catch (e: Exception) {
            Log.d("HttpClientUtil", e.message)
        } finally {

            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {

                }

            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (e: IOException) {

                }

            }

            if (bufr != null) {
                try {
                    bufr.close()
                } catch (e: IOException) {

                }

            }

            connection?.disconnect()
        }
        return null
    }


    private fun doHttpPost(serverURL: String, data: String): String? {
        var connection: HttpURLConnection? = null
        var `in`: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufr: BufferedReader? = null
        try {
            val url = URL(serverURL)
            val urlConnection = url.openConnection() as? HttpURLConnection ?: return null

            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.readTimeout = CONNECTION_TIMEOUT

            //设置输入流和输出流,都设置为true
            connection.doOutput = true
            connection.doInput = true

            //把提交的数据以输出流的形式提交到服务器
            val os = connection.outputStream
            os.write(data.toByteArray())

            connection.connect()


            var response: StringBuilder? = null

            val responseCode = connection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                `in` = connection.inputStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }

                return response.toString()
            } else {
                `in` = connection.errorStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                while (line != null) {
                    response.append(line)
                }
                val jsonObject = JSONObject()
                jsonObject.put("StatusCode", responseCode)
                jsonObject.put("ResponseStr", response.toString())

                return jsonObject.toString()

            }
        } catch (e: Exception) {
            Log.d("HttpClientUtil", e.message)
        } finally {

            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {

                }

            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (e: IOException) {

                }

            }

            if (bufr != null) {
                try {
                    bufr.close()
                } catch (e: IOException) {

                }

            }

            connection?.disconnect()
        }
        return null
    }


    private fun doHttpsPost(serverURL: String, data: String): String? {
        var connection: HttpsURLConnection? = null
        var `in`: InputStream? = null
        var inputStreamReader: InputStreamReader? = null
        var bufr: BufferedReader? = null
        try {
            val url = URL(serverURL)
            val urlConnection = url.openConnection() as? HttpsURLConnection ?: return null

            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.readTimeout = CONNECTION_TIMEOUT

            //设置输入流和输出流,都设置为true
            connection.doOutput = true
            connection.doInput = true

            //把提交的数据以输出流的形式提交到服务器
            val os = connection.outputStream
            os.write(data.toByteArray())

            connection.connect()


            var response: StringBuilder? = null

            val responseCode = connection.responseCode
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                `in` = connection.inputStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }

                return response.toString()
            } else {
                `in` = connection.errorStream
                //下面对获取到的输入流进行读取
                inputStreamReader = InputStreamReader(`in`)
                bufr = BufferedReader(inputStreamReader)
                response = StringBuilder()
                var line: String? = null
                line = bufr.readLine()
                while (line != null) {
                    response.append(line)
                }
                val jsonObject = JSONObject()
                jsonObject.put("StatusCode", responseCode)
                jsonObject.put("ResponseStr", response.toString())

                return jsonObject.toString()

            }
        } catch (e: Exception) {
            Log.d("HttpClientUtil", e.message)
        } finally {

            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {

                }

            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close()
                } catch (e: IOException) {

                }

            }

            if (bufr != null) {
                try {
                    bufr.close()
                } catch (e: IOException) {

                }

            }

            connection?.disconnect()
        }
        return null
    }

}
