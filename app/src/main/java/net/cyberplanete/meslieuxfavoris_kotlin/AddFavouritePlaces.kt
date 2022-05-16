package net.cyberplanete.meslieuxfavoris_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding


class AddFavouritePlaces : AppCompatActivity() {private var binding: ActivityAddFavouritePlacesBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFavouritePlacesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
    }
}