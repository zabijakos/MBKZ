package com.example.mbkz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.button_start)
        val lbButton = findViewById<Button>(R.id.button_leaderboard)
        val settingButton = findViewById<Button>(R.id.button_setting)

        startButton.setOnClickListener {
            val intent = Intent(this, ModeActivity::class.java)
            startActivity(intent)
        }

        lbButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        settingButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        (application as MyApp).resumeMusic()
    }

    override fun onPause() {
        super.onPause()

//        (application as MyApp).stopMusic()
    }
}