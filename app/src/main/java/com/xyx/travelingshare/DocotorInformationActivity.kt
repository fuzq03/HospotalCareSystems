package com.xyx.travelingshare

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import com.xyx.travelingshare.entity.Appointment
import com.xyx.travelingshare.entity.Friend
import com.xyx.travelingshare.entity.User_All
import com.xyx.travelingshare.utils.HttpPostRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DocotorInformationActivity : AppCompatActivity() {
    private lateinit var photoImage:ImageView
    private lateinit var nameTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var informationTextView: TextView
    private lateinit var datelayout: LinearLayout
    private lateinit var dateTextView: TextView
    private lateinit var editTextView: TextView
    private lateinit var appointlayout:LinearLayout
    private lateinit var dialogView:View
    private var friend:Friend? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docotor_information)
        val id = intent.getIntExtra("id",-1)
        initData(id)
    }
    @SuppressLint("ResourceAsColor", "CutPasteId")
    private fun initData(id:Int){
        photoImage = findViewById(R.id.photo)
        nameTextView = findViewById(R.id.name)
        typeTextView = findViewById(R.id.type)
        informationTextView = findViewById(R.id.information)
        datelayout = findViewById(R.id.dateview)
        dateTextView = findViewById(R.id.date)
        editTextView = findViewById(R.id.edit)
        appointlayout = findViewById(R.id.appointlayout)
        dialogView = LayoutInflater.from(this).inflate(R.layout.edit_dialog, null)
        fetchInformation(id)
        /**
         * 点击之后弹出编辑资料的Dialog
         * 在Dialog中编辑自己的账户名称、密码和信息
         * 点击确认后成功修改，并且重新渲染界面
         */
        editTextView.setOnClickListener {
            dialogView.findViewById<EditText>(R.id.editName).hint = dialogView.findViewById<EditText>(R.id.editName).hint.toString() + friend?.friend_name
            dialogView.findViewById<EditText>(R.id.editPassword).hint = dialogView.findViewById<EditText>(R.id.editPassword).hint.toString()+friend?.password
            dialogView.findViewById<EditText>(R.id.editInformation).hint = dialogView.findViewById<EditText>(R.id.editInformation).hint.toString()+friend?.friend_information
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("编辑资料")
                .setPositiveButton("确认") { dialog, which ->
                    var newName = dialogView.findViewById<EditText>(R.id.editName).text.toString()
                    if (newName==""){
                        newName = friend?.friend_name.toString()
                    }
                    var newPassword = dialogView.findViewById<EditText>(R.id.editPassword).text.toString()
                    if(newPassword ==""){
                        newPassword = friend?.password.toString()
                    }
                    var newInformation = dialogView.findViewById<EditText>(R.id.editInformation).text.toString()
                    if(newInformation == ""){
                        newInformation = friend?.friend_information.toString()
                    }
                    val friend1 = friend?.id?.let { it1 -> friend?.friend_type?.let { it2 ->
                        friend?.base64?.let { it3 ->
                            Friend(it1,newName,
                                it2,newInformation,newPassword.toInt(), it3
                            )
                        }
                    } }
                    Log.d("666","$friend1")
                    // 执行修改操作，并重新渲染界面
                    updateUserInformation(id,friend1)
                }
                .setNegativeButton("取消", null)

            val dialog = dialogBuilder.create()
            dialog.show()
        }
        /**
         * 设置时间选择器
         */
        datelayout.setOnClickListener {
            val datelist = mutableListOf<Int>()
            datelist.add(DateTimeConfig.YEAR)
            datelist.add(DateTimeConfig.MONTH)
            datelist.add(DateTimeConfig.DAY)
            CardDatePickerDialog.builder(this)
                .setTitle("预约日期")
                .setBackGroundModel(CardDatePickerDialog.STACK)
                .showBackNow(true)
                .setWrapSelectorWheel(false)
                .setThemeColor(R.color.green)
                .showDateLabel(true)
                .showFocusDateInfo(true)
                .setLabelText("年", "月", "日", "时", "分")
                .setDisplayType(datelist) // 仅显示年月日
                .setOnCancel("关闭") {}
                .setOnChoose("确认") { millisecond ->
                    val selectedDate = SimpleDateFormat("yyyy-MM-dd").format(Date(millisecond))
                    dateTextView.text = selectedDate
                    refreshItem(id)
                }
                .build().show()
        }

    }

    /**
     * 向后台发送修改请求
     */
    private fun updateUserInformation(id:Int,friend1: Friend?) = CoroutineScope(Dispatchers.IO).launch{
        val url = "http://100.65.86.80/friend/update"
        val requestBody = FormBody.Builder()
            .add("id", id.toString())
            .add("name", friend1?.friend_name)
            .add("password", friend1?.password.toString())
            .add("information",friend1?.friend_information)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        val json = response.body()?.string()
        withContext(Dispatchers.Main) {
            initData(id)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun refreshItem(id:Int){
        CoroutineScope(Dispatchers.Main).launch {
            val appointments = fetchAppointment(id)
            Log.d("666","$appointments")
            appointlayout.removeAllViews()
            for (i in 8 until 18){
                if(i != 12 && i != 13){
                    val view_date = LayoutInflater.from(this@DocotorInformationActivity).inflate(R.layout.dateitem, appointlayout, false)
                    view_date.findViewById<TextView>(R.id.date_time).text = "$i:00-${i+1}:00"
                    view_date.findViewById<TextView>(R.id.date_time).setTextColor(R.color.black)
                    appointlayout.addView(view_date)

                    for (appoint in appointments) {
                        Log.d("666",appoint.start_time)
                        if(appoint.start_time == "$i:00"){
                            val view_appoint = LayoutInflater.from(this@DocotorInformationActivity).inflate(R.layout.appintmentitem, appointlayout, false)
                            view_appoint.findViewById<TextView>(R.id.appointUser).text = view_appoint.findViewById<TextView>(R.id.appointUser).text.toString()+"${appoint.username}"
                            view_appoint.findViewById<Button>(R.id.delete).setOnClickListener {
                                fetchDelete(appoint.id,id)
                                appointlayout.removeView(view_appoint)
                            }
                            appointlayout.addView(view_appoint)
                        }
                    }
                }
            }
        }
    }

    /**
     * 从后端发送获取预约信息请求
     */
    private suspend fun fetchAppointment(id:Int): List<Appointment> = withContext(Dispatchers.IO) {
        val url = "http://100.65.86.80/appointment/getByFriendId"
        val requestBody = FormBody.Builder()
            .add("id", id.toString())
            .add("date", dateTextView.text.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        val json = response.body()?.string()
        var appointments = mutableListOf<Appointment>()

        if (json != null && response.isSuccessful) {
            val gson = Gson()
            appointments = gson.fromJson(json, Array<Appointment>::class.java).toMutableList()
        }
        appointments
    }

    /**
     * 后台发送请求获取医生信息
     */
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
                        friend = gson.fromJson(json, Friend::class.java)
                        nameTextView.text = friend?.friend_name
                        typeTextView.text = friend?.friend_type
                        informationTextView.text = friend?.friend_information
                        val imageBytes = Base64.decode(friend?.base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        photoImage.setImageBitmap(bitmap)
                    }
                }

                Looper.loop()
            }
        })
    }
    private fun fetchDelete(id1: Int,id: Int) = CoroutineScope(Dispatchers.IO).launch {
        val url = "http://100.65.86.80/appointment/deleteAppointment"
        val requestBody = FormBody.Builder()
            .add("id", id1.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        val json = response.body()?.string()
        withContext(Dispatchers.Main) {
            initData(id)
        }
    }
}