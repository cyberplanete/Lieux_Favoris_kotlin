package net.cyberplanete.meslieuxfavoris_kotlin.database

import android.app.Application

class MesLieuxFavorisApp : Application() {
    /*
    * application class and initialize the database
    * */
    val mesLIeuxFavorisDatabase by lazy { MesLIeuxFavorisDatabase.getInstance(this) }
}