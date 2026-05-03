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

class MainActivity : AppCompatActivity() {
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

        val frames = listOf(
            R.drawable.peachboy,
            R.drawable.peachboyup
        )

        var currentFrame = 0
        val handler = android.os.Handler(Looper.getMainLooper())

        //Start the game, reveal hidden buttons that allow
        //the user to make a name for their tomagotchi
        //Once the name is chosen it cannot be edited
        startGameButton.setOnClickListener {
            peachboy.visibility = View.VISIBLE
            startGameButton.visibility = View.GONE
            editName.visibility = View.VISIBLE
            requestNameTitle.visibility = View.VISIBLE
            confirmNameButton.visibility = View.VISIBLE
        }



        val animationRunnable = object : Runnable {
            override fun run() {
                peachboy.setImageResource(frames[currentFrame])
                currentFrame = (currentFrame + 1) % frames.size
                handler.postDelayed(this, 500)
            }
        }

        handler.post(animationRunnable)

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

            //Set the tomagotchi's name :)
            nameText.text = tomagotchiName
            nameText.visibility = View.VISIBLE
        }
    }
}