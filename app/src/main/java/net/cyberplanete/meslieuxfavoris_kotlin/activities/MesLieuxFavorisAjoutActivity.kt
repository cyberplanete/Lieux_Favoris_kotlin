package net.cyberplanete.meslieuxfavoris_kotlin.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.vmadalin.easypermissions.EasyPermissions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.cyberplanete.meslieuxfavoris_kotlin.R
import net.cyberplanete.meslieuxfavoris_kotlin.database.MesLIeuxFavorisDatabase
import net.cyberplanete.meslieuxfavoris_kotlin.database.MesLieuxFavorisApp
import net.cyberplanete.meslieuxfavoris_kotlin.database.MesLieuxFavorisDAO
import net.cyberplanete.meslieuxfavoris_kotlin.databinding.ActivityAddFavouritePlacesBinding
import net.cyberplanete.meslieuxfavoris_kotlin.models.MesLieuxFavorisEntity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MesLieuxFavorisAjoutActivity : AppCompatActivity(), View.OnClickListener,
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val GALLERY_REQUEST_CODE = 1
        const val CAMERA_REQUEST_CODE = 2
        const val FOLDER_IMAGES_OF_FAVORITES_PLACES = "MesLieuxFavorisImages"
    }
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var saveURIImageToInternalStorage: Uri? = null // Utiliser lors de la sauvegarde du
    private var calendar = Calendar.getInstance() // Instance de Calendar java
    private lateinit var dateListener: DatePickerDialog.OnDateSetListener // Pour l'UI
    private var binding: ActivityAddFavouritePlacesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFavouritePlacesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        /* DATABASE */
        val mesLieuxFavorisDAO =(application as MesLieuxFavorisApp).mesLIeuxFavorisDatabase.mesLieuxFavorisDAO() // Database
        lifecycleScope.launch { mesLieuxFavorisDAO.fetchAllMesLieuxFavoris().collect { val lieuxFavorisList = ArrayList(it) } }
        /* DATABASE */

        /* Pour la bar d'action */
        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Le titre est ajout?? depuis androidManifest.xml
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
        /* END --- Pour la bar d'action */

        /* ------------------------ DatePicker - Choix de la date depuis un calendrier ---------------------------- */
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
        /* --------------------------DatePicker - END ----------------------------*/
        updateDateInView() // Mise ?? jour du champ date automatiquement
        binding?.etDate?.setOnClickListener(this)//


        binding?.tvAddPhoto?.setOnClickListener(this) // Ajouter une image


        binding?.btnSauvegarder?.setOnClickListener(this)  // Sauvegarder le lieu favoris


    }

    /* Centralisation logique onClickListenner*/
    override fun onClick(elementOfView: View?) {
        when (elementOfView!!.id) //
        {
            R.id.et_date -> {
                DatePickerDialog(
                    this@MesLieuxFavorisAjoutActivity,
                    dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_photo -> {
                val imageDialog = AlertDialog.Builder(this)  // Contruction d'une alertDialog
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
            R.id.btn_sauvegarder -> {

                when {
                    binding?.etTitle?.text.isNullOrEmpty() -> { // Verififer que ce champ n'est pas vide
                        Toast.makeText(this, "Please enter  tilte", Toast.LENGTH_LONG).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {// Verififer que ce champ n'est pas vide
                        Toast.makeText(this, "Please enter a description", Toast.LENGTH_LONG).show()
                    }
                    saveURIImageToInternalStorage == null -> {// Verififer que ce champ n'est pas vide
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
                    }
                    binding?.etLocalisation?.text.isNullOrEmpty() -> {// Verififer que ce champ n'est pas vide
                        Toast.makeText(this, "Please enter  tilte", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        val unLieuxFavorisEntity = MesLieuxFavorisEntity(
                            id = 0,
                            binding?.etTitle?.text.toString(),
                            saveURIImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocalisation?.text.toString(), latitude , longitude

                        )
                        var db = MesLIeuxFavorisDatabase.getInstance(this)
                        lifecycleScope.launch { db.mesLieuxFavorisDAO().insert(unLieuxFavorisEntity) }
                        Toast.makeText(this, "Lieu ins??r?? correctement dans la base de donn??es", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }


    /* Centralisation logique onClickListenner  - END */

    /* Methode pour afficher la date dans l'inputtext apr??s l'avoir s??l??ctionner dans le datePicker */
    private fun updateDateInView() {
        val dateFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding?.etDate?.setText(simpleDateFormat.format(calendar.time).toString())

    }
    /* ---------------------- DatePicker END ------------------------*/


    /* ---------------------- Request read and write storage permissions pour choisir une image -
   Check permissions pour l'acc??s aux dossiers et fichiers
       si nok , alors demande des droits d'acc??s
       sinon ouverture du gestionnaire de fichiers pour selectionner une image */

    private fun requestReadWriteExternalStoragePermission() {

        if (hasReadWriteStoragePermission()) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResultGallery.launch(galleryIntent)

        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cette application ?? besoin d'acc??der ?? vos fichiers et dossiers pour permettre la s??lection d'une image ",
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
                            saveURIImageToInternalStorage =
                                saveImageToInternalStorage(selectedImageBitmap)
                            Log.e(
                                "Save image:",
                                "Path :: ${saveURIImageToInternalStorage}"
                            ) // Un log afin de s'assurer de la bonne sauvegarde du fichier
                            binding?.ivImage?.setImageBitmap(selectedImageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@MesLieuxFavorisAjoutActivity,
                                "Erreur de chargement de l'image depuis la gallerie",
                                Toast.LENGTH_LONG
                            )
                        }


                    }
                }
            })


/* ---------------------- Request read and write storage permissions - END --------------------------------------- */


    /* ------------------------- Request camera permissions -   Check permissions pour l'acc??s ?? la camera -  si nok , alors demande des droits d'acc??s */
    private fun requestCameraPermission() {

        if (hasCameraPermission()) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResultCamera.launch(cameraIntent)
            //TODO ouvrir l'appareil photo

        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cette application ?? besoin d'acc??der ?? votre camera pour prendre des photos  ",
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
                            saveURIImageToInternalStorage =
                                saveImageToInternalStorage(imageBitmap) // Sauvegarde de l'image
                            Log.e(
                                "Save image:",
                                "Path :: ${saveURIImageToInternalStorage}"
                            ) // Un log afin de s'assurer de la bonne sauvegarde du fichier

                            //  val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                            binding?.ivImage?.setImageBitmap(imageBitmap) // Affichage de l'image
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@MesLieuxFavorisAjoutActivity,
                                "Erreur de chargement de la photo depuis la camera",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })


    /* -------------- Request camera permissions - END -----------------------*/

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            ///  SettingsDialog.Builder(this).build().show()
            AlertDialog.Builder(this)
                .setMessage("il semble que vous ayez d??sactiv?? l'autorisation requise pour cette fonctionnalit??. Cette autorisation peut ??tre activ??e dans les param??tres de l'application")
                .setPositiveButton("GO TO SETTINGS")
                { _, _ ->
                    try {
                        // Renvoyer l'utilisateur vers les parametres de l'application pour permettre ?? l'utilisateur d'appliquer les droits manuellements
                        // override fun onRequestPermissionsResult which Handle the result of a permission request g??r?? dans les if statements
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
        Toast.makeText(this, "Permissions autoris??es", Toast.LENGTH_LONG).show()

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

    /*-------------------- Methode permettant la sauvegarde de l'image ----------------------------------- */
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        var contexttWrapper =
            ContextWrapper(applicationContext) //Proxying implementation of Context that simply delegates all of its calls to another Context
        var directory =
            contexttWrapper.getDir(//Creation du dossier contenant les images de lieu favoris
                FOLDER_IMAGES_OF_FAVORITES_PLACES,
                Context.MODE_PRIVATE//non accessible depuis d'autres applications
            )
        var file =
            File(directory, "${UUID.randomUUID()}.jpg") /// Unique User ID - Creation du fichier

        try {
            val bitmapFile: OutputStream = FileOutputStream(file) // Preparation pour la compression
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapFile)
            bitmapFile.flush() // Quand la compression est termin??e
            bitmapFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath) // Utiliser pour Un log terminal afin de s'assurer de la bonne sauvegarde du fichier
    }


}