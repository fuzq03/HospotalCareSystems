package com.xyx.travelingshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.xyx.travelingshare.utils.HttpPostRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Response
import java.io.IOException

class SignUpActivity : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var passWordTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var maleRadioButton: RadioButton
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initView()
    }
    private fun initView(){
        userNameTextView = findViewById(R.id.uerName)
        passWordTextView = findViewById(R.id.passWord)
        emailTextView = findViewById(R.id.email)
        addressTextView = findViewById(R.id.address)
        maleRadioButton = findViewById(R.id.radioButtonMale)
        femaleRadioButton = findViewById(R.id.radioButtonFemale)
        signUpButton = findViewById(R.id.signUp)
        signUpButton.setOnClickListener {
            val url = "http://192.168.137.1:8080/user/save"
            var gender="male"
            gender = if(maleRadioButton.isChecked){
                "male"
            }else{
                "female"
            }
            val requestBody = FormBody.Builder()
                .add("username",userNameTextView.text.toString())
                .add("password",passWordTextView.text.toString())
                .add("email",emailTextView.text.toString())
                .add("address",addressTextView.text.toString())
                .add("gender",gender)
                .build()
            HttpPostRequest().okhttpPost(url, requestBody, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Looper.prepare()
                    Toast.makeText(this@SignUpActivity, "post请求失败", Toast.LENGTH_SHORT).show()
                    Looper.loop()
                }

                override fun onResponse(call: Call, response: Response) {
                    Looper.prepare()
                    /**
                     * 接受服务器发回来的消息，并进行判断
                     */
                    if (response.body()?.string() != "0"){
                        Toast.makeText(this@SignUpActivity, "注册成功,用户名为：" + userNameTextView.text.toString(), Toast.LENGTH_SHORT).show()
                    }else{
                        /**
                         * 开启主线程进行动画渲染
                         */
                        Toast.makeText(this@SignUpActivity, "注册失败" , Toast.LENGTH_SHORT).show()
                    }

                    Looper.loop()
                }
            })
        }
    }
}