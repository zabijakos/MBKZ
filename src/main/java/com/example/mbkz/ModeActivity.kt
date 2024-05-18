package com.example.mbkz

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class ModeActivity : AppCompatActivity() {

    var used = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_mode)

        val easyButton = findViewById<Button>(R.id.button_easy)
        val mediumButton = findViewById<Button>(R.id.button_medium)
        val hardButton = findViewById<Button>(R.id.button_hard)
        val customButton = findViewById<Button>(R.id.button_custom)

        easyButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("pairs", 3)
            startActivity(intent)
        }

        mediumButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("pairs", 8)
            startActivity(intent)
        }

        hardButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("pairs", 10)
            startActivity(intent)
        }

        customButton.setOnClickListener {
            val intent = Intent(this, CustomModeActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        if(used)
            finish()

        used = true

        (application as MyApp).resumeMusic()

    }

    override fun onPause() {
        super.onPause()

//        (application as MyApp).stopMusic()
    }
}