package net.cyberplanete.meslieuxfavoris_kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityMainBinding




class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        binding?.fabAddFavoritePlace?.setOnClickListener {
            val intent = Intent(this, MesLieuxFavorisAjoutActivity::class.java)
            startActivity(intent)
        }

    }
}