package net.cyberplanete.meslieuxfavoris_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        binding?.fabAddFavoritePlace?.setOnClickListener {
            val intent = Intent(this,AddFavouritePlaces::class.java)
            startActivity(intent)
        }

    }
}