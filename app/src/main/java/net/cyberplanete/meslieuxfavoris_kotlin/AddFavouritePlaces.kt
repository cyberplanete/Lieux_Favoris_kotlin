package net.cyberplanete.meslieuxfavoris_kotlin

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding
import java.text.SimpleDateFormat
import java.util.*

class AddFavouritePlaces : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_READ_WRITE_EXTERNAL_REQUEST_CODE = 1
        const val PERMISSION_CAMERA_REQUEST_CODE = 2
    }


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
        /* DatePicker - END */

        /* Add Image */
        binding?.tvAddPhoto?.setOnClickListener(this)

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
            R.id.tv_add_photo -> {
                val imageDialog = AlertDialog.Builder(this)
                imageDialog.setTitle("Selectionner une action")
                val imageDialogItems =
                    arrayOf("Selectionner une photo depuis la gallerie", "Prendre une photo")
                imageDialog.setItems(imageDialogItems) { dialog, whichOne ->
                    when (whichOne) {
                        0 -> requestReadWriteExternalStoragePermission()
                        1 -> requestCameraPermission()
                    }


                }.show()
            }
        }
    }

    fun choisirUnePhotoDepuisGallerie() {
        TODO("Not yet implemented")
    }

    /* Methode pour afficher la date dans l'inputtext après l'avoir séléctionner dans le datePicker */
    private fun updateDateInView() {
        val dateFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding?.etDate?.setText(simpleDateFormat.format(calendar.time).toString())

    }

    private fun hasReadWriteStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun requestReadWriteExternalStoragePermission() {
        EasyPermissions.requestPermissions(
            this,
            "Cette application à besoin d'accéder à vos fichiers et dossiers pour permettre la sélection d'une image ",
            PERMISSION_READ_WRITE_EXTERNAL_REQUEST_CODE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

            )
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            this,
            "Cette application à besoin d'accéder à votre camera pour prendre des photos  ",
            PERMISSION_CAMERA_REQUEST_CODE,
            Manifest.permission.CAMERA,

            )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
          ///  SettingsDialog.Builder(this).build().show()
            AlertDialog.Builder(this).setMessage("Permission refusée......").setPositiveButton("GO TO SETTINGS")
            { _,_ ->
                try {
                    // Renvoyer l'utilisateur vers les parametres de l'application pour permettre à l'utilisateur d'appliquer les droits manuellements
                    // override fun onRequestPermissionsResult which Handle the result of a permission request géré dans les if statements
                    val intent  =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uriPackageName = Uri.fromParts("package",packageName,null)
                    intent.data = uriPackageName
                    startActivity(intent)
                }catch (e:Resources.NotFoundException) {e.printStackTrace()}

            }.setNegativeButton("Annuler") {dialog,which ->
              dialog.dismiss()
            }.show()
        }else
        {
            if (requestCode == PERMISSION_READ_WRITE_EXTERNAL_REQUEST_CODE)
            {
                requestReadWriteExternalStoragePermission()
            }
            else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE)
            {
                requestCameraPermission()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this,"Permissions autorisées",Toast.LENGTH_LONG).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)//Handle the result of a permission request
    }
}