package net.cyberplanete.meslieuxfavoris_kotlin

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.vmadalin.easypermissions.EasyPermissions
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddFavouritePlaces : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val GALLERY_REQUEST_CODE = 1
        const val CAMERA_REQUEST_CODE = 2
        //  const val GALLERY = 3
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
    /* Centralisation logique onClickListenner  - END */


    /* ---------------------- DatePicker ------------------------*/
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

    /* Methode pour afficher la date dans l'inputtext après l'avoir séléctionner dans le datePicker */
    private fun updateDateInView() {
        val dateFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding?.etDate?.setText(simpleDateFormat.format(calendar.time).toString())

    }
    /* ---------------------- DatePicker END ------------------------*/


    /* - Request read and write storage permissions -
   Check permissions pour l'accès aux dossiers et fichiers
       si nok , alors demande des droits d'accès
       sinon ouverture du gestionnaire de fichiers pour selectionner une image */

    private fun requestReadWriteExternalStoragePermission() {

        if (hasReadWriteStoragePermission()) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResultGallery.launch(galleryIntent)

        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cette application à besoin d'accéder à vos fichiers et dossiers pour permettre la sélection d'une image ",
                GALLERY_REQUEST_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,

                )

        }

    }

    private fun hasReadWriteStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /* ActivityResult  for   private fun requestReadWriteExternalStoragePermission() et  private fun requestCameraPermission() */
    private val startActivityForResultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> {
                if (it.resultCode == RESULT_OK) //Si
                {
                    if (it.data != null) // Si il y a une image
                    {
                        val contentURI = it.data!!.data
                        try {
                            val selectedImageBitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                            binding?.ivImage?.setImageBitmap(selectedImageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@AddFavouritePlaces,
                                "Erreur de chargement de l'image depuis la gallerie",
                                Toast.LENGTH_LONG
                            )
                        }


                    }
                }
            })


/* ---------------------- Request read and write storage permissions - END --------------------------------------- */


    /* - Request camera permissions -
   Check permissions pour l'accès à la camera -
       si nok , alors demande des droits d'accès */
    private fun requestCameraPermission() {

        if (hasCameraPermission()) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResultCamera.launch(cameraIntent)
            //TODO ouvrir l'appareil photo

        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cette application à besoin d'accéder à votre camera pour prendre des photos  ",
                CAMERA_REQUEST_CODE,
                Manifest.permission.CAMERA,

                )
        }
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /* ActivityResult  for   private fun requestReadWriteExternalStoragePermission() et  private fun requestCameraPermission() */
    private val startActivityForResultCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> {
                if (it.resultCode == RESULT_OK) //Si
                {
                    if (it.data != null) // Si il y a une image
                    {
                        try {

                            val imageBitmap = it?.data?.extras?.get("data") as Bitmap
                            //  val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                            binding?.ivImage?.setImageBitmap(imageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@AddFavouritePlaces,
                                "Erreur de chargement de la photo depuis la camera",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })


    /* - Request camera permissions - END */

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ///  SettingsDialog.Builder(this).build().show()
            AlertDialog.Builder(this)
                .setMessage("il semble que vous ayez désactivé l'autorisation requise pour cette fonctionnalité. Cette autorisation peut être activée dans les paramètres de l'application")
                .setPositiveButton("GO TO SETTINGS")
                { _, _ ->
                    try {
                        // Renvoyer l'utilisateur vers les parametres de l'application pour permettre à l'utilisateur d'appliquer les droits manuellements
                        // override fun onRequestPermissionsResult which Handle the result of a permission request géré dans les if statements
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uriPackageName = Uri.fromParts("package", packageName, null)
                        intent.data = uriPackageName
                        startActivity(intent)
                    } catch (e: Resources.NotFoundException) {
                        e.printStackTrace()
                    }

                }.setNegativeButton("Annuler") { dialog, which ->
                    dialog.dismiss()
                }.show()
        } else {
            if (requestCode == GALLERY_REQUEST_CODE) {
                requestReadWriteExternalStoragePermission()
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                requestCameraPermission()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "Permissions autorisées", Toast.LENGTH_LONG).show()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )//Handle the result of a permission request
    }
}