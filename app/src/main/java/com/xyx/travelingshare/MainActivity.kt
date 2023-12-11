package com.xyx.travelingshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var userText:TextView
    private lateinit var passWardText:TextView
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFace()
    }
    private fun initFace(){
        userText = findViewById(R.id.uerName)
        passWardText = findViewById(R.id.passWord)
        loginButton = findViewById(R.id.login)
        signUpButton = findViewById(R.id.signUp)
        signUpButton.setOnClickListener{
            val intent = Intent(applicationContext,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}