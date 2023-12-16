package com.xyx.travelingshare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import com.xyx.travelingshare.alipay.AlipayAPI
import com.xyx.travelingshare.alipay.PayResult
import com.xyx.travelingshare.entity.Appointment
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

class AppointmentActivity : AppCompatActivity() {
    private lateinit var datelayout: LinearLayout
    private lateinit var dateTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private lateinit var appointmentButton: Button

    private val SDK_PAY_FLAG = 1

    private var id:Int = 0
    private var friend_name = ""
    private var start_time = ""
    private var end_time = ""
    val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as String)
                    /**
                     * 同步返回的结果必须放置到服务端进行验证，建议商户依赖异步通知
                     */
                    val resultInfo = payResult.result // 同步返回需要验证的信息

                    val resultStatus = payResult.resultStatus
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(this@AppointmentActivity, "支付成功", Toast.LENGTH_SHORT).show()

                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(this@AppointmentActivity, "支付结果确认中", Toast.LENGTH_SHORT).show()
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(this@AppointmentActivity, "支付失败$resultInfo", Toast.LENGTH_SHORT).show()
                            val url = "http://100.65.175.3:8080/appointment/save"
                            val requestBody = FormBody.Builder()
                                .add("date",dateTextView.text.toString())
                                .add("start_time",start_time)
                                .add("end_time",end_time)
                                .add("user_id",User_All.id.toString())
                                .add("username",User_All.userName)
                                .add("friend_id",id.toString())
                                .add("friend_name",friend_name)
                                .build()
                            HttpPostRequest().okhttpPost(url, requestBody, object : okhttp3.Callback {
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
                                        Toast.makeText(applicationContext, "$json", Toast.LENGTH_SHORT).show()
                                    }
                                    val intent = Intent(this@AppointmentActivity,HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Looper.loop()
                                }
                            })
                            // 在支付成功时进行页面跳转
                            val intent = Intent(this@AppointmentActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // 可选，根据您的需求选择是否结束当前Activity
                        }
                    }
                }
            }
        }
    }
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        id = intent.getIntExtra("id",-1)
        friend_name = intent.getStringExtra("friend_name").toString()

        datelayout = findViewById(R.id.dateview)
        dateTextView = findViewById(R.id.date)
        dateTimeTextView = findViewById(R.id.datetime)
        appointmentButton = findViewById(R.id.appointmentButton)

        appointmentButton.setOnClickListener {
            AliPayThread().start()

        }

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
                    refreshItem()
                }
                .build().show()
        }
    }
    private suspend fun fetchAppointment(): List<String> = withContext(Dispatchers.IO) {
        val url = "http://100.65.175.3:8080/appointment/getByFriendId"
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
        val startList = mutableListOf<String>()

        if (json != null && response.isSuccessful) {
            val gson = Gson()
            val appointments = gson.fromJson(json, Array<Appointment>::class.java)
            startList.addAll(appointments.map { it.start_time })
            Log.d("AppointmentActivity", "Start Times: $startList")
        }

        startList
    }

    @SuppressLint("SetTextI18n")
    private fun refreshItem() {
        CoroutineScope(Dispatchers.Main).launch {
            val startList = fetchAppointment()
            val timeGroup = findViewById<RadioGroup>(R.id.timegroup)
            timeGroup.gravity = Gravity.CENTER

            // 清除RadioGroup中的所有选项
            timeGroup.clearCheck()
            timeGroup.removeAllViews()

            for (i in 8 until 18) {
                val starttime = "$i:00"
                if (!startList.contains(starttime) && i != 12 && i != 13) {
                    val radioButton = RadioButton(this@AppointmentActivity)
                    radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
                    if(i==8){
                        radioButton.text = "0$starttime-0${i + 1}:00"
                    }else if(i==9){
                        radioButton.text = "0$starttime-${i + 1}:00"
                    }else{
                        radioButton.text = "$starttime-${i + 1}:00"
                    }
                    radioButton.id = View.generateViewId()  // 自动生成唯一的id
                    radioButton.setOnClickListener {
                        dateTimeTextView.text=radioButton.text
                        start_time = starttime
                        end_time = "${i + 1}:00"
                    }
                    timeGroup.addView(radioButton)
                }
            }
        }
    }
    private inner class AliPayThread : Thread() {
        override fun run() {
            val productName = "测试的商品"
            val productDescription = "测试商品的详细描述"
            val productPrice = "0.01"

            val result = AlipayAPI.pay(this@AppointmentActivity, productName, productDescription, productPrice)
            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }
    }

}