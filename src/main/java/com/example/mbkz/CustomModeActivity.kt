package com.example.mbkz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ImageButton
import kotlin.math.max
import kotlin.math.min

class CustomModeActivity : AppCompatActivity() {

    var used = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_mode)

        val buttonUp = findViewById<ImageButton>(R.id.button_up)
        val buttonDown = findViewById<ImageButton>(R.id.button_down)
        val buttonConfirm = findViewById<ImageButton>(R.id.button_confirm)

        val pairNumber =  findViewById<EditText>(R.id.editTextNumber)
        pairNumber.text = Editable.Factory.getInstance().newEditable("4")

        buttonUp.setOnClickListener { changeValue(1, pairNumber) }
        buttonDown.setOnClickListener { changeValue(-1, pairNumber) }
        buttonConfirm.setOnClickListener { startGame(pairNumber) }
    }

    private fun startGame(pairNumber: EditText) {
        var value: Int? = pairNumber.text.toString().toIntOrNull()

        if (value != null) {
            value = max(1, min(value, 18))

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("pairs", value)
            startActivity(intent)
        }
    }


    private fun changeValue(change: Int, target: EditText) {
        var value: Int? = target.text.toString().toIntOrNull()

        if (value != null) {
            value += change
            target.setText(max(1, min(value, 18)).toString())
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