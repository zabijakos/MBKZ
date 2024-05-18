package com.example.mbkz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*


class GameActivity : AppCompatActivity() {

    private var previousCard: ImageButton? = null
    private var actualCard: ImageButton? = null
    private var movesCount: Int = 0
    private var cardsFound: Int = 0
    private var hintsLeft: Int = 0
    private var soundAllowed = false

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var backPressCallback: OnBackPressedCallback
    private val timerDuration: Long = 1000

    private lateinit var images: List<Int>
    private var cards = ArrayList<ImageButton>()
    private lateinit var movesCounter: TextView




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // vypnuti statusbaru
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                  WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_game)

        // osetreni navratu
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnConfirmation()
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressCallback)


        // nastaveni karet
        val pairs = intent.getIntExtra("pairs", 0)
        prepareLayout(pairs * 2)


        // nastaveni pouzivaných promennych
        movesCounter = findViewById(R.id.moves_counter)
        movesCounter.text = getString(R.string.moves) + movesCount.toString()
        setupTimer()
        setupHint(pairs)
        setUpSound()

    }

    @SuppressLint("SetTextI18n")
    private fun giveHint(button: Button) {
        if(hintsLeft < 1)
            return

        hintsLeft--
        button.text = "${getString(R.string.hint)}: $hintsLeft"

        var randomCard = cards.random()
        while (randomCard.tag == "done" || randomCard == previousCard)
            randomCard = cards.random()

        hintAnimate(randomCard, images[cards.indexOf(randomCard)], inTime = 40, outTime = 70) {
            hintAnimate(randomCard, R.drawable.card_back, inTime = 70, outTime = 40)
        }


    }

    @SuppressLint("SetTextI18n")
    private fun setupHint(pairs: Int) {
        val buttonHint = findViewById<Button>(R.id.button_hint)

        if (pairs < 4) {
            buttonHint.visibility = View.GONE
            return
        }

        hintsLeft = pairs / 3

        buttonHint.text = "${getString(R.string.hint)}: $hintsLeft"
        buttonHint.setOnClickListener { giveHint(it as Button) }
    }

    private fun setUpSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.correct)
        val sharedPreferences = this.getSharedPreferences("setting", MODE_PRIVATE)

        soundAllowed = sharedPreferences.getBoolean("sound", true)
    }

    private fun setupTimer() {
        countDownTimer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                flipBack()
            }
        }
    }

    private fun startTimer() {
        countDownTimer.cancel()  // Zruší aktuální časovač
        setupTimer()  // Nastaví a spustí nový časovač
        countDownTimer.start()
    }

    private fun prepareLayout(cardsCount: Int) {
        val columns = ceil(min(sqrt(cardsCount.toDouble()), 6.0))
        val rows = ceil(cardsCount / columns)

        val board = findViewById<TableLayout>(R.id.board)

        val imgSize = calculateImageSize(columns)
        images = getRandomCards(cardsCount/2)


        for (i in 0 until rows.toInt()) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            for (j in 0 until columns.toInt()) {
                if(cards.count() == images.count())
                    break

                val button = createCard(imgSize)
                button.tag = images[cards.count()]

                tableRow.addView(button)
                cards.add(button)
            }
            board.addView(tableRow)
        }
    }

    private fun flipAnimate(card: ImageButton, image: Int,
                            inTime: Long = 100, outTime: Long = 100) {

        val animIn = ObjectAnimator.ofFloat(card,"scaleX", 1f, 0f)
        val animOut = ObjectAnimator.ofFloat(card,"scaleX", 0f, 1f)

        animIn.interpolator = AccelerateInterpolator()
        animOut.interpolator = AccelerateInterpolator()

        animIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                card.setImageResource(image)
                animOut.start()
            }
        })

        animIn.start()

        animIn.setDuration(inTime)
        animOut.setDuration(outTime)
    }

    private fun hintAnimate(card: ImageButton, image: Int, inTime: Long = 100, outTime: Long = 100, completion: (() -> Unit)? = null) {
        val animIn = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0f)
        val animOut = ObjectAnimator.ofFloat(card, "scaleX", 0f, 1f)

        animIn.interpolator = LinearInterpolator()
        animOut.interpolator = LinearInterpolator()

        animIn.duration = inTime
        animOut.duration = outTime

        animIn.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                card.setImageResource(image)
                animOut.start()
            }
        })

        animOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                Handler(Looper.getMainLooper()).postDelayed({
                    completion?.invoke()
                }, 200)
            }
        })

        animIn.start()
    }

    private fun flipBack() {
        if (previousCard != null) {
            if (previousCard!!.tag == "done")
                flipAnimate(previousCard!!, android.R.color.transparent)
            else
                flipAnimate(previousCard!!, R.drawable.card_back, outTime = 130)

            previousCard = null
        }

        if (actualCard != null) {
            if(actualCard!!.tag == "done")
                flipAnimate(actualCard!!, android.R.color.transparent)
            else
                flipAnimate(actualCard!!, R.drawable.card_back, outTime = 110)
            actualCard = null
        }
    }

    private fun flipCard(card: ImageButton) {
        if(card.tag == "done")
            return

        if(previousCard != null && cards.indexOf(card) == cards.indexOf(previousCard))
            return

        if(actualCard == null) {
            if(previousCard == null)
                previousCard = card
            else
                evaluate(card)
        }
        else {
            countDownTimer.cancel()
            flipBack()
            previousCard = card
        }

        flipAnimate(card, images[cards.indexOf(card)], inTime = 110, outTime = 160)
    }

    @SuppressLint("SetTextI18n")
    private fun evaluate(card: ImageButton) {
        movesCount++

        movesCounter.text = getString(R.string.moves) + movesCount.toString()

        if (card.tag == previousCard!!.tag) {
            cardsFound += 2

            card.tag = "done"
            previousCard!!.tag = "done"

            card.isEnabled = false
            previousCard!!.isEnabled = false

            playSound(R.raw.correct)
        }

        actualCard = card
        startTimer()

        if(cardsFound == cards.size) {
            card.setImageResource(images[cards.indexOf(card)])
            gameOver()
        }

    }

    fun playSound(soundResourceId: Int) {
        if(!soundAllowed)
            return

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()

        val assetFileDescriptor = this.resources.openRawResourceFd(soundResourceId) ?: return
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        assetFileDescriptor.close()

        mediaPlayer.prepare()
        mediaPlayer.start()
    }

//    private fun playFindSound() {
//        if(!soundAllowed)
//            return
//
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.stop()
//            mediaPlayer.prepareAsync()
//            mediaPlayer.setOnPreparedListener {
//                mediaPlayer.seekTo(0)
//                mediaPlayer.start()
//            }
//        }
//        else {
//            mediaPlayer.start()
//        }
//    }

    private fun gameOver() {
        if(updateLeaderboard(this))
            playSound(R.raw.best)
        else
            playSound(R.raw.win)
        showFinish()
    }


    private fun showFinish() {
        val builder = AlertDialog.Builder(this)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.win_layout, null)

        builder.setView(layout)

        val restart = layout.findViewById<Button>(R.id.button_restart)
        val menu = layout.findViewById<Button>(R.id.button_menu)

        restart.setOnClickListener {
            cardsFound = 0
            movesCount = 0
            cards.clear()
            val board: TableLayout = findViewById(R.id.board)
            board.removeAllViews()
            previousCard = null
            actualCard = null
            recreate()
        }

        menu.setOnClickListener {
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun updateLeaderboard(context: Context): Boolean {
        // Získání odkazu na SharedPreferences
        val sharedPreferences = context.getSharedPreferences("leaderboard", MODE_PRIVATE)

        // Získání seznamu výsledků pro danou kategorii
        val categoryScores = sharedPreferences.getStringSet((cards.size / 2).toString(), HashSet())

        val scoreList: MutableList<Int> = categoryScores!!.map { it.toInt() }.toMutableList()

        // Přidání nového skóre do seznamu
        scoreList.add(movesCount)

        scoreList.sort()

        if (scoreList.size > 5) {
            scoreList.removeAt(5)
        }

        // Uložení upraveného seznamu zpět do SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putStringSet((cards.size/ 2).toString(), scoreList.map { it.toString() }.toHashSet())
        editor.apply()

        return scoreList[0] == movesCount
    }

    private fun calculateImageSize(columns: Double): Int {
        val displayMetrics = DisplayMetrics()

        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        return (width / columns).toInt()
    }

    private fun createCard(size: Int): ImageButton {
        val imageButton = ImageButton(this)
        imageButton.id = View.generateViewId()
        imageButton.layoutParams = TableRow.LayoutParams(size, size)
        imageButton.scaleType = ImageView.ScaleType.FIT_CENTER
        imageButton.setBackgroundResource(android.R.color.transparent)
        imageButton.setImageResource(R.drawable.card_back)
        imageButton.setOnClickListener { flipCard(it as ImageButton) }

        return imageButton
    }

   private fun getRandomCards(pairs: Int): List<Int> {
        val fields = R.drawable::class.java.fields
        val drawableIds = mutableListOf<Int>()

        for (field in fields) {
            try {
                drawableIds.add(field.getInt(null))
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        return drawableIds.filter { resources.getResourceEntryName(it).endsWith("_game") }
                          .shuffled()
                          .take(pairs)
                          .flatMap { listOf(it, it) }.shuffled()
    }


     fun returnConfirmation() {

        val builder = AlertDialog.Builder(this)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.popup_layout, null)

        builder.setView(layout)
         builder.setCancelable(false)
         val dialog = builder.create()

        val yes = layout.findViewById<Button>(R.id.button_restart_yes)
        val no = layout.findViewById<Button>(R.id.button_restart_no)
        val textV = layout.findViewById<TextView>(R.id.resetView)

        textV.text = getString(R.string.leaveQ)

        yes.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        no.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        mediaPlayer.release()
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