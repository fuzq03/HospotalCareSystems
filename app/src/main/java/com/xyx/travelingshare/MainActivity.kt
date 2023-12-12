package com.xyx.travelingshare

import android.content.Intent
import android.media.audiofx.Visualizer
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ActionProvider.VisibilityListener
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.xyx.travelingshare.utils.HttpPostRequest
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
        signUpButton.setOnClickListener{
            val intent = Intent(applicationContext,SignUpActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val url = "http://192.168.137.1:8080/user/login"
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
                    Looper.prepare()
                    /**
                     * 接受服务器发回来的消息，并进行判断
                     */
                    if (response.body()?.string() != "0"){
                        val intent = Intent(applicationContext,HomeActivity::class.java)
                        startActivity(intent)
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