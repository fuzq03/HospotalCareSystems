package com.xyx.travelingshare

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.xyx.travelingshare.entity.Friend
import com.xyx.travelingshare.utils.HttpPostRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

class DetailActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var informationTextView: TextView
    private lateinit var button: Button
    private lateinit var photoImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val id = intent.getIntExtra("id",-1)
        nameTextView = findViewById(R.id.name)
        informationTextView = findViewById(R.id.information)
        button = findViewById(R.id.start)
        button.setOnClickListener {
            val intent = Intent(applicationContext,AppointmentActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("friend_name",nameTextView.text)
            startActivity(intent)
        }
        photoImageView=findViewById(R.id.photo)
        fetchInformation(id)
    }
    private fun fetchInformation(id:Int){
        val url = "http://100.65.86.80/friend/getById"
        val requestBody = FormBody.Builder()
            .add("id", id.toString())
            .build()
        HttpPostRequest().okhttpPost(url, requestBody, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Looper.prepare()
                runOnUiThread {
                    Toast.makeText(applicationContext, "post请求失败", Toast.LENGTH_SHORT).show()
                }
                Looper.loop()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body()?.string()
                Looper.prepare()
                /**
                 * 接受服务器发回来的消息，并进行判断
                 */
                if (json != "") {
                    CoroutineScope(Dispatchers.Main).launch {
                        val gson = Gson()
                        val friend = gson.fromJson(json, Friend::class.java)
                        nameTextView.text = friend.friend_name
                        informationTextView.text = friend.friend_information
                        val imageBytes = Base64.decode(friend.base64, Base64.DEFAULT)
                        Log.d("DetailActivity",friend.base64)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        photoImageView.setImageBitmap(bitmap)
                    }
                }

                Looper.loop()
            }
        })
    }
}