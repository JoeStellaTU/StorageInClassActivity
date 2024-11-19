package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        // Load previously saved comic if it exists
        loadSavedComic()

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url, { showComic(it) }, { /* Handle error */ })
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)

        // Save the comic info after displaying it
        saveComic(comicObject)
    }

    // Save comic info to SharedPreferences
    private fun saveComic(comicObject: JSONObject) {
        val sharedPref = getSharedPreferences("comic_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("title", comicObject.getString("title"))
            putString("description", comicObject.getString("alt"))
            putString("image_url", comicObject.getString("img"))
            apply() // Apply changes
        }
    }

    // Load saved comic from SharedPreferences
    private fun loadSavedComic() {
        val sharedPref = getSharedPreferences("comic_prefs", MODE_PRIVATE)
        val title = sharedPref.getString("title", null)
        val description = sharedPref.getString("description", null)
        val imageUrl = sharedPref.getString("image_url", null)

        // If comic data exists, load it
        if (title != null && description != null && imageUrl != null) {
            titleTextView.text = title
            descriptionTextView.text = description
            Picasso.get().load(imageUrl).into(comicImageView)
        }
    }
}
