package com.example.tomagotchi

import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import java.util.logging.Handler
import android.widget.LinearLayout


data class FoodChallenge(val hint: String, val options: List<String>)
val foodChallenges = listOf(
    FoodChallenge("🍕 I want pizza!",      listOf("Domino's", "Pizza Hut", "Little Caesars", "Papa John's")),
    FoodChallenge("🍔 I want a burger!",   listOf("McDonald's", "Burger King", "Five Guys", "In-N-Out")),
    FoodChallenge("🌮 I want Mexican!",    listOf("Taco Bell", "Qdoba", "Chipotle", "Margarita Factory")),
    FoodChallenge("🥖 I want a sandwich!", listOf("Subway", "Firehouse Subs", "Jimmy John's", "Jersey Mike's")),
)
class MainActivity : AppCompatActivity() {

    private var wrongAttempts = 0
    private var currentChallenge: FoodChallenge? = null
    private var isAlive = true
    private var isReviveChance = false
    private var currentCorrectAnswer = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val confirmNameButton = findViewById<Button>(R.id.confirmNameButton)
        val peachboy = findViewById<ImageView>(R.id.peachboyadult)
        val editName = findViewById<EditText>(R.id.editName)
        val requestNameTitle = findViewById<TextView>(R.id.requestTomaName)
        var tomagotchiName = ""

        val feedButton = findViewById<Button>(R.id.feedButton)
        val waterButton = findViewById<Button>(R.id.waterButton)
        val sleepButton = findViewById<Button>(R.id.sleepButton)
        val nameText = findViewById<TextView>(R.id.name)

        val feedingOverlay = findViewById<LinearLayout>(R.id.feedingOverlay)
        val hintText       = findViewById<TextView>(R.id.hintText)
        val strikesText    = findViewById<TextView>(R.id.strikesText)
        val feedResultText = findViewById<TextView>(R.id.feedResultText)
        val optionButtons  = listOf(
            findViewById<Button>(R.id.option1Button),
            findViewById<Button>(R.id.option2Button),
            findViewById<Button>(R.id.option3Button),
            findViewById<Button>(R.id.option4Button)

        )

        val peachAdultFrames = listOf(
            R.drawable.peachboy,
            R.drawable.peachboyup
        )

        val peachBabyFrames = listOf(
            R.drawable.peachboybaby,
            R.drawable.peachboybabyup
        )

        //Different vector images of peach
        val versionsOfPeach = listOf(
            R.drawable.peachboy,
            R.drawable.peachboyup,
            R.drawable.egg,
            R.drawable.brokenegg,
            R.drawable.peachboybaby,
            R.drawable.peachboybabyup
        )

        var currentFrame = 0
        val handler = android.os.Handler(Looper.getMainLooper())

        fun showMainButtons() {
            feedButton.visibility  = View.VISIBLE
            waterButton.visibility = View.VISIBLE
            sleepButton.visibility = View.VISIBLE
            nameText.visibility    = View.VISIBLE
        }

        fun hideMainButtons() {
            feedButton.visibility  = View.GONE
            waterButton.visibility = View.GONE
            sleepButton.visibility = View.GONE
            nameText.visibility    = View.GONE
        }

        fun updateStrikes() {
            strikesText.text = "❤️".repeat(3 - wrongAttempts) + "🖤".repeat(wrongAttempts)
        }

        fun killTomagotchi() {
            isReviveChance = true
            hintText.text = "You really gonna kill me..."
            feedResultText.text = ""
            strikesText.text = "🖤🖤🖤"
            optionButtons.forEach { it.isEnabled = true }

        }

        fun permanentDeath() {
            isAlive = false
            isReviveChance = false
            peachboy.setImageResource(R.drawable.peachboydead)
            feedingOverlay.visibility = View.GONE
            nameText.text = "💀 ${tomagotchiName} is gone..."
            nameText.visibility = View.VISIBLE
            handler.removeCallbacksAndMessages(null)
            startGameButton.text = "Start Over"
            startGameButton.visibility = View.VISIBLE

        }

        fun handleOptionClick(chosen: String) {
            optionButtons.forEach { it.isEnabled = false }

            if (isReviveChance) {
                if (chosen == currentCorrectAnswer) {
                    isReviveChance = false
                    wrongAttempts  = 0
                    isAlive        = true
                    feedResultText.text = "Finally some food!!"
                    handler.postDelayed({
                        feedingOverlay.visibility = View.GONE
                        showMainButtons()
                        optionButtons.forEach { it.isEnabled = true }
                    }, 1200)
                } else {
                    feedResultText.text = "💀 That's it... I'm literally dying our of hunger..."
                    handler.postDelayed({ permanentDeath() }, 1500)
                }
                return
            }

            if (chosen == currentCorrectAnswer) {
                feedResultText.text = "Nice"
                handler.postDelayed({
                    feedingOverlay.visibility = View.GONE
                    showMainButtons()
                    optionButtons.forEach { it.isEnabled = true }
                }, 1200)
            } else {
                wrongAttempts++
                updateStrikes()
                if (wrongAttempts >= 3) {
                    peachboy.setImageResource(R.drawable.peachboysad)
                    feedResultText.text = "Bruh... last chance!!"
                    handler.postDelayed({ killTomagotchi() }, 1500)
                } else {
                    feedResultText.text = "Naur, try again!"
                    handler.postDelayed({ optionButtons.forEach { it.isEnabled = true } }, 300)
                }
            }
        }

        fun startFeedingGame() {
            if (!isAlive) return
            wrongAttempts    = 0
            currentChallenge = foodChallenges.random()
            val challenge    = currentChallenge!!

            // Random correct answer from the 4 options
            val correct  = challenge.options.random()
            val shuffled = challenge.options.shuffled()

            hintText.text       = challenge.hint
            feedResultText.text = ""
            updateStrikes()
            optionButtons.forEachIndexed { i, btn ->
                btn.text      = shuffled[i]
                btn.isEnabled = true
            }

            // Store correct answer so handleOptionClick can check it
            currentCorrectAnswer = correct
            feedingOverlay.visibility = View.VISIBLE
            hideMainButtons()
        }

        optionButtons.forEach { btn ->
            btn.setOnClickListener { handleOptionClick(btn.text.toString()) }
        }

        feedButton.setOnClickListener { startFeedingGame() }

        //Start the game, reveal hidden buttons that allow
        //the user to make a name for their tomagotchi
        //Once the name is chosen it cannot be edited
        startGameButton.setOnClickListener {
            // Reset all state
            isAlive = true
            isReviveChance = false
            wrongAttempts = 0
            currentFrame = 0
            startGameButton.text = "Start Game"

            // Hide everything that might be lingering
            nameText.visibility    = View.GONE
            feedButton.visibility  = View.GONE
            waterButton.visibility = View.GONE
            sleepButton.visibility = View.GONE
            feedingOverlay.visibility = View.GONE

            peachboy.setImageResource(versionsOfPeach[2])
            peachboy.translationY = 60f
            peachboy.visibility = View.VISIBLE
            startGameButton.visibility = View.GONE
            editName.visibility = View.VISIBLE
            requestNameTitle.visibility = View.VISIBLE
            confirmNameButton.visibility = View.VISIBLE
        }

        //Baby tomagotchi animation that runs back and forth
        //Hand waving
        val babyRunnable = object : Runnable {
            override fun run() {
                peachboy.setImageResource(peachBabyFrames[currentFrame])
                currentFrame = (currentFrame + 1) % peachBabyFrames.size
                handler.postDelayed(this, 500)
            }
        }

        //Adult tomagotchi animation that runs back and forth
        //Hand waving
        val adultRunnable = object : Runnable {
            override fun run() {
                peachboy.setImageResource(peachAdultFrames[currentFrame])
                currentFrame = (currentFrame + 1) % peachAdultFrames.size
                handler.postDelayed(this, 500)
            }
        }

        //Confirms the player's tomagotchi name
        //Hides the name grabbing buttons/textviews etc.
        //Makes the main game buttons appear: eat, feed. and sleep
        confirmNameButton.setOnClickListener {
            val nameInput = editName.text.toString().trim()
            tomagotchiName = nameInput

            editName.visibility = View.GONE
            requestNameTitle.visibility = View.GONE
            confirmNameButton.visibility = View.GONE

            feedButton.visibility = View.VISIBLE
            waterButton.visibility = View.VISIBLE
            sleepButton.visibility = View.VISIBLE

            nameText.text = tomagotchiName
            nameText.visibility = View.VISIBLE

            //Egg cracks
            handler.postDelayed({
                //Broken egg
                peachboy.setImageResource(versionsOfPeach[3])
                peachboy.translationY = 60f
                //Broken egg shows for a second then turns into baby!
                handler.postDelayed({
                    peachboy.translationY = 0f
                    handler.post(babyRunnable)

                    //After 1 minute, tomagotchi becomes an adult!
                    handler.postDelayed({
                        handler.removeCallbacks(babyRunnable)
                        handler.post(adultRunnable)
                    }, 60000)

                }, 800)
            }, 1000)
        }
    }
}