package com.example.mbkz

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat


class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_leaderboard)

        val reset = findViewById<Button>(R.id.button_reset)
        reset.setOnClickListener { resetLeaderBoard() }


        restrainScrollSpace()
        showLederBoard()
    }

    private fun restrainScrollSpace() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val height = displayMetrics.heightPixels

        val scrollContainer = findViewById<ScrollView>(R.id.scrollContainer)
        val layoutParams = scrollContainer.layoutParams
        layoutParams.height = (height * 0.60).toInt()
        scrollContainer.layoutParams = layoutParams
    }

    private fun resetLeaderBoard() {
        // Vytvoření dialogového okna s použitím vlastního layoutu
        val builder = AlertDialog.Builder(this)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.popup_layout, null)

        builder.setView(layout)
        builder.setCancelable(false)
        val dialog = builder.create()

        val yes = layout.findViewById<Button>(R.id.button_restart_yes)
        val no = layout.findViewById<Button>(R.id.button_restart_no)

        yes.setOnClickListener {
            val sharedPreferences = this.getSharedPreferences("leaderboard", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            dialog.dismiss()
            recreate()
        }

        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showLederBoard() {
        // Získání odkazu na SharedPreferences
        val sharedPreferences = getSharedPreferences("leaderboard", MODE_PRIVATE)

        val allEntries = sharedPreferences.all.entries.sortedByDescending { it.key.toInt() }

        if (allEntries.isEmpty()) {
            emptyPrint()
            return
        }

        for ((category, categoryScoresSet) in allEntries) {
            val categoryScores = categoryScoresSet as Set<String>

            val scoreList: MutableList<Int> = categoryScores.map { it.toInt() }.toMutableList()

            scoreList.sort()

            printCategory(category, scoreList)
        }


    }
    @SuppressLint("SetTextI18n")
    fun printCategory(category:String, scoreList: MutableList<Int>) {
        val board = findViewById<LinearLayout>(R.id.leaderboard)
        var place:Int = 1

        val header = createView(30)
        header.text = "Párů: $category"
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)

        board.addView(header)

        val placeColors = arrayListOf(R.color.gold,R.color.silver,R.color.bronze,R.color.white)

        for (moves in scoreList) {
            val colorIndex = minOf(placeColors.size - 1, place - 1)
            val view = createView(color = placeColors[colorIndex])
            view.text = "${place}. Místo:   $moves Tahů"

            board.addView(view)
            place++
        }

    }

    @SuppressLint("SetTextI18n")
    private fun emptyPrint() {
        val board = findViewById<LinearLayout>(R.id.leaderboard)

        val header = createView(30)
        header.text = "Zatím není stanovený žádný rekord"
        header.textAlignment = View.TEXT_ALIGNMENT_CENTER
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)

        board.addView(header)
    }

    private fun createView(margin: Int = 0, color: Int = R.color.white): TextView {
        val textView = TextView(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, margin, 0, margin)
        textView.layoutParams = params

        textView.setTextColor(ContextCompat.getColor(this, color))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
        textView.setTypeface(ResourcesCompat.getFont(this, R.font.dwpica_regular))
        textView.setTypeface(null, Typeface.BOLD)

        textView.id = View.generateViewId()

        return textView
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