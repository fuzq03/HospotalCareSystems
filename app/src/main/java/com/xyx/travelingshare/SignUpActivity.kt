package com.xyx.travelingshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.RadioButton
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
            val url = "http://192.168.8.26:8080/user/save"
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
                    val json = response.body()?.string()
                    Looper.prepare()
                    /**
                     * 接受服务器发回来的消息，并进行判断
                     */
                    if (json != "0"){
                        Toast.makeText(this@SignUpActivity, "注册成功,用户名为：" + userNameTextView.text.toString(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@SignUpActivity, "注册失败" , Toast.LENGTH_SHORT).show()
                    }

                    Looper.loop()
                }
            })
        }
    }
}