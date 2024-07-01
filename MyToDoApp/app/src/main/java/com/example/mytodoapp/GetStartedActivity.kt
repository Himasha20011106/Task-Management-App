package com.example.mytodoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class GetStartedActivity : AppCompatActivity() {
    lateinit var getStarted: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        getStarted = findViewById(R.id.getstartedButton)

        val additionalButton = findViewById<Button>(R.id.getstartedButton)
        additionalButton.setOnClickListener {
            val intent = Intent(this,MainActivity ::class.java)
            startActivity(intent)

            Toast.makeText(this, "Redirecting to main dashbaord!", Toast.LENGTH_SHORT).show()
        }
    }
}