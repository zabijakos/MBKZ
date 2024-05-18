package com.example.mbkz
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ToggleButton

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val musicButton = findViewById<ToggleButton>(R.id.button_music)
        val soundButton = findViewById<ToggleButton>(R.id.button_sound)

        setState(musicButton, "music")
        setState(soundButton, "sound")



        soundButton.setOnClickListener {setValue(it as ToggleButton, "sound")}
        musicButton.setOnClickListener {
            setValue(it as ToggleButton, "music")

            if(!it.isChecked)
                (application as MyApp).stopMusic()
            else
                (application as MyApp).startMusic()
        }

    }

    private fun setState(button: ToggleButton, key: String) {
        val sharedPreferences = this.getSharedPreferences("setting", MODE_PRIVATE)

        button.isChecked = sharedPreferences.getBoolean(key, true)
    }


    private fun setValue(button: ToggleButton, key: String) {
        val sharedPreferences = this.getSharedPreferences("setting", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean(key, button.isChecked)

        editor.apply()
    }

    override fun onResume() {
        super.onResume()

        (application as MyApp).resumeMusic()
    }
}