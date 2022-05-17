package net.cyberplanete.meslieuxfavoris_kotlin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding
import java.text.SimpleDateFormat
import java.util.*


class AddFavouritePlaces : AppCompatActivity(), View.OnClickListener {
    /* DatePicker */
    private var calendar = Calendar.getInstance() // Instance de Calendar java
    private lateinit var dateListener: DatePickerDialog.OnDateSetListener // Pour l'UI


    private var binding: ActivityAddFavouritePlacesBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFavouritePlacesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        /* Pour la bar d'action */
        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Le titre est ajouté depuis androidManifest.xml
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
        /* END --- Pour la bar d'action */

        /* DatePicker */
        dateListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->

            calendar.set(
                Calendar.YEAR,
                year
            )
            calendar.set(
                Calendar.MONTH,
                month
            )
            calendar.set(
                Calendar.DAY_OF_MONTH,
                dayOfMonth
            )

            updateDateInView()
        }
        binding?.etDate?.setOnClickListener(this)


    }
/* Centralisation logique onClickListenner*/
    override fun onClick(elementOfView: View?) {
        when (elementOfView!!.id) //
        {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddFavouritePlaces,
                    dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun updateDateInView() {
        val dateFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding?.etDate?.setText(simpleDateFormat.format(calendar.time).toString())

    }
}