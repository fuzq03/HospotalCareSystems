package com.xyx.travelingshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.xyx.travelingshare.entity.User
import com.xyx.travelingshare.entity.User_All
import com.xyx.travelingshare.utils.HttpPostRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var userText:TextView
    private lateinit var passWardText:TextView
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var noticeTextView: TextView
    private lateinit var ChangeToDoctorButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFace()
    }
    private fun initFace(){
        userText = findViewById(R.id.uerName)
        passWardText = findViewById(R.id.passWord)
        noticeTextView = findViewById(R.id.notice)
        loginButton = findViewById(R.id.login)
        signUpButton = findViewById(R.id.signUp)
        ChangeToDoctorButton = findViewById(R.id.change_to_doctor)

        ChangeToDoctorButton.setOnClickListener {
            val intent = Intent(applicationContext,DocotorActivity::class.java)
            startActivity(intent)
            finish()
        }
        /**
         * 注册按钮事件
         */
        signUpButton.setOnClickListener{
            val intent = Intent(applicationContext,SignUpActivity::class.java)
            startActivity(intent)
        }
        /**
         * 登录按钮事件
         */
        loginButton.setOnClickListener {
            val url = "http://100.65.86.80/user/login"
            val requestBody = FormBody.Builder()
                .add("username",userText.text.toString())
                .add("password",passWardText.text.toString())
                .build()
            HttpPostRequest().okhttpPost(url, requestBody, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Looper.prepare()
                    Toast.makeText(this@MainActivity, "post请求失败", Toast.LENGTH_SHORT).show()
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
                            val user = gson.fromJson(json, User::class.java)
                            User_All.id = user.id
                            User_All.userName = user.userName
                            User_All.passWord = user.passWord
                            User_All.address = user.address
                            User_All.email = user.email
                            User_All.gender = user.gender
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                        }

                    }else{
                        /**
                         * 开启主线程进行动画渲染
                         */
                        runOnUiThread {
                            noticeTextView.visibility = View.VISIBLE
                            Handler().postDelayed({
                                val fadeOutAnimation = AlphaAnimation(1f, 0f)
                                fadeOutAnimation.duration = 3000 // 设置动画持续时间为1秒
                                fadeOutAnimation.fillAfter = true // 动画结束后保持状态
                                noticeTextView.startAnimation(fadeOutAnimation)
                                fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {
                                        // 动画开始时的操作
                                    }

                                    override fun onAnimationEnd(animation: Animation?) {
                                        // 动画结束时的操作
                                        noticeTextView.visibility = View.GONE
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {
                                        // 动画重复时的操作
                                    }
                                })
                            }, 1000) // 延迟5秒执行动画
                        }
                    }

                    Looper.loop()
                }
            })
        }
    }
}