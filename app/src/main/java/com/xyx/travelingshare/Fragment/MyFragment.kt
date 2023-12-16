package com.xyx.travelingshare.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.xyx.travelingshare.HomeActivity
import com.xyx.travelingshare.R
import com.xyx.travelingshare.entity.Appointment
import com.xyx.travelingshare.entity.User
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

class MyFragment : Fragment() {
    private lateinit var userTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var memolayout:LinearLayout

    companion object {
        private const val TAG = "MyFragment"
        private const val ARG_POSITION = "Position"

        fun newInstance(position: Int): MyFragment {
            val bundle = Bundle().apply {
                putInt(ARG_POSITION, position)
            }
            val fragment = MyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabIndex = arguments?.getInt(ARG_POSITION)
        Log.d(TAG, "$tabIndex fragment onCreate")
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.myfragment, container, false)
        userTextView = view.findViewById(R.id.username)
        emailTextView = view.findViewById(R.id.email)
        addressTextView = view.findViewById(R.id.address)
        memolayout = view.findViewById(R.id.memolayout)
        initData()
        return view
    }
    @SuppressLint("MissingInflatedId")
    private fun initData(){
        userTextView.text = userTextView.text.toString()+User_All.userName
        emailTextView.text = emailTextView.text.toString()+User_All.email
        addressTextView.text = addressTextView.text.toString()+User_All.address
        val url = "http://100.65.175.3:8080/appointment/getByUserId"
        val requestBody = FormBody.Builder()
            .add("id", User_All.id.toString())
            .build()
        HttpPostRequest().okhttpPost(url, requestBody, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Looper.prepare()
                Toast.makeText(context, "post请求失败", Toast.LENGTH_SHORT).show()
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
                        val appointments = gson.fromJson(json, Array<Appointment>::class.java)
                        withContext(Dispatchers.Main) {
                            memolayout.removeAllViews()
                            for (appointment in appointments) {
                                val view = LayoutInflater.from(context).inflate(R.layout.memoitem, memolayout, false)
                                view.findViewById<TextView>(R.id.friendName).text =
                                    view.findViewById<TextView>(R.id.friendName).text.toString() + appointment.friendname
                                view.findViewById<TextView>(R.id.date).text =
                                    view.findViewById<TextView>(R.id.date).text.toString() + appointment.date
                                view.findViewById<TextView>(R.id.time).text =
                                    view.findViewById<TextView>(R.id.time).text.toString() + appointment.start_time + "-" + appointment.end_time
                                view.findViewById<Button>(R.id.delete).setOnClickListener {
                                    fetchDelete(appointment.id)
                                }
                                memolayout.addView(view)
                            }
                        }
                    }
                }

                Looper.loop()
            }
        })
    }
    private fun fetchDelete(id: Int) = CoroutineScope(Dispatchers.IO).launch {
        val url = "http://100.65.175.3:8080/appointment/deleteAppointment"
        val requestBody = FormBody.Builder()
            .add("id", id.toString())
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        val json = response.body()?.string()
        withContext(Dispatchers.Main) {
            initData()
        }
    }
}