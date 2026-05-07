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
import android.content.Context


data class FoodChallenge(val hint: String, val options: List<String>)
val foodChallenges = listOf(
    FoodChallenge("🍕 I want pizza!",      listOf("Domino's", "Pizza Hut", "Little Caesars", "Papa John's")),
    FoodChallenge("🍔 I want a burger!",   listOf("McDonald's", "Burger King", "Five Guys", "In-N-Out")),
    FoodChallenge("🌮 I want Mexican Food!",    listOf("Taco Bell", "Qdoba", "Chipotle", "Margarita Factory")),
    FoodChallenge("🥖 I want a sandwich!", listOf("Subway", "Firehouse Subs", "Jimmy John's", "Jersey Mike's")),
    FoodChallenge("🍗 I want fried chicken!", listOf("Chick-fil-A", "Popeyes", "KFC", "Raising Cane's")),
    FoodChallenge("☕ I want coffee!",        listOf("Starbucks", "Dunkin'", "Dutch Bros", "Peet's Coffee")),
    FoodChallenge("🍦 I want ice cream!",     listOf("Dairy Queen", "Baskin-Robbins", "Cold Stone", "Sonics")),
    FoodChallenge("🥗 I want a salad!",       listOf("Laughing Planet", "Panera Bread", "Freshii", "Just Salad")),

)
class MainActivity : AppCompatActivity() {

    private var wrongAttempts = 0
    private var currentChallenge: FoodChallenge? = null
    private var isAlive = true
    private var isReviveChance = false
    private var currentCorrectAnswer = ""
    private var daysAlive            = 0
    private var isSleeping = false


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
        val sleepEmojiText = findViewById<TextView>(R.id.sleepEmojiText)
        val wakeUpButton = findViewById<Button>(R.id.wakeUpButton)
        val nameText = findViewById<TextView>(R.id.name)
        val daysText          = findViewById<TextView>(R.id.daysText)
        val leaderboardButton = findViewById<Button>(R.id.leaderboardButton)
        val rpsResultText = findViewById<TextView>(R.id.rpsResultText)
        val playButton    = findViewById<Button>(R.id.playButton)


        val feedingOverlay = findViewById<LinearLayout>(R.id.feedingOverlay)
        val hintText       = findViewById<TextView>(R.id.hintText)
        val strikesText    = findViewById<TextView>(R.id.strikesText)
        val feedResultText = findViewById<TextView>(R.id.feedResultText)

        val rpsOverlay        = findViewById<LinearLayout>(R.id.rpsOverlay)
        val rpsPromptText     = findViewById<TextView>(R.id.rpsPromptText)
        val rockButton        = findViewById<Button>(R.id.rockButton)
        val paperButton       = findViewById<Button>(R.id.paperButton)
        val scissorsButton    = findViewById<Button>(R.id.scissorsButton)

        val needsText = findViewById<TextView>(R.id.needsText)
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
            daysText.visibility    = View.VISIBLE        // ← ADD
            leaderboardButton.visibility = View.VISIBLE
        }

        fun hideMainButtons() {
            feedButton.visibility  = View.GONE
            waterButton.visibility = View.GONE
            sleepButton.visibility = View.GONE
            nameText.visibility    = View.GONE
            playButton.visibility  = View.GONE
        }

        fun updateStrikes() {
            strikesText.text = "❤️".repeat(3 - wrongAttempts) + "🖤".repeat(wrongAttempts)
        }

        fun showNeedMessage(msg: String) {
            needsText.text = "💬 $msg"
            needsText.visibility = View.VISIBLE
            handler.postDelayed({ needsText.visibility = View.GONE }, 8000)
        }

        fun clearNeeds() {
            needsText.visibility = View.GONE
        }

        fun saveScore(days: Int) {
            val prefs = getSharedPreferences("tomagotchi", Context.MODE_PRIVATE)
            val scores = (1..5)
                .map { prefs.getInt("score_$it", -1) }
                .filter { it >= 0 }
                .toMutableList()
            scores.add(days)
            scores.sortDescending()
            val editor = prefs.edit()
            scores.take(5).forEachIndexed { i, score -> editor.putInt("score_${i + 1}", score) }
            editor.apply()
        }

        fun getScores(): List<Int> {
            val prefs = getSharedPreferences("tomagotchi", Context.MODE_PRIVATE)
            return (1..5).map { prefs.getInt("score_$it", -1) }.filter { it >= 0 }
        }

        fun showLeaderboard() {
            val scores = getScores()
            if (scores.isEmpty()) {
                showNeedMessage("No scores yet!")
                return
            }
            val medals = listOf("🥇", "🥈", "🥉", "4️⃣", "5️⃣")
            val text = scores.mapIndexed { i, days ->
                "${medals[i]} $days days"
            }.joinToString("\n")
            needsText.text = "🏆 Best Scores:\n$text"
            needsText.visibility = View.VISIBLE
            handler.postDelayed({ needsText.visibility = View.GONE }, 6000)
        }

        leaderboardButton.setOnClickListener { showLeaderboard() }

        // 1 day = 2 minutes (120,000 ms) for testing
        val dayRunnable = object : Runnable {
            override fun run() {
                if (!isAlive) return
                daysAlive++
                daysText.text = "📅 Day $daysAlive"

                // Unlock evolved form at day 3 (6 min)
                if (daysAlive == 3) {
                    showNeedMessage("✨ ${tomagotchiName} is evolving!!")
                    handler.postDelayed({
                        // swap for your secret evolved drawable here
                        peachboy.setImageResource(R.drawable.peachboysad)
                    }, 2000)
                }

                handler.postDelayed(this, 120_000)
            }
        }

        // rock paper scissors

        fun startRPS() {
            if (!isAlive) return
            rpsPromptText.text = "😤 ${tomagotchiName} wants to play rock paper scissors!"
            rpsResultText.text = ""
            listOf(rockButton, paperButton, scissorsButton).forEach { it.isEnabled = true }
            rpsOverlay.visibility = View.VISIBLE
            hideMainButtons()
            needsText.visibility = View.GONE
        }

        fun handleRPS(playerChoice: String) {
            listOf(rockButton, paperButton, scissorsButton).forEach { it.isEnabled = false }
            val choices = listOf("🪨 Rock", "📄 Paper", "✂️ Scissors")
            val boopChoice = choices.random()

            val result = when {
                playerChoice == boopChoice -> "😐 Tie! ${tomagotchiName} is unimpressed"
                (playerChoice == "🪨 Rock"     && boopChoice == "✂️ Scissors") ||
                        (playerChoice == "📄 Paper"    && boopChoice == "🪨 Rock")     ||
                        (playerChoice == "✂️ Scissors" && boopChoice == "📄 Paper")
                    -> "😭 You won! ${tomagotchiName} is devastated"
                else -> "😈 ${tomagotchiName} picked $boopChoice and WON! HA!"
            }

            rpsResultText.text = "${tomagotchiName} picked $boopChoice\n$result"

            handler.postDelayed({
                rpsOverlay.visibility = View.GONE
                showMainButtons()
            }, 2500)
        }

        rockButton.setOnClickListener     { handleRPS("🪨 Rock") }
        paperButton.setOnClickListener    { handleRPS("📄 Paper") }
        scissorsButton.setOnClickListener { handleRPS("✂️ Scissors") }
        playButton.setOnClickListener     { startRPS() }

        // bored messages + RPS trigger

        val boredMessages = listOf(
            "bored ngl",
            "hello?? anyone there",
            "....",
            "staring at the void rn",
            "someone entertain me",
            "do something im literally bored",
        )

        val boredRunnable = object : Runnable {
            override fun run() {
                if (isAlive && !isSleeping &&
                    rpsOverlay.visibility == View.GONE &&
                    feedingOverlay.visibility == View.GONE) {
                    val name = tomagotchiName.ifBlank { "Boop" }
                    if ((0..1).random() == 0) {
                        showNeedMessage("💬 $name wants to play!")
                        handler.postDelayed({ startRPS() }, 2000)
                    } else {
                        showNeedMessage("💬 $name: ${boredMessages.random()}")
                    }
                }
                handler.postDelayed(this, (10_000..20_000).random().toLong())
            }
        }

        val needsRunnable = object : Runnable {
            override fun run() {
                val name = tomagotchiName.ifBlank { "Your buddy" }
                val messages = listOf(
                    "$name needs to eat!",
                    "$name is starving!!",
                    "$name needs to drink something!",
                    "$name is thirsty!!",
                    "$name is sleepy...",
                    "$name needs a nap!!"
                )
                showNeedMessage(messages.random())
                handler.postDelayed(this, (20000..45000).random().toLong())
            }
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

        feedButton.setOnClickListener  { clearNeeds(); startFeedingGame() }

        waterButton.setOnClickListener { clearNeeds(); showNeedMessage("${tomagotchiName} got some water! 💧") }


        sleepButton.setOnClickListener {
            isSleeping = true
            peachboy.visibility = View.INVISIBLE
            sleepEmojiText.visibility = View.VISIBLE
            wakeUpButton.visibility = View.VISIBLE
            hideMainButtons()
            showNeedMessage("💤 ${tomagotchiName} is sleeping... shhh")
        }

        wakeUpButton.setOnClickListener {
            clearNeeds()
            isSleeping = false
            peachboy.visibility = View.VISIBLE
            sleepEmojiText.visibility = View.GONE
            wakeUpButton.visibility = View.GONE
            showMainButtons()
            showNeedMessage("💬 ${tomagotchiName} is awake... do NOT talk to me yet")
        }

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
            sleepEmojiText.visibility = View.GONE
            wakeUpButton.visibility = View.GONE

            handler.removeCallbacksAndMessages(null)  // stops ticker on reset
            clearNeeds()

            // Hide everything that might be lingering
            nameText.visibility    = View.GONE
            feedButton.visibility  = View.GONE
            waterButton.visibility = View.GONE
            sleepButton.visibility = View.GONE
            feedingOverlay.visibility = View.GONE
            playButton.visibility = View.GONE
            daysText.visibility = View.GONE
            leaderboardButton.visibility = View.GONE

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
            playButton.visibility        = View.VISIBLE
            daysText.visibility          = View.VISIBLE
            leaderboardButton.visibility = View.VISIBLE

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

            handler.postDelayed(needsRunnable, (20000..45000).random().toLong())
            handler.postDelayed(boredRunnable, 10_000)   // ← add this
            handler.postDelayed(dayRunnable, 120_000)    // ← add this
        }
    }
}