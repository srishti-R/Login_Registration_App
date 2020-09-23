package com.example.login_registration

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_login.setOnClickListener {
            attemptLogin()
        }

        button_register.setOnClickListener {
            attemptRegister()
        }
    }

    private fun attemptRegister() {
        object: Thread(){
            override fun run() {
                openHttpPostConnection("http://192.168.0.6:8282/register")
            }

        }.start()
    }

    private fun attemptLogin(){
        object: Thread(){
            override fun run() {
                openHttpGetConnection("http://192.168.0.6:8282/login/${et_username.text}")
            }
        }.start()
    }

    private fun openHttpGetConnection(url:String){
        val resCode: Int
        try {
            val url1 = URL(url)
            val urlConn = url1.openConnection() as? HttpURLConnection ?: throw IOException("URL is not an Http URL")
            urlConn.allowUserInteraction = false
            urlConn.requestMethod = "GET"
            urlConn.setRequestProperty("Accept", "application/json")
            urlConn.connect()
            resCode = urlConn.responseCode
            Log.d("res", resCode.toString())
            if (resCode == HttpURLConnection.HTTP_OK || resCode==226) {
                val data = urlConn.inputStream.bufferedReader().readText()
                Log.d("Data:",data)
                val json=JSONObject(data)
                if(et_password.text.toString() == json.get("password"))
                    showToast("Login Successful")
            }
            urlConn.disconnect()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun openHttpPostConnection(urlStr: String) {
        var resCode = -1
        try {
            val url = URL(urlStr)
            val urlConn = url.openConnection() as? HttpURLConnection ?: throw IOException("URL is not an Http URL")
           urlConn.allowUserInteraction = false
           urlConn.instanceFollowRedirects = true
           urlConn.requestMethod = "POST"
           urlConn.setRequestProperty("Content-Type", "application/json; utf-8")
           urlConn.setRequestProperty("Accept", "application/json")
           urlConn.doOutput = true
           urlConn.doInput=true
            val obj=JSONObject()
            obj.put("username", et_username.text)
            obj.put("password", et_password.text)
            Log.d("obj", obj.toString())
            val dataOutputStream=DataOutputStream(urlConn.outputStream)
            dataOutputStream.writeBytes(obj.toString())
            dataOutputStream.flush()
            dataOutputStream.close()
            urlConn.connect()
            resCode = urlConn.responseCode
            Log.d("mess",urlConn.responseMessage)
            Log.d("res", resCode.toString())
            if (resCode == HttpURLConnection.HTTP_OK || resCode==208) {
                val data = urlConn.inputStream.bufferedReader().readText()
                Log.d("Data:",data)
                showToast("Registration successful")
            }
            urlConn.disconnect()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun showToast(text:String){
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
        }
    }
}