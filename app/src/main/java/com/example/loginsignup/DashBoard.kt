package com.example.loginsignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class DashBoard : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var  btnLogout : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        auth = FirebaseAuth.getInstance()
        btnLogout = findViewById(R.id.btnLogOut)

        btnLogout.setOnClickListener{
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}